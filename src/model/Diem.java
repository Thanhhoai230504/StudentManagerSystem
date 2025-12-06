package model;

/**
 * Lớp Model đại diện cho Điểm
 */
public class Diem {
    private int id;
    private int idSinhVien;
    private String mssv;
    private String hoTen;
    private String monHoc;
    private double diemQuaTrinh;
    private double diemThi;
    private double diemTongKet;
    
    public Diem(int id, int idSinhVien, String mssv, String hoTen,
                String monHoc, double diemQuaTrinh, double diemThi) {
        this.id = id;
        this.idSinhVien = idSinhVien;
        this.mssv = mssv;
        this.hoTen = hoTen;
        this.monHoc = monHoc;
        this.diemQuaTrinh = diemQuaTrinh;
        this.diemThi = diemThi;
        this.diemTongKet = tinhDiemTongKet();
    }
    
    public Diem(int idSinhVien, String monHoc, double diemQuaTrinh, double diemThi) {
        this.idSinhVien = idSinhVien;
        this.monHoc = monHoc;
        this.diemQuaTrinh = diemQuaTrinh;
        this.diemThi = diemThi;
        this.diemTongKet = tinhDiemTongKet();
    }
    
    public Diem() {}
    
    /**
     * Công thức tính điểm: 30% quá trình + 70% thi
     */
    public double tinhDiemTongKet() {
        return Math.round((diemQuaTrinh * 0.3 + diemThi * 0.7) * 10.0) / 10.0;
    }
    
    // Getters và Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getIdSinhVien() {
        return idSinhVien;
    }
    
    public void setIdSinhVien(int idSinhVien) {
        this.idSinhVien = idSinhVien;
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
    
    public String getMonHoc() {
        return monHoc;
    }
    
    public void setMonHoc(String monHoc) {
        this.monHoc = monHoc;
    }
    
    public double getDiemQuaTrinh() {
        return diemQuaTrinh;
    }
    
    public void setDiemQuaTrinh(double diemQuaTrinh) {
        this.diemQuaTrinh = diemQuaTrinh;
        this.diemTongKet = tinhDiemTongKet();
    }
    
    public double getDiemThi() {
        return diemThi;
    }
    
    public void setDiemThi(double diemThi) {
        this.diemThi = diemThi;
        this.diemTongKet = tinhDiemTongKet();
    }
    
    public double getDiemTongKet() {
        return diemTongKet;
    }
    
    public void setDiemTongKet(double diemTongKet) {
        this.diemTongKet = diemTongKet;
    }
    
    @Override
    public String toString() {
        return "Diem{" +
                "id=" + id +
                ", mssv='" + mssv + '\'' +
                ", monHoc='" + monHoc + '\'' +
                ", diemTongKet=" + diemTongKet +
                '}';
    }
}