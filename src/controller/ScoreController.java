package controller;

import dao.ScoreDAO;
import model.Diem;
import java.util.List;
import java.util.Map;

/**
 * Controller xử lý logic nghiệp vụ cho Điểm
 */
public class ScoreController {
    private ScoreDAO dao;
    
    public ScoreController() {
        this.dao = new ScoreDAO();
    }
    
    public boolean themDiem(Diem diem) {
        return dao.themDiem(diem);
    }
    
    public boolean capNhatDiem(Diem diem) {
        return dao.capNhatDiem(diem);
    }
    
    public boolean xoaDiem(int id) {
        return dao.xoaDiem(id);
    }
    
    public List<Diem> layDiemTheoSinhVien(int idSinhVien) {
        return dao.layDiemTheoSinhVien(idSinhVien);
    }
    
    public List<Diem> layTatCaDiem() {
        return dao.layTatCaDiem();
    }
    
    public Map<String, Double> tinhDiemTrungBinhTheoLop() {
        return dao.tinhDiemTrungBinhTheoLop();
    }
}
