package dao;

import model.Lop;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO xử lý các thao tác CRUD với bảng classes
 */
public class ClassDAO {
    private Connection connection;
    
    public ClassDAO() {
        this.connection = DatabaseConnection.getConnection();
    }
    
    /**
     * Thêm lớp mới
     */
    public boolean themLop(Lop lop) {
        String sql = "INSERT INTO classes (ma_lop, ten_lop) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, lop.getMaLop());
            pstmt.setString(2, lop.getTenLop());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi thêm lớp: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Cập nhật thông tin lớp
     */
    public boolean capNhatLop(Lop lop) {
        String sql = "UPDATE classes SET ten_lop=? WHERE ma_lop=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, lop.getTenLop());
            pstmt.setString(2, lop.getMaLop());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi cập nhật lớp: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Xóa lớp theo mã lớp
     */
    public boolean xoaLop(String maLop) {
        String sql = "DELETE FROM classes WHERE ma_lop=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, maLop);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi xóa lớp: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Lấy tất cả lớp (kèm sĩ số)
     */
    public List<Lop> layTatCaLop() {
        List<Lop> danhSach = new ArrayList<>();
        String sql = "SELECT c.ma_lop, c.ten_lop, COUNT(s.id) as si_so " +
                     "FROM classes c LEFT JOIN students s ON c.ma_lop = s.ma_lop " +
                     "GROUP BY c.ma_lop, c.ten_lop ORDER BY c.ma_lop";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Lop lop = new Lop(
                    rs.getString("ma_lop"),
                    rs.getString("ten_lop"),
                    rs.getInt("si_so")
                );
                danhSach.add(lop);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy danh sách lớp: " + e.getMessage());
            e.printStackTrace();
        }
        return danhSach;
    }
    
    /**
     * Tìm lớp theo mã lớp
     */
    public Lop timLop(String maLop) {
        String sql = "SELECT c.ma_lop, c.ten_lop, COUNT(s.id) as si_so " +
                     "FROM classes c LEFT JOIN students s ON c.ma_lop = s.ma_lop " +
                     "WHERE c.ma_lop=? " +
                     "GROUP BY c.ma_lop, c.ten_lop";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, maLop);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Lop(
                    rs.getString("ma_lop"), 
                    rs.getString("ten_lop"),
                    rs.getInt("si_so")
                );
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tìm lớp: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Kiểm tra lớp có tồn tại không
     */
    public boolean kiemTraLopTonTai(String maLop) {
        String sql = "SELECT COUNT(*) as count FROM classes WHERE ma_lop=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, maLop);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi kiểm tra lớp: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Đếm tổng số lớp
     */
    public int demTongLop() {
        String sql = "SELECT COUNT(*) as total FROM classes";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi đếm lớp: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    
    /**
     * Lấy danh sách lớp (không có sĩ số) - cho combobox
     */
    public List<Lop> layDanhSachLopDonGian() {
        List<Lop> danhSach = new ArrayList<>();
        String sql = "SELECT ma_lop, ten_lop FROM classes ORDER BY ma_lop";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Lop lop = new Lop(
                    rs.getString("ma_lop"),
                    rs.getString("ten_lop")
                );
                danhSach.add(lop);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy danh sách lớp: " + e.getMessage());
            e.printStackTrace();
        }
        return danhSach;
    }
    
    /**
     * Tìm kiếm lớp theo tên (tìm gần đúng)
     */
    public List<Lop> timKiemTheoTen(String tenLop) {
        List<Lop> danhSach = new ArrayList<>();
        String sql = "SELECT c.ma_lop, c.ten_lop, COUNT(s.id) as si_so " +
                     "FROM classes c LEFT JOIN students s ON c.ma_lop = s.ma_lop " +
                     "WHERE c.ten_lop LIKE ? " +
                     "GROUP BY c.ma_lop, c.ten_lop ORDER BY c.ma_lop";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "%" + tenLop + "%");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Lop lop = new Lop(
                    rs.getString("ma_lop"),
                    rs.getString("ten_lop"),
                    rs.getInt("si_so")
                );
                danhSach.add(lop);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tìm kiếm lớp: " + e.getMessage());
            e.printStackTrace();
        }
        return danhSach;
    }
}