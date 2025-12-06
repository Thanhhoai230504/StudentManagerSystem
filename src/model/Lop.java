package model;

/**
 * Lớp Model đại diện cho Lớp học
 */
public class Lop {
    private String maLop;
    private String tenLop;
    private int siSo;
    
    public Lop(String maLop, String tenLop, int siSo) {
        this.maLop = maLop;
        this.tenLop = tenLop;
        this.siSo = siSo;
    }
    
    public Lop(String maLop, String tenLop) {
        this.maLop = maLop;
        this.tenLop = tenLop;
        this.siSo = 0;
    }
    
    public Lop() {}
    
    public String getMaLop() {
        return maLop;
    }
    
    public void setMaLop(String maLop) {
        this.maLop = maLop;
    }
    
    public String getTenLop() {
        return tenLop;
    }
    
    public void setTenLop(String tenLop) {
        this.tenLop = tenLop;
    }
    
    public int getSiSo() {
        return siSo;
    }
    
    public void setSiSo(int siSo) {
        this.siSo = siSo;
    }
    
    @Override
    public String toString() {
        return maLop + " - " + tenLop;
    }
}