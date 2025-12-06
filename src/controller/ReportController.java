package controller;

import dao.StudentDAO;
import dao.ClassDAO;
import dao.ScoreDAO;
import dao.JsonStudentDAO; // BỎ COMMENT - ĐÃ THÊM GSON RỒI!
import model.SinhVien;
import model.Lop;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Controller xử lý báo cáo và thống kê
 */
public class ReportController {
    private StudentDAO studentDAO;
    private ClassDAO classDAO;
    private ScoreDAO scoreDAO;
    private JsonStudentDAO jsonDAO; // BỎ COMMENT - ĐÃ THÊM GSON RỒI!
    
    public ReportController() {
        this.studentDAO = new StudentDAO();
        this.classDAO = new ClassDAO();
        this.scoreDAO = new ScoreDAO();
        this.jsonDAO = new JsonStudentDAO(); // BỎ COMMENT - ĐÃ THÊM GSON RỒI!
    }
    
    /**
     * Thống kê số lượng sinh viên theo lớp
     */
    public Map<String, Integer> thongKeSoLuongTheoLop() {
        Map<String, Integer> ketQua = new HashMap<>();
        List<Lop> danhSachLop = classDAO.layTatCaLop();
        
        for (Lop lop : danhSachLop) {
            ketQua.put(lop.getMaLop() + " - " + lop.getTenLop(), lop.getSiSo());
        }
        
        return ketQua;
    }
    
    /**
     * Thống kê điểm trung bình theo lớp
     */
    public Map<String, Double> thongKeDiemTrungBinhTheoLop() {
        return scoreDAO.tinhDiemTrungBinhTheoLop();
    }
    
    /**
     * Thống kê theo giới tính
     */
    public Map<String, Integer> thongKeTheoGioiTinh() {
        Map<String, Integer> ketQua = new HashMap<>();
        List<SinhVien> tatCa = studentDAO.layTatCaSinhVien();
        
        int nam = 0, nu = 0;
        for (SinhVien sv : tatCa) {
            if ("Nam".equalsIgnoreCase(sv.getGioiTinh())) {
                nam++;
            } else {
                nu++;
            }
        }
        
        ketQua.put("Nam", nam);
        ketQua.put("Nữ", nu);
        return ketQua;
    }
    
    /**
     * Xuất báo cáo JSON đầy đủ
     */
    public boolean xuatBaoCaoJSON() {
        // Xuất báo cáo chi tiết
        boolean result = jsonDAO.xuatBaoCaoChiTiet();
        
        // Đồng bộ danh sách sinh viên đơn giản
        if (result) {
            jsonDAO.dongBoTuMySQL();
        }
        
        return result;
    }
}