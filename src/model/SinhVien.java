package model;

import java.time.LocalDate;

/**
 * Lớp Model đại diện cho Sinh viên
 */
public class SinhVien {
    private int id;
    private String mssv;
    private String hoTen;
    private LocalDate ngaySinh;
    private String gioiTinh;
    private String maLop;
    
    // Constructor đầy đủ
    public SinhVien(int id, String mssv, String hoTen, LocalDate ngaySinh, 
                    String gioiTinh, String maLop) {
        this.id = id;
        this.mssv = mssv;
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.gioiTinh = gioiTinh;
        this.maLop = maLop;
    }
    
    // Constructor không có id (dùng khi thêm mới)
    public SinhVien(String mssv, String hoTen, LocalDate ngaySinh, 
                    String gioiTinh, String maLop) {
        this.mssv = mssv;
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.gioiTinh = gioiTinh;
        this.maLop = maLop;
    }
    
    // Constructor mặc định
    public SinhVien() {}
    
    // Getters và Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getMssv() {
        return mssv;
    }
    
    public void setMssv(String mssv) {
        this.mssv = mssv;
    }
    
    public String getHoTen() {
        return hoTen;
    }
    
    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }
    
    public LocalDate getNgaySinh() {
        return ngaySinh;
    }
    
    public void setNgaySinh(LocalDate ngaySinh) {
        this.ngaySinh = ngaySinh;
    }
    
    public String getGioiTinh() {
        return gioiTinh;
    }
    
    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }
    
    public String getMaLop() {
        return maLop;
    }
    
    public void setMaLop(String maLop) {
        this.maLop = maLop;
    }
    
    @Override
    public String toString() {
        return "SinhVien{" +
                "id=" + id +
                ", mssv='" + mssv + '\'' +
                ", hoTen='" + hoTen + '\'' +
                ", ngaySinh=" + ngaySinh +
                ", gioiTinh='" + gioiTinh + '\'' +
                ", maLop='" + maLop + '\'' +
                '}';
    }
}