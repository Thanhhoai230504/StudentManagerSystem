package dao;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import model.SinhVien;
import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO xử lý dữ liệu NoSQL (JSON)
 * Lưu trữ bản sao dữ liệu sinh viên dưới dạng JSON
 */
public class JsonStudentDAO {
    private static final String JSON_FILE = "data/students.json";
    private static final String REPORT_FILE = "data/report_full.json";
    private Gson gson;
    
    public JsonStudentDAO() {
        // Cấu hình Gson để xử lý LocalDate
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();
        
        // Tạo thư mục data nếu chưa tồn tại
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
    }
    
    /**
     * Lưu danh sách sinh viên vào file JSON
     */
    public boolean luuVaoJSON(List<SinhVien> danhSach) {
        try (Writer writer = new FileWriter(JSON_FILE)) {
            gson.toJson(danhSach, writer);
            System.out.println("Đã lưu " + danhSach.size() + " sinh viên vào JSON");
            return true;
        } catch (IOException e) {
            System.err.println("Lỗi ghi file JSON: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Xuất báo cáo đầy đủ (sinh viên + điểm + thống kê)
     */
    public boolean xuatBaoCaoChiTiet() {
        try {
            // Lấy dữ liệu từ các DAO
            StudentDAO studentDAO = new StudentDAO();
            ClassDAO classDAO = new ClassDAO();
            ScoreDAO scoreDAO = new ScoreDAO();
            
            // Tạo object báo cáo
            Map<String, Object> baoCao = new HashMap<>();
            
            // 1. Metadata
            Map<String, String> metadata = new HashMap<>();
            metadata.put("tieu_de", "Báo cáo Hệ thống Quản lý Sinh viên");
            metadata.put("ngay_xuat", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            metadata.put("phien_ban", "1.0");
            metadata.put("he_thong", "Student Manager System");
            baoCao.put("metadata", metadata);
            
            // 2. Tổng quan
            Map<String, Object> tongQuan = new HashMap<>();
            int tongSV = studentDAO.demTongSinhVien();
            int tongLop = classDAO.demTongLop();
            
            tongQuan.put("tong_sinh_vien", tongSV);
            tongQuan.put("tong_lop", tongLop);
            tongQuan.put("si_so_trung_binh", tongLop > 0 ? (double) tongSV / tongLop : 0);
            baoCao.put("tong_quan", tongQuan);
            
            // 3. Danh sách sinh viên đầy đủ
            List<Map<String, Object>> danhSachSV = new ArrayList<>();
            List<SinhVien> students = studentDAO.layTatCaSinhVien();
            
            for (SinhVien sv : students) {
                Map<String, Object> svData = new HashMap<>();
                svData.put("id", sv.getId());
                svData.put("mssv", sv.getMssv());
                svData.put("ho_ten", sv.getHoTen());
                svData.put("ngay_sinh", sv.getNgaySinh().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                svData.put("gioi_tinh", sv.getGioiTinh());
                svData.put("ma_lop", sv.getMaLop());
                
                // Lấy điểm của sinh viên
                List<Map<String, Object>> diemList = new ArrayList<>();
                List<model.Diem> diemSV = scoreDAO.layDiemTheoSinhVien(sv.getId());
                double tongDiem = 0;
                
                for (model.Diem diem : diemSV) {
                    Map<String, Object> diemData = new HashMap<>();
                    diemData.put("mon_hoc", diem.getMonHoc());
                    diemData.put("diem_qua_trinh", diem.getDiemQuaTrinh());
                    diemData.put("diem_thi", diem.getDiemThi());
                    diemData.put("diem_tong_ket", diem.getDiemTongKet());
                    diemData.put("xep_loai", getXepLoai(diem.getDiemTongKet()));
                    diemList.add(diemData);
                    tongDiem += diem.getDiemTongKet();
                }
                
                svData.put("diem_so", diemList);
                svData.put("so_mon_hoc", diemList.size());
                
                if (!diemList.isEmpty()) {
                    double diemTB = tongDiem / diemList.size();
                    svData.put("diem_trung_binh", Math.round(diemTB * 100.0) / 100.0);
                    svData.put("xep_loai_chung", getXepLoai(diemTB));
                } else {
                    svData.put("diem_trung_binh", 0);
                    svData.put("xep_loai_chung", "Chưa có điểm");
                }
                
                danhSachSV.add(svData);
            }
            baoCao.put("danh_sach_sinh_vien", danhSachSV);
            
            // 4. Thống kê theo lớp
            List<Map<String, Object>> thongKeLop = new ArrayList<>();
            List<model.Lop> danhSachLop = classDAO.layTatCaLop();
            
            for (model.Lop lop : danhSachLop) {
                Map<String, Object> lopData = new HashMap<>();
                lopData.put("ma_lop", lop.getMaLop());
                lopData.put("ten_lop", lop.getTenLop());
                lopData.put("si_so", lop.getSiSo());
                
                // Tính điểm TB của lớp
                Map<String, Double> diemTBLop = scoreDAO.tinhDiemTrungBinhTheoLop();
                if (diemTBLop.containsKey(lop.getMaLop())) {
                    double dtb = diemTBLop.get(lop.getMaLop());
                    lopData.put("diem_trung_binh", Math.round(dtb * 100.0) / 100.0);
                    lopData.put("xep_loai", getXepLoai(dtb));
                } else {
                    lopData.put("diem_trung_binh", 0);
                    lopData.put("xep_loai", "Chưa có điểm");
                }
                
                // Thống kê giới tính trong lớp
                List<SinhVien> svLop = studentDAO.laySinhVienTheoLop(lop.getMaLop());
                int nam = 0, nu = 0;
                for (SinhVien sv : svLop) {
                    if ("Nam".equals(sv.getGioiTinh())) nam++;
                    else nu++;
                }
                
                Map<String, Integer> gioiTinh = new HashMap<>();
                gioiTinh.put("nam", nam);
                gioiTinh.put("nu", nu);
                lopData.put("gioi_tinh", gioiTinh);
                
                thongKeLop.add(lopData);
            }
            baoCao.put("thong_ke_theo_lop", thongKeLop);
            
            // 5. Thống kê điểm theo môn học
            List<Map<String, Object>> thongKeMonHoc = new ArrayList<>();
            List<String> danhSachMon = scoreDAO.layDanhSachMonHoc();
            
            for (String monHoc : danhSachMon) {
                Map<String, Object> monData = new HashMap<>();
                monData.put("mon_hoc", monHoc);
                
                List<model.Diem> diemMon = scoreDAO.layDiemTheoMonHoc(monHoc);
                monData.put("so_sinh_vien", diemMon.size());
                
                if (!diemMon.isEmpty()) {
                    double tongDiem = diemMon.stream().mapToDouble(model.Diem::getDiemTongKet).sum();
                    double diemTB = tongDiem / diemMon.size();
                    monData.put("diem_trung_binh", Math.round(diemTB * 100.0) / 100.0);
                    monData.put("diem_cao_nhat", scoreDAO.layDiemCaoNhat(monHoc));
                    monData.put("diem_thap_nhat", scoreDAO.layDiemThapNhat(monHoc));
                    
                    // Đếm số sinh viên theo xếp loại
                    int gioi = 0, kha = 0, tb = 0, yeu = 0;
                    for (model.Diem d : diemMon) {
                        double diem = d.getDiemTongKet();
                        if (diem >= 8.0) gioi++;
                        else if (diem >= 6.5) kha++;
                        else if (diem >= 5.0) tb++;
                        else yeu++;
                    }
                    
                    Map<String, Integer> xepLoai = new HashMap<>();
                    xepLoai.put("gioi", gioi);
                    xepLoai.put("kha", kha);
                    xepLoai.put("trung_binh", tb);
                    xepLoai.put("yeu", yeu);
                    monData.put("phan_loai", xepLoai);
                }
                
                thongKeMonHoc.add(monData);
            }
            baoCao.put("thong_ke_theo_mon_hoc", thongKeMonHoc);
            
            // 6. Thống kê tổng hợp
            Map<String, Object> thongKeTongHop = new HashMap<>();
            
            // Giới tính
            int tongNam = 0, tongNu = 0;
            for (SinhVien sv : students) {
                if ("Nam".equals(sv.getGioiTinh())) tongNam++;
                else tongNu++;
            }
            Map<String, Object> gioiTinhStats = new HashMap<>();
            gioiTinhStats.put("nam", tongNam);
            gioiTinhStats.put("nu", tongNu);
            gioiTinhStats.put("ty_le_nam", tongSV > 0 ? Math.round((tongNam * 100.0 / tongSV) * 10.0) / 10.0 : 0);
            gioiTinhStats.put("ty_le_nu", tongSV > 0 ? Math.round((tongNu * 100.0 / tongSV) * 10.0) / 10.0 : 0);
            thongKeTongHop.put("gioi_tinh", gioiTinhStats);
            
            // Xếp loại chung
            Map<String, Integer> xepLoaiChung = scoreDAO.thongKeTheoXepLoai();
            thongKeTongHop.put("xep_loai_chung", xepLoaiChung);
            
            // Điểm TB toàn trường
            Map<String, Double> diemTBToanTruong = scoreDAO.tinhDiemTrungBinhTheoLop();
            if (!diemTBToanTruong.isEmpty()) {
                double tongDiemTB = diemTBToanTruong.values().stream().mapToDouble(Double::doubleValue).sum();
                double diemTBChung = tongDiemTB / diemTBToanTruong.size();
                thongKeTongHop.put("diem_trung_binh_toan_truong", Math.round(diemTBChung * 100.0) / 100.0);
            }
            
            baoCao.put("thong_ke_tong_hop", thongKeTongHop);
            
            // Ghi vào file
            try (Writer writer = new FileWriter(REPORT_FILE)) {
                gson.toJson(baoCao, writer);
                System.out.println("✓ Đã xuất báo cáo chi tiết vào: " + REPORT_FILE);
                return true;
            }
            
        } catch (Exception e) {
            System.err.println("✗ Lỗi xuất báo cáo chi tiết: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Đọc danh sách sinh viên từ file JSON
     */
    public List<SinhVien> docTuJSON() {
        File file = new File(JSON_FILE);
        if (!file.exists()) {
            System.out.println("File JSON chưa tồn tại, trả về danh sách rỗng");
            return new ArrayList<>();
        }
        
        try (Reader reader = new FileReader(JSON_FILE)) {
            Type listType = new TypeToken<ArrayList<SinhVien>>(){}.getType();
            List<SinhVien> danhSach = gson.fromJson(reader, listType);
            
            if (danhSach == null) {
                return new ArrayList<>();
            }
            
            System.out.println("Đã đọc " + danhSach.size() + " sinh viên từ JSON");
            return danhSach;
        } catch (IOException e) {
            System.err.println("Lỗi đọc file JSON: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Thêm 1 sinh viên vào JSON
     */
    public boolean themSinhVien(SinhVien sv) {
        List<SinhVien> danhSach = docTuJSON();
        danhSach.add(sv);
        return luuVaoJSON(danhSach);
    }
    
    /**
     * Cập nhật sinh viên trong JSON
     */
    public boolean capNhatSinhVien(SinhVien svMoi) {
        List<SinhVien> danhSach = docTuJSON();
        for (int i = 0; i < danhSach.size(); i++) {
            if (danhSach.get(i).getMssv().equals(svMoi.getMssv())) {
                danhSach.set(i, svMoi);
                return luuVaoJSON(danhSach);
            }
        }
        return false;
    }
    
    /**
     * Xóa sinh viên khỏi JSON
     */
    public boolean xoaSinhVien(String mssv) {
        List<SinhVien> danhSach = docTuJSON();
        boolean removed = danhSach.removeIf(sv -> sv.getMssv().equals(mssv));
        if (removed) {
            return luuVaoJSON(danhSach);
        }
        return false;
    }
    
    /**
     * Đồng bộ từ MySQL sang JSON
     */
    public boolean dongBoTuMySQL() {
        StudentDAO sqlDAO = new StudentDAO();
        List<SinhVien> danhSach = sqlDAO.layTatCaSinhVien();
        return luuVaoJSON(danhSach);
    }
    
    /**
     * Tìm sinh viên theo MSSV trong JSON
     */
    public SinhVien timTheoMSSV(String mssv) {
        List<SinhVien> danhSach = docTuJSON();
        for (SinhVien sv : danhSach) {
            if (sv.getMssv().equals(mssv)) {
                return sv;
            }
        }
        return null;
    }
    
    /**
     * Xếp loại theo điểm
     */
    private String getXepLoai(double diem) {
        if (diem >= 8.0) return "Giỏi";
        if (diem >= 6.5) return "Khá";
        if (diem >= 5.0) return "Trung bình";
        return "Yếu";
    }
}

