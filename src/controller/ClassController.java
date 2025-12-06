package controller;

import client.ClientService;
import model.Lop;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * ClassController - Phía Client
 */
public class ClassController {
    private ClientService clientService;
    
    // CONSTRUCTOR NÀY PHẢI CÓ!!!
    public ClassController(ClientService clientService) {
        this.clientService = clientService;
    }
    
    public boolean themLop(Lop lop) {
        try {
            return clientService.themLop(lop);
        } catch (IOException e) {
            handleError("Lỗi thêm lớp", e);
            return false;
        }
    }
    
    public boolean capNhatLop(Lop lop) {
        try {
            return clientService.capNhatLop(lop);
        } catch (IOException e) {
            handleError("Lỗi cập nhật lớp", e);
            return false;
        }
    }
    
    public boolean xoaLop(String maLop) {
        try {
            return clientService.xoaLop(maLop);
        } catch (IOException e) {
            handleError("Lỗi xóa lớp", e);
            return false;
        }
    }
    
    public List<Lop> layTatCaLop() {
        try {
            List<Lop> result = clientService.layTatCaLop();
            return (result != null) ? result : new ArrayList<>();  // ← FIX
        } catch (IOException e) {
            handleError("Lỗi lấy danh sách lớp", e);
            return new ArrayList<>();  // ← FIX
        }
    }
    
    public Lop timLop(String maLop) {
        try {
            List<Lop> allClasses = clientService.layTatCaLop();
            return allClasses.stream()
                .filter(lop -> lop.getMaLop().equals(maLop))
                .findFirst()
                .orElse(null);
        } catch (IOException e) {
            handleError("Lỗi tìm lớp", e);
            return null;
        }
    }
    
    private void handleError(String message, Exception e) {
        System.err.println(message + ": " + e.getMessage());
    }
}