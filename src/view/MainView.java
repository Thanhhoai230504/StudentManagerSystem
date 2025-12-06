package view;

import javax.swing.*;
import java.awt.*;

/**
 * Giao diện chính của ứng dụng
 */
public class MainView extends JFrame {
    private JTabbedPane tabbedPane;
    private StudentPanel studentPanel;
    private ClassPanel classPanel;
    private ScorePanel scorePanel;
    private ReportPanel reportPanel;
    
    public MainView() {
        initComponents();
    }
    
    private void initComponents() {
        setTitle("Student Manager System - Hệ thống Quản lý Sinh viên");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Tạo menu bar
        createMenuBar();
        
        // Tạo tabbed pane
        tabbedPane = new JTabbedPane();
        
        // Khởi tạo các panel
        studentPanel = new StudentPanel();
        classPanel = new ClassPanel();
        scorePanel = new ScorePanel();
        reportPanel = new ReportPanel();
        
        // Thêm các tab
        tabbedPane.addTab("Quản lý Sinh viên", new ImageIcon(), studentPanel, "Thêm, sửa, xóa, tìm kiếm sinh viên");
        tabbedPane.addTab("Quản lý Lớp", new ImageIcon(), classPanel, "Quản lý lớp học");
        tabbedPane.addTab("Quản lý Điểm", new ImageIcon(), scorePanel, "Nhập và quản lý điểm");
        tabbedPane.addTab("Báo cáo & Thống kê", new ImageIcon(), reportPanel, "Xem báo cáo và thống kê");
        
        // Lắng nghe sự kiện chuyển tab để tự động refresh dữ liệu
        tabbedPane.addChangeListener(e -> {
            int selectedIndex = tabbedPane.getSelectedIndex();
            switch (selectedIndex) {
                case 0: // Tab Sinh viên
                    studentPanel.refreshData();
                    break;
                case 1: // Tab Lớp
                    classPanel.refreshData();
                    break;
                case 2: // Tab Điểm
                    scorePanel.refreshData();
                    break;
                case 3: // Tab Báo cáo
                    reportPanel.refreshData();
                    break;
            }
        });
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Tạo status bar
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel statusLabel = new JLabel("Sẵn sàng");
        statusBar.add(statusLabel);
        add(statusBar, BorderLayout.SOUTH);
    }
    
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // Menu Hệ thống
        JMenu systemMenu = new JMenu("Hệ thống");
        JMenuItem exitItem = new JMenuItem("Thoát");
        exitItem.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn thoát?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
            );
            if (choice == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });
        systemMenu.add(exitItem);
        
        // Menu Dữ liệu
        JMenu dataMenu = new JMenu("Dữ liệu");
        JMenuItem syncItem = new JMenuItem("Đồng bộ JSON");
        syncItem.addActionListener(e -> {
            studentPanel.dongBoJSON();
            JOptionPane.showMessageDialog(this, "Đã đồng bộ dữ liệu sang JSON!");
        });
        dataMenu.add(syncItem);
        
        // Menu Trợ giúp
        JMenu helpMenu = new JMenu("Trợ giúp");
        JMenuItem aboutItem = new JMenuItem("Giới thiệu");
        aboutItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                this,
                "Student Manager System v1.0\n" +
                "Hệ thống Quản lý Sinh viên\n" +
                "Công nghệ: Java Swing, MySQL, JSON\n" +
                "Mô hình: MVC",
                "Giới thiệu",
                JOptionPane.INFORMATION_MESSAGE
            );
        });
        helpMenu.add(aboutItem);
        
        menuBar.add(systemMenu);
        menuBar.add(dataMenu);
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);
    }
    
    public static void main(String[] args) {
        // Sử dụng Nimbus Look and Feel cho giao diện đẹp hơn
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Nếu Nimbus không có, sử dụng default
        }
        
        SwingUtilities.invokeLater(() -> {
            MainView mainView = new MainView();
            mainView.setVisible(true);
        });
    }
}


