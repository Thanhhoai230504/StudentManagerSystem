package controller;

import client.ClientService;
import model.SinhVien;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StudentController {
    private ClientService clientService;
    
    public StudentController(ClientService clientService) {
        this.clientService = clientService;
    }
    
    public boolean themSinhVien(SinhVien sv) {
        try {
            return clientService.themSinhVien(sv);
        } catch (IOException e) {
            handleError("Lỗi thêm sinh viên", e);
            return false;
        }
    }
    
    public boolean capNhatSinhVien(SinhVien sv) {
        try {
            return clientService.capNhatSinhVien(sv);
        } catch (IOException e) {
            handleError("Lỗi cập nhật sinh viên", e);
            return false;
        }
    }
    
    public boolean xoaSinhVien(int id, String mssv) {
        try {
            return clientService.xoaSinhVien(id, mssv);
        } catch (IOException e) {
            handleError("Lỗi xóa sinh viên", e);
            return false;
        }
    }
    
    public List<SinhVien> layTatCaSinhVien() {
        try {
            List<SinhVien> result = clientService.layTatCaSinhVien();
            return (result != null) ? result : new ArrayList<>();  // ← FIX
        } catch (IOException e) {
            handleError("Lỗi lấy danh sách sinh viên", e);
            return new ArrayList<>();  // ← FIX
        }
    }
    
    public SinhVien timTheoMSSV(String mssv) {
        try {
            return clientService.timTheoMSSV(mssv);
        } catch (IOException e) {
            handleError("Lỗi tìm sinh viên", e);
            return null;
        }
    }
    
    public List<SinhVien> timKiemTheoTen(String ten) {
        try {
            List<SinhVien> result = clientService.timKiemTheoTen(ten);
            return (result != null) ? result : new ArrayList<>();  // ← FIX
        } catch (IOException e) {
            handleError("Lỗi tìm kiếm", e);
            return new ArrayList<>();  // ← FIX
        }
    }
    
    public List<SinhVien> laySinhVienTheoLop(String maLop) {
        try {
            List<SinhVien> result = clientService.laySinhVienTheoLop(maLop);
            return (result != null) ? result : new ArrayList<>();  // ← FIX
        } catch (IOException e) {
            handleError("Lỗi lấy sinh viên theo lớp", e);
            return new ArrayList<>();  // ← FIX
        }
    }
    
    public List<SinhVien> layDuLieuJSON() {
        try {
            List<SinhVien> result = clientService.layDuLieuJSON();
            return (result != null) ? result : new ArrayList<>();  // ← FIX
        } catch (IOException e) {
            handleError("Lỗi đọc JSON", e);
            return new ArrayList<>();  // ← FIX
        }
    }
    
    public boolean dongBoJSON() {
        try {
            return clientService.dongBoJSON();
        } catch (IOException e) {
            handleError("Lỗi đồng bộ JSON", e);
            return false;
        }
    }
    
    private void handleError(String message, Exception e) {
        System.err.println(message + ": " + e.getMessage());
        e.printStackTrace();  // ← THÊM để thấy lỗi chi tiết
    }
}