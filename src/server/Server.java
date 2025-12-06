package server;

import com.google.gson.Gson;
import dao.*;
import model.*;
import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Server xử lý kết nối từ Client qua Socket
 * Port: 8888
 */
public class Server {
    private static final int PORT = 8888;
    private ServerSocket serverSocket;
    private boolean isRunning = true;
    private Gson gson;
    
    // DAOs
    private StudentDAO studentDAO;
    private ClassDAO classDAO;
    private ScoreDAO scoreDAO;
    private JsonStudentDAO jsonDAO;
    
    public Server() {
        this.gson = new Gson();
        this.studentDAO = new StudentDAO();
        this.classDAO = new ClassDAO();
        this.scoreDAO = new ScoreDAO();
        this.jsonDAO = new JsonStudentDAO();
    }
    
    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("✓ Server đã khởi động trên port " + PORT);
            System.out.println("⏳ Đang chờ kết nối từ Client...\n");
            
            while (isRunning) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("✓ Client kết nối: " + clientSocket.getInetAddress());
                
                // Tạo thread mới cho mỗi client
                ClientHandler handler = new ClientHandler(clientSocket);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            System.err.println("Lỗi Server: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void stop() {
        isRunning = false;
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
            DatabaseConnection.closeConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Thread xử lý từng client
     */
    private class ClientHandler implements Runnable {
        private Socket clientSocket;
        private BufferedReader in;
        private PrintWriter out;
        
        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }
        
        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                
                String request;
                while ((request = in.readLine()) != null) {
                    System.out.println("📨 Request: " + request);
                    
                    String response = handleRequest(request);
                    out.println(response);
                    
                    System.out.println("📤 Response sent\n");
                }
            } catch (IOException e) {
                System.err.println("Lỗi xử lý client: " + e.getMessage());
            } finally {
                closeConnection();
            }
        }
        
        private String handleRequest(String request) {
            try {
                Request req = gson.fromJson(request, Request.class);
                Response res = new Response();
                
                switch (req.getAction()) {
                    // === STUDENT OPERATIONS ===
                    case "GET_ALL_STUDENTS":
                        List<SinhVien> students = studentDAO.layTatCaSinhVien();
                        res.setSuccess(true);
                        res.setData(students);
                        res.setMessage("Lấy danh sách thành công");
                        break;
                        
                    case "ADD_STUDENT":
                        SinhVien newStudent = gson.fromJson(gson.toJson(req.getData()), SinhVien.class);
                        boolean addSuccess = studentDAO.themSinhVien(newStudent);
                        if (addSuccess) {
                            jsonDAO.dongBoTuMySQL(); // Đồng bộ JSON
                        }
                        res.setSuccess(addSuccess);
                        res.setMessage(addSuccess ? "Thêm thành công" : "Thêm thất bại");
                        break;
                        
                    case "UPDATE_STUDENT":
                        SinhVien updateStudent = gson.fromJson(gson.toJson(req.getData()), SinhVien.class);
                        boolean updateSuccess = studentDAO.capNhatSinhVien(updateStudent);
                        if (updateSuccess) {
                            jsonDAO.dongBoTuMySQL();
                        }
                        res.setSuccess(updateSuccess);
                        res.setMessage(updateSuccess ? "Cập nhật thành công" : "Cập nhật thất bại");
                        break;
                        
                    case "DELETE_STUDENT":
                        Map<String, Object> deleteData = (Map<String, Object>) req.getData();
                        int deleteId = ((Double) deleteData.get("id")).intValue();
                        String deleteMssv = (String) deleteData.get("mssv");
                        boolean deleteSuccess = studentDAO.xoaSinhVien(deleteId);
                        if (deleteSuccess) {
                            jsonDAO.xoaSinhVien(deleteMssv);
                        }
                        res.setSuccess(deleteSuccess);
                        res.setMessage(deleteSuccess ? "Xóa thành công" : "Xóa thất bại");
                        break;
                        
                    case "SEARCH_STUDENT_BY_NAME":
                        String name = (String) req.getData();
                        List<SinhVien> searchResults = studentDAO.timKiemTheoTen(name);
                        res.setSuccess(true);
                        res.setData(searchResults);
                        break;
                        
                    case "SEARCH_STUDENT_BY_MSSV":
                        String mssv = (String) req.getData();
                        SinhVien student = studentDAO.timTheoMSSV(mssv);
                        res.setSuccess(student != null);
                        res.setData(student);
                        break;
                        
                    case "GET_STUDENTS_BY_CLASS":
                        String maLop = (String) req.getData();
                        List<SinhVien> classStudents = studentDAO.laySinhVienTheoLop(maLop);
                        res.setSuccess(true);
                        res.setData(classStudents);
                        break;
                        
                    // === CLASS OPERATIONS ===
                    case "GET_ALL_CLASSES":
                        List<Lop> classes = classDAO.layTatCaLop();
                        res.setSuccess(true);
                        res.setData(classes);
                        break;
                        
                    case "ADD_CLASS":
                        Lop newClass = gson.fromJson(gson.toJson(req.getData()), Lop.class);
                        boolean addClassSuccess = classDAO.themLop(newClass);
                        res.setSuccess(addClassSuccess);
                        res.setMessage(addClassSuccess ? "Thêm lớp thành công" : "Thêm lớp thất bại");
                        break;
                        
                    case "UPDATE_CLASS":
                        Lop updateClass = gson.fromJson(gson.toJson(req.getData()), Lop.class);
                        boolean updateClassSuccess = classDAO.capNhatLop(updateClass);
                        res.setSuccess(updateClassSuccess);
                        res.setMessage(updateClassSuccess ? "Cập nhật thành công" : "Cập nhật thất bại");
                        break;
                        
                    case "DELETE_CLASS":
                        String deleteMaLop = (String) req.getData();
                        boolean deleteClassSuccess = classDAO.xoaLop(deleteMaLop);
                        res.setSuccess(deleteClassSuccess);
                        res.setMessage(deleteClassSuccess ? "Xóa thành công" : "Xóa thất bại");
                        break;
                        
                    // === SCORE OPERATIONS ===
                    case "GET_ALL_SCORES":
                        List<Diem> scores = scoreDAO.layTatCaDiem();
                        res.setSuccess(true);
                        res.setData(scores);
                        break;
                        
                    case "ADD_SCORE":
                        Diem newScore = gson.fromJson(gson.toJson(req.getData()), Diem.class);
                        boolean addScoreSuccess = scoreDAO.themDiem(newScore);
                        res.setSuccess(addScoreSuccess);
                        res.setMessage(addScoreSuccess ? "Thêm điểm thành công" : "Thêm điểm thất bại");
                        break;
                        
                    case "UPDATE_SCORE":
                        Diem updateScore = gson.fromJson(gson.toJson(req.getData()), Diem.class);
                        boolean updateScoreSuccess = scoreDAO.capNhatDiem(updateScore);
                        res.setSuccess(updateScoreSuccess);
                        res.setMessage(updateScoreSuccess ? "Cập nhật thành công" : "Cập nhật thất bại");
                        break;
                        
                    case "DELETE_SCORE":
                        int scoreId = ((Double) req.getData()).intValue();
                        boolean deleteScoreSuccess = scoreDAO.xoaDiem(scoreId);
                        res.setSuccess(deleteScoreSuccess);
                        res.setMessage(deleteScoreSuccess ? "Xóa thành công" : "Xóa thất bại");
                        break;
                        
                    case "GET_SCORES_BY_STUDENT":
                        int studentId = ((Double) req.getData()).intValue();
                        List<Diem> studentScores = scoreDAO.layDiemTheoSinhVien(studentId);
                        res.setSuccess(true);
                        res.setData(studentScores);
                        break;
                        
                    // === REPORT OPERATIONS ===
                    case "GET_CLASS_STATS":
                        Map<String, Integer> classStats = new HashMap<>();
                        List<Lop> allClasses = classDAO.layTatCaLop();
                        for (Lop lop : allClasses) {
                            classStats.put(lop.getMaLop() + " - " + lop.getTenLop(), lop.getSiSo());
                        }
                        res.setSuccess(true);
                        res.setData(classStats);
                        break;
                        
                    case "GET_SCORE_AVG_BY_CLASS":
                        Map<String, Double> scoreAvg = scoreDAO.tinhDiemTrungBinhTheoLop();
                        res.setSuccess(true);
                        res.setData(scoreAvg);
                        break;
                        
                    case "GET_GENDER_STATS":
                        List<SinhVien> allStudents = studentDAO.layTatCaSinhVien();
                        int nam = 0, nu = 0;
                        for (SinhVien sv : allStudents) {
                            if ("Nam".equals(sv.getGioiTinh())) nam++;
                            else nu++;
                        }
                        Map<String, Integer> genderStats = new HashMap<>();
                        genderStats.put("Nam", nam);
                        genderStats.put("Nữ", nu);
                        res.setSuccess(true);
                        res.setData(genderStats);
                        break;
                        
                    case "EXPORT_JSON_REPORT":
                        boolean exportSuccess = jsonDAO.xuatBaoCaoChiTiet();
                        if (exportSuccess) {
                            jsonDAO.dongBoTuMySQL();
                        }
                        res.setSuccess(exportSuccess);
                        res.setMessage(exportSuccess ? "Xuất báo cáo thành công" : "Xuất báo cáo thất bại");
                        break;
                        
                    // === JSON OPERATIONS ===
                    case "SYNC_JSON":
                        boolean syncSuccess = jsonDAO.dongBoTuMySQL();
                        res.setSuccess(syncSuccess);
                        res.setMessage(syncSuccess ? "Đồng bộ thành công" : "Đồng bộ thất bại");
                        break;
                        
                    case "GET_JSON_DATA":
                        List<SinhVien> jsonData = jsonDAO.docTuJSON();
                        res.setSuccess(true);
                        res.setData(jsonData);
                        break;
                        
                    default:
                        res.setSuccess(false);
                        res.setMessage("Action không hợp lệ: " + req.getAction());
                }
                
                return gson.toJson(res);
                
            } catch (Exception e) {
                Response errorRes = new Response();
                errorRes.setSuccess(false);
                errorRes.setMessage("Lỗi server: " + e.getMessage());
                e.printStackTrace();
                return gson.toJson(errorRes);
            }
        }
        
        private void closeConnection() {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                if (clientSocket != null) clientSocket.close();
                System.out.println("✗ Client đã ngắt kết nối\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void main(String[] args) {
        // Kiểm tra kết nối database
        if (!DatabaseConnection.testConnection()) {
            System.err.println("✗ Không thể kết nối MySQL!");
            return;
        }
        
        Server server = new Server();
        
        // Shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n⏹ Đang tắt Server...");
            server.stop();
            System.out.println("✓ Server đã tắt an toàn");
        }));
        
        server.start();
    }
}

/**
 * Class đại diện cho Request từ Client
 */
class Request {
    private String action;
    private Object data;
    
    public String getAction() {
        return action;
    }
    
    public void setAction(String action) {
        this.action = action;
    }
    
    public Object getData() {
        return data;
    }
    
    public void setData(Object data) {
        this.data = data;
    }
}

/**
 * Class đại diện cho Response từ Server
 */
class Response {
    private boolean success;
    private String message;
    private Object data;
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public Object getData() {
        return data;
    }
    
    public void setData(Object data) {
        this.data = data;
    }
}