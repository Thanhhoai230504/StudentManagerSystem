package main;
import dao.DatabaseConnection;
import view.MainView;
import javax.swing.*;

/**
 * Main class - Điểm khởi động ứng dụng
 * Student Manager System
 * 
 * @author Your Name
 * @version 1.0
 */
public class Main {
    
    public static void main(String[] args) {
        // Kiểm tra kết nối database trước khi chạy
        if (!DatabaseConnection.testConnection()) {
            JOptionPane.showMessageDialog(null,
                "Không thể kết nối đến MySQL!\n\n" +
                "Vui lòng kiểm tra:\n" +
                "1. MySQL Server đã chạy chưa\n" +
                "2. Thông tin kết nối trong DatabaseConnection.java\n" +
                "3. Database 'student_manager' đã được tạo chưa\n\n" +
                "Chạy file student_manager.sql để tạo database.",
                "Lỗi kết nối Database",
                JOptionPane.ERROR_MESSAGE
            );
            System.exit(1);
        }
        
        // Thiết lập Look and Feel
        try {
            // Sử dụng Nimbus Look and Feel
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Nếu Nimbus không có, sử dụng default
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        
        // Khởi chạy ứng dụng trong Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                // Hiển thị splash screen (tùy chọn)
                showSplashScreen();
                
                // Khởi tạo Main View
                MainView mainView = new MainView();
                mainView.setVisible(true);
                
                System.out.println("=================================");
                System.out.println("Student Manager System đã khởi động!");
                System.out.println("=================================");
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                    "Lỗi khởi động ứng dụng: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
                );
                e.printStackTrace();
                System.exit(1);
            }
        });
        
        // Đóng kết nối khi thoát ứng dụng
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Đang đóng kết nối database...");
            DatabaseConnection.closeConnection();
            System.out.println("Ứng dụng đã thoát an toàn.");
        }));
    }
    
    /**
     * Hiển thị splash screen khi khởi động
     */
    private static void showSplashScreen() {
        JWindow splash = new JWindow();
        JPanel content = new JPanel();
        content.setBackground(new java.awt.Color(25, 118, 210));
        content.setLayout(new java.awt.BorderLayout());
        
        JLabel title = new JLabel("STUDENT MANAGER SYSTEM", SwingConstants.CENTER);
        title.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 24));
        title.setForeground(java.awt.Color.WHITE);
        
        JLabel subtitle = new JLabel("Đang khởi động...", SwingConstants.CENTER);
        subtitle.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 14));
        subtitle.setForeground(java.awt.Color.WHITE);
        
        JPanel textPanel = new JPanel(new java.awt.GridLayout(2, 1));
        textPanel.setOpaque(false);
        textPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        textPanel.add(title);
        textPanel.add(subtitle);
        
        content.add(textPanel, java.awt.BorderLayout.CENTER);
        
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        content.add(progressBar, java.awt.BorderLayout.SOUTH);
        
        splash.setContentPane(content);
        splash.setSize(400, 200);
        splash.setLocationRelativeTo(null);
        splash.setVisible(true);
        
        // Đóng splash screen sau 2 giây
        Timer timer = new Timer(2000, e -> splash.dispose());
        timer.setRepeats(false);
        timer.start();
        
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
