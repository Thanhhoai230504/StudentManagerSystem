package controller;

import client.ClientService;
import model.Diem;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ScoreController - Phía Client
 */
public class ScoreController {  // ĐỔI từ "class" thành "public class"
    private ClientService clientService;
    
    public ScoreController(ClientService clientService) {
        this.clientService = clientService;
    }
    
    public boolean themDiem(Diem diem) {
        try {
            return clientService.themDiem(diem);
        } catch (IOException e) {
            handleError("Lỗi thêm điểm", e);
            return false;
        }
    }
    
    public boolean capNhatDiem(Diem diem) {
        try {
            return clientService.capNhatDiem(diem);
        } catch (IOException e) {
            handleError("Lỗi cập nhật điểm", e);
            return false;
        }
    }
    
    public boolean xoaDiem(int id) {
        try {
            return clientService.xoaDiem(id);
        } catch (IOException e) {
            handleError("Lỗi xóa điểm", e);
            return false;
        }
    }
    
    public List<Diem> layDiemTheoSinhVien(int idSinhVien) {
        try {
            List<Diem> result = clientService.layDiemTheoSinhVien(idSinhVien);
            return (result != null) ? result : new ArrayList<>();  // ← FIX
        } catch (IOException e) {
            handleError("Lỗi lấy điểm sinh viên", e);
            return new ArrayList<>();  // ← FIX
        }
    }
    
    public List<Diem> layTatCaDiem() {
        try {
            List<Diem> result = clientService.layTatCaDiem();
            return (result != null) ? result : new ArrayList<>();  // ← FIX
        } catch (IOException e) {
            handleError("Lỗi lấy danh sách điểm", e);
            return new ArrayList<>();  // ← FIX
        }
    }
    
    public Map<String, Double> tinhDiemTrungBinhTheoLop() {
        try {
            return clientService.tinhDiemTrungBinhTheoLop();
        } catch (IOException e) {
            handleError("Lỗi tính điểm TB", e);
            return new HashMap<>();
        }
    }
    
    private void handleError(String message, Exception e) {
        System.err.println(message + ": " + e.getMessage());
    }
}