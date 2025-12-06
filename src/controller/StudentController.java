package controller;

import dao.StudentDAO;
import dao.JsonStudentDAO;
import model.SinhVien;
import java.util.List;

/**
 * Controller xử lý logic nghiệp vụ cho Sinh viên
 */
public class StudentController {
    private StudentDAO sqlDAO;
    private JsonStudentDAO jsonDAO;
    
    public StudentController() {
        this.sqlDAO = new StudentDAO();
        this.jsonDAO = new JsonStudentDAO();
    }
    
    public boolean themSinhVien(SinhVien sv) {
        boolean success = sqlDAO.themSinhVien(sv);
        if (success) {
            jsonDAO.dongBoTuMySQL(); // Đồng bộ sang JSON
        }
        return success;
    }
    
    public boolean capNhatSinhVien(SinhVien sv) {
        boolean success = sqlDAO.capNhatSinhVien(sv);
        if (success) {
            jsonDAO.dongBoTuMySQL();
        }
        return success;
    }
    
    public boolean xoaSinhVien(int id, String mssv) {
        boolean success = sqlDAO.xoaSinhVien(id);
        if (success) {
            jsonDAO.xoaSinhVien(mssv);
        }
        return success;
    }
    
    public List<SinhVien> layTatCaSinhVien() {
        return sqlDAO.layTatCaSinhVien();
    }
    
    public SinhVien timTheoMSSV(String mssv) {
        return sqlDAO.timTheoMSSV(mssv);
    }
    
    public List<SinhVien> timKiemTheoTen(String ten) {
        return sqlDAO.timKiemTheoTen(ten);
    }
    
    public List<SinhVien> laySinhVienTheoLop(String maLop) {
        return sqlDAO.laySinhVienTheoLop(maLop);
    }
    
    public List<SinhVien> laySinhVienTheoGioiTinh(String gioiTinh) {
        return sqlDAO.laySinhVienTheoGioiTinh(gioiTinh);
    }
    
    public List<SinhVien> layDuLieuJSON() {
        return jsonDAO.docTuJSON();
    }
    
    public boolean dongBoJSON() {
        return jsonDAO.dongBoTuMySQL();
    }
}