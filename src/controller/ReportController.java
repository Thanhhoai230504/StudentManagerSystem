package controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import client.ClientService;

/**
 * ReportController - Phía Client
 */
public class ReportController {
    private ClientService clientService;
    
    public ReportController(ClientService clientService) {
        this.clientService = clientService;
    }
    
    public Map<String, Integer> thongKeSoLuongTheoLop() {
        try {
            return clientService.thongKeSoLuongTheoLop();
        } catch (IOException e) {
            handleError("Lỗi thống kê lớp", e);
            return new HashMap<>();
        }
    }
    
    public Map<String, Double> thongKeDiemTrungBinhTheoLop() {
        try {
            return clientService.tinhDiemTrungBinhTheoLop();
        } catch (IOException e) {
            handleError("Lỗi thống kê điểm TB", e);
            return new HashMap<>();
        }
    }
    
    public Map<String, Integer> thongKeTheoGioiTinh() {
        try {
            return clientService.thongKeTheoGioiTinh();
        } catch (IOException e) {
            handleError("Lỗi thống kê giới tính", e);
            return new HashMap<>();
        }
    }
    
    public boolean xuatBaoCaoJSON() {
        try {
            return clientService.xuatBaoCaoJSON();
        } catch (IOException e) {
            handleError("Lỗi xuất báo cáo", e);
            return false;
        }
    }
    
    private void handleError(String message, Exception e) {
        System.err.println(message + ": " + e.getMessage());
    }
}