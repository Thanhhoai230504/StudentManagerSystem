package view;

import client.ClientService;
import controller.ScoreController;
import controller.StudentController;
import model.Diem;
import model.SinhVien;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel quản lý điểm - CLIENT-SERVER VERSION
 */
public class ScorePanel extends JPanel {
    private ScoreController controller;
    private StudentController studentController;
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> cboSinhVien;
    private JTextField txtMonHoc, txtDiemQT, txtDiemThi, txtDiemTK;
    
    // CONSTRUCTOR MỚI - NHẬN ClientService
    public ScorePanel(ClientService clientService) {
        controller = new ScoreController(clientService);
        studentController = new StudentController(clientService);
        initComponents();
        loadSinhVien();
        loadData();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Nhập điểm"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Sinh viên:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        cboSinhVien = new JComboBox<>();
        formPanel.add(cboSinhVien, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Môn học:"), gbc);
        gbc.gridx = 1;
        txtMonHoc = new JTextField(15);
        formPanel.add(txtMonHoc, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Điểm quá trình:"), gbc);
        gbc.gridx = 1;
        txtDiemQT = new JTextField(15);
        formPanel.add(txtDiemQT, gbc);
        
        gbc.gridx = 2;
        formPanel.add(new JLabel("Điểm thi:"), gbc);
        gbc.gridx = 3;
        txtDiemThi = new JTextField(15);
        formPanel.add(txtDiemThi, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Điểm tổng kết:"), gbc);
        gbc.gridx = 1;
        txtDiemTK = new JTextField(15);
        txtDiemTK.setEditable(false);
        txtDiemTK.setBackground(Color.LIGHT_GRAY);
        formPanel.add(txtDiemTK, gbc);
        
        JButton btnTinhDiem = new JButton("Tính điểm");
        btnTinhDiem.addActionListener(e -> tinhDiem());
        gbc.gridx = 2; gbc.gridwidth = 2;
        formPanel.add(btnTinhDiem, gbc);
        
        add(formPanel, BorderLayout.NORTH);
        
        // Table panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Bảng điểm"));
        
        String[] columns = {"ID", "MSSV", "Họ tên", "Môn học", "Điểm QT", "Điểm thi", "Điểm TK"};
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
        
        JButton btnThem = new JButton("Thêm điểm");
        btnThem.addActionListener(e -> themDiem());
        
        JButton btnSua = new JButton("Cập nhật");
        btnSua.addActionListener(e -> suaDiem());
        
        JButton btnXoa = new JButton("Xóa");
        btnXoa.addActionListener(e -> xoaDiem());
        
        JButton btnLamMoi = new JButton("Làm mới");
        btnLamMoi.addActionListener(e -> lamMoi());
        
        buttonPanel.add(btnThem);
        buttonPanel.add(btnSua);
        buttonPanel.add(btnXoa);
        buttonPanel.add(btnLamMoi);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void loadSinhVien() {
        cboSinhVien.removeAllItems();
        List<SinhVien> danhSach = studentController.layTatCaSinhVien();
        for (SinhVien sv : danhSach) {
            cboSinhVien.addItem(sv.getId() + " - " + sv.getMssv() + " - " + sv.getHoTen());
        }
    }
    
    private void loadData() {
        tableModel.setRowCount(0);
        List<Diem> danhSach = controller.layTatCaDiem();
        for (Diem diem : danhSach) {
            Object[] row = {
                diem.getId(),
                diem.getMssv(),
                diem.getHoTen(),
                diem.getMonHoc(),
                diem.getDiemQuaTrinh(),
                diem.getDiemThi(),
                diem.getDiemTongKet()
            };
            tableModel.addRow(row);
        }
    }
    
    private void tinhDiem() {
        try {
            double qt = Double.parseDouble(txtDiemQT.getText());
            double thi = Double.parseDouble(txtDiemThi.getText());
            double tk = Math.round((qt * 0.3 + thi * 0.7) * 10.0) / 10.0;
            txtDiemTK.setText(String.valueOf(tk));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Điểm phải là số!");
        }
    }
    
    private void themDiem() {
        if (!validateInput()) return;
        
        try {
            String selected = (String) cboSinhVien.getSelectedItem();
            int idSV = Integer.parseInt(selected.split(" - ")[0]);
            
            Diem diem = new Diem(
                idSV,
                txtMonHoc.getText().trim(),
                Double.parseDouble(txtDiemQT.getText()),
                Double.parseDouble(txtDiemThi.getText())
            );
            
            if (controller.themDiem(diem)) {
                JOptionPane.showMessageDialog(this, "Thêm điểm thành công!");
                loadData();
                lamMoi();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi thêm điểm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void suaDiem() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn điểm cần sửa!");
            return;
        }
        
        if (!validateInput()) return;
        
        try {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            String selected = (String) cboSinhVien.getSelectedItem();
            int idSV = Integer.parseInt(selected.split(" - ")[0]);
            
            Diem diem = new Diem(
                id,
                idSV,
                null, null,
                txtMonHoc.getText().trim(),
                Double.parseDouble(txtDiemQT.getText()),
                Double.parseDouble(txtDiemThi.getText())
            );
            
            if (controller.capNhatDiem(diem)) {
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
    
    private void xoaDiem() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn điểm cần xóa!");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa điểm này?", 
            "Xác nhận", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            if (controller.xoaDiem(id)) {
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
            for (int i = 0; i < cboSinhVien.getItemCount(); i++) {
                if (cboSinhVien.getItemAt(i).contains(mssv)) {
                    cboSinhVien.setSelectedIndex(i);
                    break;
                }
            }
            
            txtMonHoc.setText((String) tableModel.getValueAt(selectedRow, 3));
            txtDiemQT.setText(tableModel.getValueAt(selectedRow, 4).toString());
            txtDiemThi.setText(tableModel.getValueAt(selectedRow, 5).toString());
            txtDiemTK.setText(tableModel.getValueAt(selectedRow, 6).toString());
        }
    }
    
    private void lamMoi() {
        if (cboSinhVien.getItemCount() > 0) {
            cboSinhVien.setSelectedIndex(0);
        }
        txtMonHoc.setText("");
        txtDiemQT.setText("");
        txtDiemThi.setText("");
        txtDiemTK.setText("");
        table.clearSelection();
        loadData();
    }
    
    private boolean validateInput() {
        if (txtMonHoc.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập môn học!");
            return false;
        }
        try {
            double qt = Double.parseDouble(txtDiemQT.getText());
            double thi = Double.parseDouble(txtDiemThi.getText());
            if (qt < 0 || qt > 10 || thi < 0 || thi > 10) {
                JOptionPane.showMessageDialog(this, "Điểm phải từ 0 đến 10!");
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Điểm phải là số!");
            return false;
        }
        return true;
    }

	public void refreshData() {
		// TODO Auto-generated method stub
		loadSinhVien(); // Cập nhật danh sách sinh viên
        loadData();
	}
}