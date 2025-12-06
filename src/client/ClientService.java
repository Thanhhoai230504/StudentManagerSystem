package client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.*;
import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Map;

/**
 * Service kết nối đến Server từ phía Client
 * Sử dụng Socket để giao tiếp với Server
 */
public class ClientService {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8888;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Gson gson;
    private boolean isConnected = false;
    
    public ClientService() {
        this.gson = new Gson();
    }
    
    /**
     * Kết nối đến Server
     */
    public boolean connect() {
        try {
            socket = new Socket(SERVER_HOST, SERVER_PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            isConnected = true;
            System.out.println("✓ Đã kết nối đến Server: " + SERVER_HOST + ":" + SERVER_PORT);
            return true;
        } catch (IOException e) {
            System.err.println("✗ Không thể kết nối đến Server: " + e.getMessage());
            isConnected = false;
            return false;
        }
    }
    
    /**
     * Ngắt kết nối
     */
    public void disconnect() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
            isConnected = false;
            System.out.println("✓ Đã ngắt kết nối Server");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Kiểm tra kết nối
     */
    public boolean isConnected() {
        return isConnected && socket != null && !socket.isClosed();
    }
    
    /**
     * Gửi request đến Server và nhận response
     */
    private Response sendRequest(String action, Object data) throws IOException {
        if (!isConnected()) {
            throw new IOException("Chưa kết nối đến Server!");
        }
        
        // Tạo request
        Request request = new Request();
        request.setAction(action);
        request.setData(data);
        
        // Gửi request
        String requestJson = gson.toJson(request);
        out.println(requestJson);
        
        // Nhận response
        String responseJson = in.readLine();
        return gson.fromJson(responseJson, Response.class);
    }
    
    // ============ STUDENT OPERATIONS ============
    
    public List<SinhVien> layTatCaSinhVien() throws IOException {
        Response res = sendRequest("GET_ALL_STUDENTS", null);
        if (res.isSuccess()) {
            return gson.fromJson(gson.toJson(res.getData()), 
                new TypeToken<List<SinhVien>>(){}.getType());
        }
        return null;
    }
    
    public boolean themSinhVien(SinhVien sv) throws IOException {
        Response res = sendRequest("ADD_STUDENT", sv);
        return res.isSuccess();
    }
    
    public boolean capNhatSinhVien(SinhVien sv) throws IOException {
        Response res = sendRequest("UPDATE_STUDENT", sv);
        return res.isSuccess();
    }
    
    public boolean xoaSinhVien(int id, String mssv) throws IOException {
        Map<String, Object> data = Map.of("id", id, "mssv", mssv);
        Response res = sendRequest("DELETE_STUDENT", data);
        return res.isSuccess();
    }
    
    public List<SinhVien> timKiemTheoTen(String ten) throws IOException {
        Response res = sendRequest("SEARCH_STUDENT_BY_NAME", ten);
        if (res.isSuccess()) {
            return gson.fromJson(gson.toJson(res.getData()), 
                new TypeToken<List<SinhVien>>(){}.getType());
        }
        return null;
    }
    
    public SinhVien timTheoMSSV(String mssv) throws IOException {
        Response res = sendRequest("SEARCH_STUDENT_BY_MSSV", mssv);
        if (res.isSuccess() && res.getData() != null) {
            return gson.fromJson(gson.toJson(res.getData()), SinhVien.class);
        }
        return null;
    }
    
    public List<SinhVien> laySinhVienTheoLop(String maLop) throws IOException {
        Response res = sendRequest("GET_STUDENTS_BY_CLASS", maLop);
        if (res.isSuccess()) {
            return gson.fromJson(gson.toJson(res.getData()), 
                new TypeToken<List<SinhVien>>(){}.getType());
        }
        return null;
    }
    
    // ============ CLASS OPERATIONS ============
    
    public List<Lop> layTatCaLop() throws IOException {
        Response res = sendRequest("GET_ALL_CLASSES", null);
        if (res.isSuccess()) {
            return gson.fromJson(gson.toJson(res.getData()), 
                new TypeToken<List<Lop>>(){}.getType());
        }
        return null;
    }
    
    public boolean themLop(Lop lop) throws IOException {
        Response res = sendRequest("ADD_CLASS", lop);
        return res.isSuccess();
    }
    
    public boolean capNhatLop(Lop lop) throws IOException {
        Response res = sendRequest("UPDATE_CLASS", lop);
        return res.isSuccess();
    }
    
    public boolean xoaLop(String maLop) throws IOException {
        Response res = sendRequest("DELETE_CLASS", maLop);
        return res.isSuccess();
    }
    
    // ============ SCORE OPERATIONS ============
    
    public List<Diem> layTatCaDiem() throws IOException {
        Response res = sendRequest("GET_ALL_SCORES", null);
        if (res.isSuccess()) {
            return gson.fromJson(gson.toJson(res.getData()), 
                new TypeToken<List<Diem>>(){}.getType());
        }
        return null;
    }
    
    public boolean themDiem(Diem diem) throws IOException {
        Response res = sendRequest("ADD_SCORE", diem);
        return res.isSuccess();
    }
    
    public boolean capNhatDiem(Diem diem) throws IOException {
        Response res = sendRequest("UPDATE_SCORE", diem);
        return res.isSuccess();
    }
    
    public boolean xoaDiem(int id) throws IOException {
        Response res = sendRequest("DELETE_SCORE", id);
        return res.isSuccess();
    }
    
    public List<Diem> layDiemTheoSinhVien(int idSinhVien) throws IOException {
        Response res = sendRequest("GET_SCORES_BY_STUDENT", idSinhVien);
        if (res.isSuccess()) {
            return gson.fromJson(gson.toJson(res.getData()), 
                new TypeToken<List<Diem>>(){}.getType());
        }
        return null;
    }
    
    // ============ REPORT OPERATIONS ============
    
    public Map<String, Integer> thongKeSoLuongTheoLop() throws IOException {
        Response res = sendRequest("GET_CLASS_STATS", null);
        if (res.isSuccess()) {
            return gson.fromJson(gson.toJson(res.getData()), 
                new TypeToken<Map<String, Integer>>(){}.getType());
        }
        return null;
    }
    
    public Map<String, Double> tinhDiemTrungBinhTheoLop() throws IOException {
        Response res = sendRequest("GET_SCORE_AVG_BY_CLASS", null);
        if (res.isSuccess()) {
            return gson.fromJson(gson.toJson(res.getData()), 
                new TypeToken<Map<String, Double>>(){}.getType());
        }
        return null;
    }
    
    public Map<String, Integer> thongKeTheoGioiTinh() throws IOException {
        Response res = sendRequest("GET_GENDER_STATS", null);
        if (res.isSuccess()) {
            return gson.fromJson(gson.toJson(res.getData()), 
                new TypeToken<Map<String, Integer>>(){}.getType());
        }
        return null;
    }
    
    public boolean xuatBaoCaoJSON() throws IOException {
        Response res = sendRequest("EXPORT_JSON_REPORT", null);
        return res.isSuccess();
    }
    
    // ============ JSON OPERATIONS ============
    
    public boolean dongBoJSON() throws IOException {
        Response res = sendRequest("SYNC_JSON", null);
        return res.isSuccess();
    }
    
    public List<SinhVien> layDuLieuJSON() throws IOException {
        Response res = sendRequest("GET_JSON_DATA", null);
        if (res.isSuccess()) {
            return gson.fromJson(gson.toJson(res.getData()), 
                new TypeToken<List<SinhVien>>(){}.getType());
        }
        return null;
    }
}

/**
 * Class Request
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
 * Class Response
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