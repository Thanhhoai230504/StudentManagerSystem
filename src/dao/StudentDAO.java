package dao;

import model.SinhVien;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO xử lý các thao tác CRUD với bảng students
 */
public class StudentDAO {
    private Connection connection;
    
    public StudentDAO() {
        this.connection = DatabaseConnection.getConnection();
    }
    
    /**
     * Thêm sinh viên mới
     */
    public boolean themSinhVien(SinhVien sv) {
        String sql = "INSERT INTO students (mssv, ho_ten, ngay_sinh, gioi_tinh, ma_lop) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, sv.getMssv());
            pstmt.setString(2, sv.getHoTen());
            pstmt.setDate(3, Date.valueOf(sv.getNgaySinh()));
            pstmt.setString(4, sv.getGioiTinh());
            pstmt.setString(5, sv.getMaLop());
            
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi thêm sinh viên: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Cập nhật thông tin sinh viên
     */
    public boolean capNhatSinhVien(SinhVien sv) {
        String sql = "UPDATE students SET mssv=?, ho_ten=?, ngay_sinh=?, gioi_tinh=?, ma_lop=? WHERE id=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, sv.getMssv());
            pstmt.setString(2, sv.getHoTen());
            pstmt.setDate(3, Date.valueOf(sv.getNgaySinh()));
            pstmt.setString(4, sv.getGioiTinh());
            pstmt.setString(5, sv.getMaLop());
            pstmt.setInt(6, sv.getId());
            
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi cập nhật sinh viên: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Xóa sinh viên theo ID
     */
    public boolean xoaSinhVien(int id) {
        String sql = "DELETE FROM students WHERE id=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi xóa sinh viên: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Lấy tất cả sinh viên
     * ORDER BY id DESC: Sinh viên mới nhất ở trên (giống Facebook, Instagram)
     * ORDER BY id ASC: Sinh viên cũ nhất ở trên (theo thứ tự thêm vào)
     */
    public List<SinhVien> layTatCaSinhVien() {
        List<SinhVien> danhSach = new ArrayList<>();
        // Sắp xếp theo ID tăng dần (sinh viên mới ở cuối)
        String sql = "SELECT * FROM students ORDER BY id ASC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                SinhVien sv = new SinhVien(
                    rs.getInt("id"),
                    rs.getString("mssv"),
                    rs.getString("ho_ten"),
                    rs.getDate("ngay_sinh").toLocalDate(),
                    rs.getString("gioi_tinh"),
                    rs.getString("ma_lop")
                );
                danhSach.add(sv);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy danh sách sinh viên: " + e.getMessage());
            e.printStackTrace();
        }
        return danhSach;
    }
    
    /**
     * Tìm sinh viên theo MSSV
     */
    public SinhVien timTheoMSSV(String mssv) {
        String sql = "SELECT * FROM students WHERE mssv=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, mssv);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new SinhVien(
                    rs.getInt("id"),
                    rs.getString("mssv"),
                    rs.getString("ho_ten"),
                    rs.getDate("ngay_sinh").toLocalDate(),
                    rs.getString("gioi_tinh"),
                    rs.getString("ma_lop")
                );
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tìm sinh viên: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Tìm kiếm sinh viên theo tên (tìm gần đúng)
     */
    public List<SinhVien> timKiemTheoTen(String ten) {
        List<SinhVien> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE ho_ten LIKE ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "%" + ten + "%");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                SinhVien sv = new SinhVien(
                    rs.getInt("id"),
                    rs.getString("mssv"),
                    rs.getString("ho_ten"),
                    rs.getDate("ngay_sinh").toLocalDate(),
                    rs.getString("gioi_tinh"),
                    rs.getString("ma_lop")
                );
                danhSach.add(sv);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tìm kiếm: " + e.getMessage());
            e.printStackTrace();
        }
        return danhSach;
    }
    
    /**
     * Lấy sinh viên theo lớp
     */
    public List<SinhVien> laySinhVienTheoLop(String maLop) {
        List<SinhVien> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE ma_lop=?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, maLop);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                SinhVien sv = new SinhVien(
                    rs.getInt("id"),
                    rs.getString("mssv"),
                    rs.getString("ho_ten"),
                    rs.getDate("ngay_sinh").toLocalDate(),
                    rs.getString("gioi_tinh"),
                    rs.getString("ma_lop")
                );
                danhSach.add(sv);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy sinh viên theo lớp: " + e.getMessage());
            e.printStackTrace();
        }
        return danhSach;
    }
    
    /**
     * Lấy sinh viên theo giới tính
     */
    public List<SinhVien> laySinhVienTheoGioiTinh(String gioiTinh) {
        List<SinhVien> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE gioi_tinh=?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, gioiTinh);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                SinhVien sv = new SinhVien(
                    rs.getInt("id"),
                    rs.getString("mssv"),
                    rs.getString("ho_ten"),
                    rs.getDate("ngay_sinh").toLocalDate(),
                    rs.getString("gioi_tinh"),
                    rs.getString("ma_lop")
                );
                danhSach.add(sv);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy sinh viên theo giới tính: " + e.getMessage());
            e.printStackTrace();
        }
        return danhSach;
    }
    
    /**
     * Lấy sinh viên theo ID
     */
    public SinhVien timTheoId(int id) {
        String sql = "SELECT * FROM students WHERE id=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new SinhVien(
                    rs.getInt("id"),
                    rs.getString("mssv"),
                    rs.getString("ho_ten"),
                    rs.getDate("ngay_sinh").toLocalDate(),
                    rs.getString("gioi_tinh"),
                    rs.getString("ma_lop")
                );
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tìm sinh viên theo ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Đếm tổng số sinh viên
     */
    public int demTongSinhVien() {
        String sql = "SELECT COUNT(*) as total FROM students";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi đếm sinh viên: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    
    /**
     * Đếm sinh viên theo lớp
     */
    public int demSinhVienTheoLop(String maLop) {
        String sql = "SELECT COUNT(*) as total FROM students WHERE ma_lop=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, maLop);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi đếm sinh viên theo lớp: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
}