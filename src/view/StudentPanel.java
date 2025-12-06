package view;

import client.ClientService;
import controller.StudentController;
import controller.ClassController;
import model.SinhVien;
import model.Lop;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Panel quản lý sinh viên - CLIENT-SERVER VERSION
 */
public class StudentPanel extends JPanel {
    private StudentController controller;
    private ClassController classController;
    private JTable table;
    private DefaultTableModel tableModel;
    
    // Form fields
    private JTextField txtMSSV, txtHoTen, txtNgaySinh, txtTimKiem;
    private JComboBox<String> cboGioiTinh, cboLop;
    private JButton btnThem, btnSua, btnXoa, btnLamMoi, btnTimKiem, btnDongBoJSON;
    
    private String mssvCu = null;
    
    // CONSTRUCTOR MỚI - NHẬN ClientService
    public StudentPanel(ClientService clientService) {
        controller = new StudentController(clientService);
        classController = new ClassController(clientService);
        initComponents();
        loadData();
        loadLopHoc();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel form nhập liệu
        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.NORTH);
        
        // Panel bảng dữ liệu
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);
        
        // Panel nút chức năng
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Thông tin Sinh viên"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // MSSV
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("MSSV:"), gbc);
        gbc.gridx = 1;
        txtMSSV = new JTextField(20);
        panel.add(txtMSSV, gbc);
        
        // Họ tên
        gbc.gridx = 2;
        panel.add(new JLabel("Họ và tên:"), gbc);
        gbc.gridx = 3;
        txtHoTen = new JTextField(20);
        panel.add(txtHoTen, gbc);
        
        // Ngày sinh
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Ngày sinh (dd/MM/yyyy):"), gbc);
        gbc.gridx = 1;
        txtNgaySinh = new JTextField(20);
        txtNgaySinh.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        panel.add(txtNgaySinh, gbc);
        
        // Giới tính
        gbc.gridx = 2;
        panel.add(new JLabel("Giới tính:"), gbc);
        gbc.gridx = 3;
        cboGioiTinh = new JComboBox<>(new String[]{"Nam", "Nữ"});
        panel.add(cboGioiTinh, gbc);
        
        // Lớp
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Lớp:"), gbc);
        gbc.gridx = 1;
        
        // Panel chứa ComboBox và nút refresh
        JPanel lopPanel = new JPanel(new BorderLayout(5, 0));
        cboLop = new JComboBox<>();
        lopPanel.add(cboLop, BorderLayout.CENTER);
        
        JButton btnRefreshLop = new JButton("🔄");
        btnRefreshLop.setToolTipText("Làm mới danh sách lớp");
        btnRefreshLop.setPreferredSize(new Dimension(40, 25));
        btnRefreshLop.addActionListener(e -> {
            loadLopHoc();
            JOptionPane.showMessageDialog(this, 
                "Đã cập nhật danh sách lớp!", 
                "Thông báo", 
                JOptionPane.INFORMATION_MESSAGE);
        });
        lopPanel.add(btnRefreshLop, BorderLayout.EAST);
        
        panel.add(lopPanel, gbc);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Danh sách Sinh viên"));
        
        // Tạo bảng
        String[] columns = {"ID", "MSSV", "Họ và tên", "Ngày sinh", "Giới tính", "Lớp"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                hienThiThongTinChon();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Panel tìm kiếm
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Tìm kiếm:"));
        txtTimKiem = new JTextField(30);
        searchPanel.add(txtTimKiem);
        
        btnTimKiem = new JButton("Tìm theo tên");
        btnTimKiem.addActionListener(e -> timKiem());
        searchPanel.add(btnTimKiem);
        
        JButton btnTimMSSV = new JButton("Tìm theo MSSV");
        btnTimMSSV.addActionListener(e -> timTheoMSSV());
        searchPanel.add(btnTimMSSV);
        
        JComboBox<String> cboTimLop = new JComboBox<>();
        cboTimLop.addItem("-- Tất cả lớp --");
        loadLopHocToComboBox(cboTimLop);
        searchPanel.add(new JLabel("Lọc theo lớp:"));
        searchPanel.add(cboTimLop);
        cboTimLop.addActionListener(e -> {
            String selected = (String) cboTimLop.getSelectedItem();
            if (selected != null && !selected.startsWith("--")) {
                String maLop = selected.split(" - ")[0];
                timTheoLop(maLop);
            } else {
                loadData();
            }
        });
        
        panel.add(searchPanel, BorderLayout.NORTH);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        
        btnThem = new JButton("Thêm mới");
        btnThem.addActionListener(e -> themSinhVien());
        
        btnSua = new JButton("Cập nhật");
        btnSua.addActionListener(e -> suaSinhVien());
        
        btnXoa = new JButton("Xóa");
        btnXoa.addActionListener(e -> xoaSinhVien());
        
        btnLamMoi = new JButton("Làm mới");
        btnLamMoi.addActionListener(e -> lamMoi());
        
        btnDongBoJSON = new JButton("Đồng bộ JSON");
        btnDongBoJSON.addActionListener(e -> dongBoJSON());
        
        panel.add(btnThem);
        panel.add(btnSua);
        panel.add(btnXoa);
        panel.add(btnLamMoi);
        panel.add(btnDongBoJSON);
        
        return panel;
    }
    
    private void loadLopHoc() {
        loadLopHocToComboBox(cboLop);
    }
    
    private void loadLopHocToComboBox(JComboBox<String> combo) {
        combo.removeAllItems();
        List<Lop> danhSachLop = classController.layTatCaLop();
        for (Lop lop : danhSachLop) {
            combo.addItem(lop.getMaLop() + " - " + lop.getTenLop());
        }
    }
    
    private void loadData() {
        tableModel.setRowCount(0);
        List<SinhVien> danhSach = controller.layTatCaSinhVien();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        for (SinhVien sv : danhSach) {
            Object[] row = {
                sv.getId(),
                sv.getMssv(),
                sv.getHoTen(),
                sv.getNgaySinh().format(formatter),
                sv.getGioiTinh(),
                sv.getMaLop()
            };
            tableModel.addRow(row);
        }
    }
    
    private void themSinhVien() {
        if (!validateInput()) return;
        
        try {
            // Kiểm tra MSSV đã tồn tại chưa
            String mssv = txtMSSV.getText().trim();
            if (controller.timTheoMSSV(mssv) != null) {
                JOptionPane.showMessageDialog(this, 
                    "MSSV đã tồn tại! Vui lòng nhập MSSV khác.", 
                    "Cảnh báo", 
                    JOptionPane.WARNING_MESSAGE);
                txtMSSV.requestFocus();
                return;
            }
            
            String maLop = ((String) cboLop.getSelectedItem()).split(" - ")[0];
            LocalDate ngaySinh = LocalDate.parse(txtNgaySinh.getText(), 
                DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            
            SinhVien sv = new SinhVien(
                mssv,
                txtHoTen.getText().trim(),
                ngaySinh,
                (String) cboGioiTinh.getSelectedItem(),
                maLop
            );
            
            if (controller.themSinhVien(sv)) {
                JOptionPane.showMessageDialog(this, "Thêm sinh viên thành công!");
                loadData();
                lamMoi();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi thêm sinh viên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void suaSinhVien() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sinh viên cần sửa!");
            return;
        }
        
        if (!validateInput()) return;
        
        try {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            String mssvCu = (String) tableModel.getValueAt(selectedRow, 1);
            String mssvMoi = txtMSSV.getText().trim();
            
            // Kiểm tra có thay đổi MSSV không
            if (!mssvCu.equals(mssvMoi)) {
                int choice = JOptionPane.showConfirmDialog(this,
                    "⚠ MSSV không thể thay đổi!\n\n" +
                    "Bạn có muốn:\n" +
                    "- YES: Chỉ cập nhật thông tin khác (giữ nguyên MSSV " + mssvCu + ")\n" +
                    "- NO: Hủy bỏ",
                    "Cảnh báo",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
                
                if (choice != JOptionPane.YES_OPTION) {
                    return;
                }
                
                // Đặt lại MSSV cũ
                txtMSSV.setText(mssvCu);
                mssvMoi = mssvCu;
            }
            
            String maLop = ((String) cboLop.getSelectedItem()).split(" - ")[0];
            LocalDate ngaySinh = LocalDate.parse(txtNgaySinh.getText(), 
                DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            
            SinhVien sv = new SinhVien(
                id,
                mssvMoi,
                txtHoTen.getText().trim(),
                ngaySinh,
                (String) cboGioiTinh.getSelectedItem(),
                maLop
            );
            
            if (controller.capNhatSinhVien(sv)) {
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                loadData();
                lamMoi();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi cập nhật!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void xoaSinhVien() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sinh viên cần xóa!");
            return;
        }
        
        String mssv = (String) tableModel.getValueAt(selectedRow, 1);
        String hoTen = (String) tableModel.getValueAt(selectedRow, 2);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc muốn xóa sinh viên:\n" +
            "MSSV: " + mssv + "\n" +
            "Họ tên: " + hoTen + "\n\n" +
            "⚠ Lưu ý: Điểm số của sinh viên này cũng sẽ bị xóa!", 
            "Xác nhận xóa", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            
            if (controller.xoaSinhVien(id, mssv)) {
                JOptionPane.showMessageDialog(this, "Xóa thành công!");
                loadData();
                lamMoi();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi xóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void hienThiThongTinChon() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            String mssv = (String) tableModel.getValueAt(selectedRow, 1);
            mssvCu = mssv; // Lưu MSSV cũ
            
            txtMSSV.setText(mssv);
            txtHoTen.setText((String) tableModel.getValueAt(selectedRow, 2));
            txtNgaySinh.setText((String) tableModel.getValueAt(selectedRow, 3));
            cboGioiTinh.setSelectedItem(tableModel.getValueAt(selectedRow, 4));
            
            String maLop = (String) tableModel.getValueAt(selectedRow, 5);
            for (int i = 0; i < cboLop.getItemCount(); i++) {
                if (cboLop.getItemAt(i).startsWith(maLop)) {
                    cboLop.setSelectedIndex(i);
                    break;
                }
            }
        }
    }
    
    private void timKiem() {
        String keyword = txtTimKiem.getText().trim();
        if (keyword.isEmpty()) {
            loadData();
            return;
        }
        
        tableModel.setRowCount(0);
        List<SinhVien> ketQua = controller.timKiemTheoTen(keyword);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        if (ketQua.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Không tìm thấy sinh viên nào với từ khóa: " + keyword,
                "Kết quả tìm kiếm",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        for (SinhVien sv : ketQua) {
            Object[] row = {
                sv.getId(), sv.getMssv(), sv.getHoTen(),
                sv.getNgaySinh().format(formatter),
                sv.getGioiTinh(), sv.getMaLop()
            };
            tableModel.addRow(row);
        }
        
        JOptionPane.showMessageDialog(this, 
            "Tìm thấy " + ketQua.size() + " sinh viên!",
            "Kết quả tìm kiếm",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void timTheoMSSV() {
        String mssv = txtTimKiem.getText().trim();
        if (mssv.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập MSSV!");
            txtTimKiem.requestFocus();
            return;
        }
        
        SinhVien sv = controller.timTheoMSSV(mssv);
        if (sv != null) {
            tableModel.setRowCount(0);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            Object[] row = {
                sv.getId(), sv.getMssv(), sv.getHoTen(),
                sv.getNgaySinh().format(formatter),
                sv.getGioiTinh(), sv.getMaLop()
            };
            tableModel.addRow(row);
            
            JOptionPane.showMessageDialog(this, 
                "Tìm thấy sinh viên: " + sv.getHoTen(),
                "Thành công",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, 
                "Không tìm thấy sinh viên có MSSV: " + mssv,
                "Không tìm thấy",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void timTheoLop(String maLop) {
        tableModel.setRowCount(0);
        List<SinhVien> ketQua = controller.laySinhVienTheoLop(maLop);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        for (SinhVien sv : ketQua) {
            Object[] row = {
                sv.getId(), sv.getMssv(), sv.getHoTen(),
                sv.getNgaySinh().format(formatter),
                sv.getGioiTinh(), sv.getMaLop()
            };
            tableModel.addRow(row);
        }
    }
    
    public void dongBoJSON() {
        if (controller.dongBoJSON()) {
            JOptionPane.showMessageDialog(this, 
                "Đồng bộ JSON thành công!\n" +
                "File lưu tại: data/students.json",
                "Thành công",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, 
                "Lỗi đồng bộ JSON!\n" +
                "Vui lòng kiểm tra thư viện Gson đã được thêm chưa.",
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void lamMoi() {
        txtMSSV.setText("");
        txtHoTen.setText("");
        txtNgaySinh.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        cboGioiTinh.setSelectedIndex(0);
        if (cboLop.getItemCount() > 0) {
            cboLop.setSelectedIndex(0);
        }
        txtTimKiem.setText("");
        txtMSSV.setEditable(true);
        txtMSSV.setBackground(Color.WHITE);
        mssvCu = null;
        table.clearSelection();
        loadData();
    }
    
    private boolean validateInput() {
        if (txtMSSV.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập MSSV!");
            txtMSSV.requestFocus();
            return false;
        }
        
        // Validate format MSSV (chỉ chữ và số)
        String mssv = txtMSSV.getText().trim();
        if (!mssv.matches("^[A-Za-z0-9]+$")) {
            JOptionPane.showMessageDialog(this, 
                "MSSV chỉ được chứa chữ cái và số, không dấu!", 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
            txtMSSV.requestFocus();
            return false;
        }
        
        if (txtHoTen.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập họ tên!");
            txtHoTen.requestFocus();
            return false;
        }
        
        try {
            LocalDate ngaySinh = LocalDate.parse(txtNgaySinh.getText(), 
                DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            
            // Kiểm tra ngày sinh hợp lý (không quá xa trong quá khứ hoặc tương lai)
            LocalDate now = LocalDate.now();
            if (ngaySinh.isAfter(now)) {
                JOptionPane.showMessageDialog(this, 
                    "Ngày sinh không được lớn hơn ngày hiện tại!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                txtNgaySinh.requestFocus();
                return false;
            }
            
            if (ngaySinh.isBefore(now.minusYears(100))) {
                JOptionPane.showMessageDialog(this, 
                    "Ngày sinh không hợp lý!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                txtNgaySinh.requestFocus();
                return false;
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Ngày sinh không hợp lệ!\n" +
                "Định dạng đúng: dd/MM/yyyy\n" +
                "Ví dụ: 15/01/2003",
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
            txtNgaySinh.requestFocus();
            return false;
        }
        
        if (cboLop.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, 
                "Chưa có lớp nào!\nVui lòng tạo lớp trước.",
                "Cảnh báo",
                JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    /**
     * Làm mới tất cả dữ liệu (gọi khi chuyển tab)
     */
    public void refreshData() {
        loadLopHoc(); // Cập nhật danh sách lớp
        loadData();   // Cập nhật danh sách sinh viên
    }
}