package dao;

import model.Diem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * DAO xử lý các thao tác CRUD với bảng scores
 */
public class ScoreDAO {
    private Connection connection;
    
    public ScoreDAO() {
        this.connection = DatabaseConnection.getConnection();
    }
    
    /**
     * Thêm điểm mới
     */
    public boolean themDiem(Diem diem) {
        String sql = "INSERT INTO scores (id_sinh_vien, mon_hoc, diem_qua_trinh, diem_thi, diem_tong_ket) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, diem.getIdSinhVien());
            pstmt.setString(2, diem.getMonHoc());
            pstmt.setDouble(3, diem.getDiemQuaTrinh());
            pstmt.setDouble(4, diem.getDiemThi());
            pstmt.setDouble(5, diem.tinhDiemTongKet());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi thêm điểm: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Cập nhật điểm
     */
    public boolean capNhatDiem(Diem diem) {
        String sql = "UPDATE scores SET diem_qua_trinh=?, diem_thi=?, diem_tong_ket=? " +
                     "WHERE id=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, diem.getDiemQuaTrinh());
            pstmt.setDouble(2, diem.getDiemThi());
            pstmt.setDouble(3, diem.tinhDiemTongKet());
            pstmt.setInt(4, diem.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi cập nhật điểm: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Xóa điểm theo ID
     */
    public boolean xoaDiem(int id) {
        String sql = "DELETE FROM scores WHERE id=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi xóa điểm: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Lấy điểm theo sinh viên
     */
    public List<Diem> layDiemTheoSinhVien(int idSinhVien) {
        List<Diem> danhSach = new ArrayList<>();
        String sql = "SELECT sc.*, st.mssv, st.ho_ten FROM scores sc " +
                     "JOIN students st ON sc.id_sinh_vien = st.id " +
                     "WHERE sc.id_sinh_vien=?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, idSinhVien);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Diem diem = new Diem(
                    rs.getInt("id"),
                    rs.getInt("id_sinh_vien"),
                    rs.getString("mssv"),
                    rs.getString("ho_ten"),
                    rs.getString("mon_hoc"),
                    rs.getDouble("diem_qua_trinh"),
                    rs.getDouble("diem_thi")
                );
                danhSach.add(diem);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy điểm theo sinh viên: " + e.getMessage());
            e.printStackTrace();
        }
        return danhSach;
    }
    
    /**
     * Lấy tất cả điểm
     */
    public List<Diem> layTatCaDiem() {
        List<Diem> danhSach = new ArrayList<>();
        String sql = "SELECT sc.*, st.mssv, st.ho_ten FROM scores sc " +
                     "JOIN students st ON sc.id_sinh_vien = st.id " +
                     "ORDER BY st.mssv, sc.mon_hoc";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Diem diem = new Diem(
                    rs.getInt("id"),
                    rs.getInt("id_sinh_vien"),
                    rs.getString("mssv"),
                    rs.getString("ho_ten"),
                    rs.getString("mon_hoc"),
                    rs.getDouble("diem_qua_trinh"),
                    rs.getDouble("diem_thi")
                );
                danhSach.add(diem);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy tất cả điểm: " + e.getMessage());
            e.printStackTrace();
        }
        return danhSach;
    }
    
    /**
     * Tính điểm trung bình theo lớp
     */
    public Map<String, Double> tinhDiemTrungBinhTheoLop() {
        Map<String, Double> ketQua = new HashMap<>();
        String sql = "SELECT st.ma_lop, AVG(sc.diem_tong_ket) as dtb " +
                     "FROM scores sc JOIN students st ON sc.id_sinh_vien = st.id " +
                     "GROUP BY st.ma_lop";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String maLop = rs.getString("ma_lop");
                double diemTB = Math.round(rs.getDouble("dtb") * 100.0) / 100.0;
                ketQua.put(maLop, diemTB);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tính điểm TB theo lớp: " + e.getMessage());
            e.printStackTrace();
        }
        return ketQua;
    }
    
    /**
     * Tính điểm trung bình của một sinh viên
     */
    public double tinhDiemTrungBinhSinhVien(int idSinhVien) {
        String sql = "SELECT AVG(diem_tong_ket) as dtb FROM scores WHERE id_sinh_vien=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, idSinhVien);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Math.round(rs.getDouble("dtb") * 100.0) / 100.0;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tính điểm TB sinh viên: " + e.getMessage());
            e.printStackTrace();
        }
        return 0.0;
    }
    
    /**
     * Lấy điểm theo môn học
     */
    public List<Diem> layDiemTheoMonHoc(String monHoc) {
        List<Diem> danhSach = new ArrayList<>();
        String sql = "SELECT sc.*, st.mssv, st.ho_ten FROM scores sc " +
                     "JOIN students st ON sc.id_sinh_vien = st.id " +
                     "WHERE sc.mon_hoc=? " +
                     "ORDER BY sc.diem_tong_ket DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, monHoc);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Diem diem = new Diem(
                    rs.getInt("id"),
                    rs.getInt("id_sinh_vien"),
                    rs.getString("mssv"),
                    rs.getString("ho_ten"),
                    rs.getString("mon_hoc"),
                    rs.getDouble("diem_qua_trinh"),
                    rs.getDouble("diem_thi")
                );
                danhSach.add(diem);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy điểm theo môn học: " + e.getMessage());
            e.printStackTrace();
        }
        return danhSach;
    }
    
    /**
     * Kiểm tra sinh viên đã có điểm môn học chưa
     */
    public boolean kiemTraDiemTonTai(int idSinhVien, String monHoc) {
        String sql = "SELECT COUNT(*) as count FROM scores WHERE id_sinh_vien=? AND mon_hoc=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, idSinhVien);
            pstmt.setString(2, monHoc);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi kiểm tra điểm: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Lấy danh sách môn học (không trùng lặp)
     */
    public List<String> layDanhSachMonHoc() {
        List<String> danhSach = new ArrayList<>();
        String sql = "SELECT DISTINCT mon_hoc FROM scores ORDER BY mon_hoc";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                danhSach.add(rs.getString("mon_hoc"));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy danh sách môn học: " + e.getMessage());
            e.printStackTrace();
        }
        return danhSach;
    }
    
    /**
     * Đếm số môn học của sinh viên
     */
    public int demSoMonHoc(int idSinhVien) {
        String sql = "SELECT COUNT(*) as total FROM scores WHERE id_sinh_vien=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, idSinhVien);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi đếm môn học: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    
    /**
     * Lấy điểm cao nhất của một môn học
     */
    public double layDiemCaoNhat(String monHoc) {
        String sql = "SELECT MAX(diem_tong_ket) as max_diem FROM scores WHERE mon_hoc=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, monHoc);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("max_diem");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy điểm cao nhất: " + e.getMessage());
            e.printStackTrace();
        }
        return 0.0;
    }
    
    /**
     * Lấy điểm thấp nhất của một môn học
     */
    public double layDiemThapNhat(String monHoc) {
        String sql = "SELECT MIN(diem_tong_ket) as min_diem FROM scores WHERE mon_hoc=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, monHoc);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("min_diem");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy điểm thấp nhất: " + e.getMessage());
            e.printStackTrace();
        }
        return 0.0;
    }
    
    /**
     * Thống kê điểm theo khoảng
     * Loại giỏi (>= 8.0), khá (>= 6.5), TB (>= 5.0), yếu (< 5.0)
     */
    public Map<String, Integer> thongKeTheoXepLoai() {
        Map<String, Integer> ketQua = new HashMap<>();
        String sql = "SELECT " +
                     "SUM(CASE WHEN diem_tong_ket >= 8.0 THEN 1 ELSE 0 END) as gioi, " +
                     "SUM(CASE WHEN diem_tong_ket >= 6.5 AND diem_tong_ket < 8.0 THEN 1 ELSE 0 END) as kha, " +
                     "SUM(CASE WHEN diem_tong_ket >= 5.0 AND diem_tong_ket < 6.5 THEN 1 ELSE 0 END) as tb, " +
                     "SUM(CASE WHEN diem_tong_ket < 5.0 THEN 1 ELSE 0 END) as yeu " +
                     "FROM scores";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                ketQua.put("Giỏi", rs.getInt("gioi"));
                ketQua.put("Khá", rs.getInt("kha"));
                ketQua.put("Trung bình", rs.getInt("tb"));
                ketQua.put("Yếu", rs.getInt("yeu"));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi thống kê xếp loại: " + e.getMessage());
            e.printStackTrace();
        }
        return ketQua;
    }
}