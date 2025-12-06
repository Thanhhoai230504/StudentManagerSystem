package view;

import client.ClientService;
import controller.ClassController;
import controller.StudentController;
import model.Lop;
import model.SinhVien;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel quản lý lớp học - CLIENT-SERVER VERSION
 */
public class ClassPanel extends JPanel {
    private ClassController controller;
    private StudentController studentController;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtMaLop, txtTenLop;
    private String maLopCu = null;
    
    // Constructor mới nhận ClientService
    public ClassPanel(ClientService clientService) {
        controller = new ClassController(clientService);
        studentController = new StudentController(clientService);
        initComponents();
        loadData();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin Lớp"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Mã lớp:"), gbc);
        gbc.gridx = 1;
        txtMaLop = new JTextField(20);
        formPanel.add(txtMaLop, gbc);
        
        gbc.gridx = 2;
        formPanel.add(new JLabel("Tên lớp:"), gbc);
        gbc.gridx = 3;
        txtTenLop = new JTextField(30);
        formPanel.add(txtTenLop, gbc);
        
        add(formPanel, BorderLayout.NORTH);
        
        // Table panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Danh sách Lớp"));
        
        String[] columns = {"Mã lớp", "Tên lớp", "Sĩ số"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                hienThiThongTinChon();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        add(tablePanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        JButton btnThem = new JButton("Thêm lớp");
        btnThem.addActionListener(e -> themLop());
        
        JButton btnSua = new JButton("Cập nhật");
        btnSua.addActionListener(e -> suaLop());
        
        JButton btnXoa = new JButton("Xóa");
        btnXoa.addActionListener(e -> xoaLop());
        
        JButton btnXemSV = new JButton("Xem sinh viên");
        btnXemSV.addActionListener(e -> xemSinhVien());
        
        JButton btnLamMoi = new JButton("Làm mới");
        btnLamMoi.addActionListener(e -> lamMoi());
        
        buttonPanel.add(btnThem);
        buttonPanel.add(btnSua);
        buttonPanel.add(btnXoa);
        buttonPanel.add(btnXemSV);
        buttonPanel.add(btnLamMoi);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void loadData() {
        tableModel.setRowCount(0);
        List<Lop> danhSach = controller.layTatCaLop();
        for (Lop lop : danhSach) {
            Object[] row = {lop.getMaLop(), lop.getTenLop(), lop.getSiSo()};
            tableModel.addRow(row);
        }
    }
    
    private void themLop() {
        if (!validateInput()) return;
        
        // Kiểm tra mã lớp đã tồn tại chưa
        if (controller.timLop(txtMaLop.getText().trim()) != null) {
            JOptionPane.showMessageDialog(this, 
                "Mã lớp đã tồn tại! Vui lòng nhập mã khác.", 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Lop lop = new Lop(txtMaLop.getText().trim(), txtTenLop.getText().trim());
        if (controller.themLop(lop)) {
            JOptionPane.showMessageDialog(this, "Thêm lớp thành công!");
            loadData();
            lamMoi();
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi thêm lớp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void suaLop() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lớp cần sửa!");
            return;
        }
        
        if (!validateInput()) return;
        
        // Lấy mã lớp cũ từ bảng (không cho phép thay đổi)
        String maLopCu = (String) tableModel.getValueAt(selectedRow, 0);
        String maLopMoi = txtMaLop.getText().trim();
        
        // Kiểm tra có thay đổi mã lớp không
        if (!maLopCu.equals(maLopMoi)) {
            int choice = JOptionPane.showConfirmDialog(this,
                "⚠ Mã lớp không thể thay đổi!\n\n" +
                "Bạn có muốn:\n" +
                "- YES: Chỉ cập nhật Tên lớp (giữ nguyên mã " + maLopCu + ")\n" +
                "- NO: Hủy bỏ",
                "Cảnh báo",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (choice != JOptionPane.YES_OPTION) {
                return;
            }
            
            // Đặt lại mã lớp cũ
            txtMaLop.setText(maLopCu);
        }
        
        // Cập nhật với mã lớp cũ
        Lop lop = new Lop(maLopCu, txtTenLop.getText().trim());
        if (controller.capNhatLop(lop)) {
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            loadData();
            lamMoi();
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi cập nhật!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void xoaLop() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lớp cần xóa!");
            return;
        }
        
        String maLop = (String) tableModel.getValueAt(selectedRow, 0);
        int siSo = (int) tableModel.getValueAt(selectedRow, 2);
        
        // Cảnh báo nếu lớp có sinh viên
        String message = "Bạn có chắc muốn xóa lớp này?";
        if (siSo > 0) {
            message = "⚠ CẢNH BÁO!\n\n" +
                     "Lớp này có " + siSo + " sinh viên.\n" +
                     "Xóa lớp sẽ làm mất thông tin lớp của các sinh viên!\n\n" +
                     "Bạn có chắc muốn xóa?";
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            message, 
            "Xác nhận xóa", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (controller.xoaLop(maLop)) {
                JOptionPane.showMessageDialog(this, "Xóa thành công!");
                loadData();
                lamMoi();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Lỗi xóa lớp!\nCó thể lớp đang được sử dụng.", 
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void xemSinhVien() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lớp!");
            return;
        }
        
        String maLop = (String) tableModel.getValueAt(selectedRow, 0);
        String tenLop = (String) tableModel.getValueAt(selectedRow, 1);
        List<SinhVien> danhSach = studentController.laySinhVienTheoLop(maLop);
        
        // Tạo bảng hiển thị đẹp hơn
        String[] columns = {"STT", "MSSV", "Họ và tên", "Giới tính"};
        DefaultTableModel svTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        if (danhSach.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Lớp " + maLop + " chưa có sinh viên nào!", 
                "Thông báo", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        for (int i = 0; i < danhSach.size(); i++) {
            SinhVien sv = danhSach.get(i);
            Object[] row = {
                i + 1,
                sv.getMssv(),
                sv.getHoTen(),
                sv.getGioiTinh()
            };
            svTableModel.addRow(row);
        }
        
        JTable svTable = new JTable(svTableModel);
        svTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        JScrollPane scrollPane = new JScrollPane(svTable);
        scrollPane.setPreferredSize(new Dimension(600, 400));
        
        JOptionPane.showMessageDialog(this, scrollPane, 
            "Danh sách sinh viên lớp " + maLop + " - " + tenLop + 
            " (Sĩ số: " + danhSach.size() + ")", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void hienThiThongTinChon() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            String maLop = (String) tableModel.getValueAt(selectedRow, 0);
            String tenLop = (String) tableModel.getValueAt(selectedRow, 1);
            
            txtMaLop.setText(maLop);
            txtTenLop.setText(tenLop);
            
            // Lưu mã lớp cũ
            maLopCu = maLop;
            
            // Disable mã lớp khi đang sửa
            txtMaLop.setEditable(false);
            txtMaLop.setBackground(Color.LIGHT_GRAY);
        }
    }
    
    private void lamMoi() {
        txtMaLop.setText("");
        txtTenLop.setText("");
        txtMaLop.setEditable(true);
        txtMaLop.setBackground(Color.WHITE);
        maLopCu = null;
        table.clearSelection();
        loadData();
    }
    
    private boolean validateInput() {
        if (txtMaLop.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã lớp!");
            txtMaLop.requestFocus();
            return false;
        }
        if (txtTenLop.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên lớp!");
            txtTenLop.requestFocus();
            return false;
        }
        
        // Validate format mã lớp (chỉ chữ và số, không dấu)
        String maLop = txtMaLop.getText().trim();
        if (!maLop.matches("^[A-Za-z0-9]+$")) {
            JOptionPane.showMessageDialog(this, 
                "Mã lớp chỉ được chứa chữ cái và số, không dấu!", 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
            txtMaLop.requestFocus();
            return false;
        }
        
        return true;
    }
    
    /**
     * Làm mới dữ liệu (gọi khi chuyển tab)
     */
    public void refreshData() {
        loadData();
    }
}