package view;

import client.ClientService;
import javax.swing.*;
import java.awt.*;

/**
 * Giao diện chính - Client Application
 * Kết nối với Server qua ClientService
 */
public class MainView extends JFrame {
    private JTabbedPane tabbedPane;
    private StudentPanel studentPanel;
    private ClassPanel classPanel;
    private ScorePanel scorePanel;
    private ReportPanel reportPanel;
    private ClientService clientService;
    private JLabel lblConnectionStatus;
    
    public MainView(ClientService clientService) {
        this.clientService = clientService;
        initComponents();
        startConnectionMonitor();
    }
    
    private void initComponents() {
        setTitle("Student Manager System - Client Application");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Menu bar
        createMenuBar();
        
        // Tabbed pane
        tabbedPane = new JTabbedPane();
        
        // Khởi tạo các panel với clientService
        studentPanel = new StudentPanel(clientService);
        classPanel = new ClassPanel(clientService);
        scorePanel = new ScorePanel(clientService);
        reportPanel = new ReportPanel(clientService);
        
        // Thêm tabs
        tabbedPane.addTab("📚 Sinh viên", studentPanel);
        tabbedPane.addTab("🏫 Lớp học", classPanel);
        tabbedPane.addTab("📝 Điểm số", scorePanel);
        tabbedPane.addTab("📊 Báo cáo", reportPanel);
        
        // Lắng nghe chuyển tab
        tabbedPane.addChangeListener(e -> {
            int selectedIndex = tabbedPane.getSelectedIndex();
            switch (selectedIndex) {
                case 0: studentPanel.refreshData(); break;
                case 1: classPanel.refreshData(); break;
                case 2: scorePanel.refreshData(); break;
                case 3: reportPanel.refreshData(); break;
            }
        });
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Status bar
        createStatusBar();
    }
    
    private void createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        
        lblConnectionStatus = new JLabel("🟢 Đã kết nối - Server: localhost:8888");
        lblConnectionStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        leftPanel.add(lblConnectionStatus);
        
        JButton btnReconnect = new JButton("🔄 Kết nối lại");
        btnReconnect.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnReconnect.setFocusPainted(false);
        btnReconnect.addActionListener(e -> reconnectToServer());
        leftPanel.add(btnReconnect);
        
        statusBar.add(leftPanel, BorderLayout.WEST);
        
        JLabel lblTime = new JLabel();
        lblTime.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        
        // Update time mỗi giây
        Timer timer = new Timer(1000, e -> {
            lblTime.setText(java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss - dd/MM/yyyy")));
        });
        timer.start();
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        rightPanel.add(lblTime);
        statusBar.add(rightPanel, BorderLayout.EAST);
        
        add(statusBar, BorderLayout.SOUTH);
    }
    
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // Menu Hệ thống
        JMenu systemMenu = new JMenu("⚙ Hệ thống");
        
        JMenuItem reconnectItem = new JMenuItem("🔄 Kết nối lại Server");
        reconnectItem.addActionListener(e -> reconnectToServer());
        
        JMenuItem exitItem = new JMenuItem("🚪 Thoát");
        exitItem.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn thoát?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                clientService.disconnect();
                System.exit(0);
            }
        });
        
        systemMenu.add(reconnectItem);
        systemMenu.addSeparator();
        systemMenu.add(exitItem);
        
        // Menu Dữ liệu
        JMenu dataMenu = new JMenu("💾 Dữ liệu");
        
        JMenuItem syncItem = new JMenuItem("📥 Đồng bộ JSON");
        syncItem.addActionListener(e -> {
            studentPanel.dongBoJSON();
        });
        
        JMenuItem exportItem = new JMenuItem("📤 Xuất báo cáo");
        exportItem.addActionListener(e -> {
            reportPanel.xuatBaoCao();
        });
        
        dataMenu.add(syncItem);
        dataMenu.add(exportItem);
        
        // Menu Trợ giúp
        JMenu helpMenu = new JMenu("❓ Trợ giúp");
        
        JMenuItem aboutItem = new JMenuItem("ℹ Giới thiệu");
        aboutItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                "   STUDENT MANAGER SYSTEM\n" +
                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
                "📌 Phiên bản: 2.0 (Client-Server)\n" +
                "🏗 Kiến trúc: MVC + Client-Server\n" +
                "💻 Công nghệ:\n" +
                "   • Client: Java Swing\n" +
                "   • Server: Java Socket\n" +
                "   • Database: MySQL (có cấu trúc)\n" +
                "   • NoSQL: JSON (không cấu trúc)\n" +
                "🔌 Kết nối: Socket TCP/IP\n" +
                "📡 Server: localhost:8888\n\n" +
                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━",
                "Về chúng tôi",
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        JMenuItem connectionInfoItem = new JMenuItem("🔌 Thông tin kết nối");
        connectionInfoItem.addActionListener(e -> {
            String status = clientService.isConnected() ? "🟢 Đã kết nối" : "🔴 Ngắt kết nối";
            JOptionPane.showMessageDialog(this,
                "━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                "   THÔNG TIN KẾT NỐI\n" +
                "━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
                "Trạng thái: " + status + "\n" +
                "Server: localhost\n" +
                "Port: 8888\n" +
                "Protocol: TCP/IP Socket\n" +
                "Format: JSON\n\n" +
                "━━━━━━━━━━━━━━━━━━━━━━━━━",
                "Thông tin kết nối",
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        helpMenu.add(connectionInfoItem);
        helpMenu.addSeparator();
        helpMenu.add(aboutItem);
        
        menuBar.add(systemMenu);
        menuBar.add(dataMenu);
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);
    }
    
    /**
     * Kết nối lại Server
     */
    private void reconnectToServer() {
        JDialog progressDialog = new JDialog(this, "Đang kết nối...", true);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel label = new JLabel("Đang kết nối lại Server...");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        
        panel.add(label);
        panel.add(Box.createVerticalStrut(15));
        panel.add(progressBar);
        
        progressDialog.add(panel);
        progressDialog.pack();
        progressDialog.setLocationRelativeTo(this);
        
        new Thread(() -> {
            try {
                Thread.sleep(500);
                clientService.disconnect();
                Thread.sleep(300);
                boolean success = clientService.connect();
                
                SwingUtilities.invokeLater(() -> {
                    progressDialog.dispose();
                    if (success) {
                        updateConnectionStatus(true);
                        JOptionPane.showMessageDialog(this,
                            "✓ Kết nối lại thành công!",
                            "Thành công",
                            JOptionPane.INFORMATION_MESSAGE);
                        refreshAllPanels();
                    } else {
                        updateConnectionStatus(false);
                        JOptionPane.showMessageDialog(this,
                            "✗ Không thể kết nối lại Server!\n" +
                            "Vui lòng kiểm tra Server đã chạy chưa.",
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        
        progressDialog.setVisible(true);
    }

    /**
     * Monitor connection status
     */
    private void startConnectionMonitor() {
        Timer timer = new Timer(5000, e -> {
            boolean connected = clientService.isConnected();
            updateConnectionStatus(connected);
        });
        timer.start();
    }
    
    private void updateConnectionStatus(boolean connected) {
        if (connected) {
            lblConnectionStatus.setText("🟢 Đã kết nối - Server: localhost:8888");
            lblConnectionStatus.setForeground(new Color(39, 174, 96));
        } else {
            lblConnectionStatus.setText("🔴 Mất kết nối - Vui lòng kết nối lại");
            lblConnectionStatus.setForeground(new Color(231, 76, 60));
        }
    }
    
    private void refreshAllPanels() {
        studentPanel.refreshData();
        classPanel.refreshData();
        scorePanel.refreshData();
        reportPanel.refreshData();
    }
   
}