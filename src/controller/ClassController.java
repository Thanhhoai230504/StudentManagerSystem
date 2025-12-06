package controller;

import dao.ClassDAO;
import model.Lop;
import java.util.List;

/**
 * Controller xử lý logic nghiệp vụ cho Lớp
 */
public class ClassController {
    private ClassDAO dao;
    
    public ClassController() {
        this.dao = new ClassDAO();
    }
    
    public boolean themLop(Lop lop) {
        return dao.themLop(lop);
    }
    
    public boolean capNhatLop(Lop lop) {
        return dao.capNhatLop(lop);
    }
    
    public boolean xoaLop(String maLop) {
        return dao.xoaLop(maLop);
    }
    
    public List<Lop> layTatCaLop() {
        return dao.layTatCaLop();
    }
    
    public Lop timLop(String maLop) {
        return dao.timLop(maLop);
    }
}