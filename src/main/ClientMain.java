package main;

import client.ClientService;
import view.MainView;
import javax.swing.*;

/**
 * Main class cho Client Application
 * Kết nối đến Server và khởi chạy GUI
 */
public class ClientMain {
    
    public static void main(String[] args) {
        // Thiết lập Look and Feel
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        
        // Khởi tạo ClientService
        ClientService clientService = new ClientService();
        
        // Thử kết nối đến Server
        SwingUtilities.invokeLater(() -> {
            // Hiển thị dialog kết nối
            JDialog connectDialog = new JDialog();
            connectDialog.setTitle("Đang kết nối Server...");
            connectDialog.setModal(true);
            connectDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
            
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            JLabel lblMessage = new JLabel("Đang kết nối đến Server...");
            lblMessage.setAlignmentX(JLabel.CENTER_ALIGNMENT);
            
            JProgressBar progressBar = new JProgressBar();
            progressBar.setIndeterminate(true);
            
            panel.add(lblMessage);
            panel.add(Box.createVerticalStrut(15));
            panel.add(progressBar);
            
            connectDialog.add(panel);
            connectDialog.pack();
            connectDialog.setLocationRelativeTo(null);
            
            // Thử kết nối trong thread riêng
            new Thread(() -> {
                try {
                    Thread.sleep(500); // Delay ngắn để hiển thị dialog
                    
                    boolean connected = clientService.connect();
                    
                    SwingUtilities.invokeLater(() -> {
                        connectDialog.dispose();
                        
                        if (connected) {
                            // Kết nối thành công - khởi động GUI
                            JOptionPane.showMessageDialog(null,
                                "✓ Kết nối Server thành công!\n\n" +
                                "Server: localhost:8888\n" +
                                "Trạng thái: Sẵn sàng",
                                "Kết nối thành công",
                                JOptionPane.INFORMATION_MESSAGE);
                            
                            // Khởi tạo và hiển thị MainView
                            MainView mainView = new MainView(clientService);
                            mainView.setVisible(true);
                            
                            System.out.println("=================================");
                            System.out.println("Client Application đã khởi động!");
                            System.out.println("=================================");
                            
                        } else {
                            // Kết nối thất bại
                            int choice = JOptionPane.showConfirmDialog(null,
                                "✗ Không thể kết nối đến Server!\n\n" +
                                "Vui lòng kiểm tra:\n" +
                                "1. Server đã chạy chưa?\n" +
                                "2. Địa chỉ: localhost:8888\n" +
                                "3. Firewall có chặn không?\n\n" +
                                "Bạn có muốn thử lại?",
                                "Lỗi kết nối",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.ERROR_MESSAGE);
                            
                            if (choice == JOptionPane.YES_OPTION) {
                                // Thử lại
                                main(args);
                            } else {
                                System.exit(0);
                            }
                        }
                    });
                    
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
            
            connectDialog.setVisible(true);
        });
        
        // Shutdown hook - ngắt kết nối khi thoát
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n⏹ Đang ngắt kết nối Server...");
            clientService.disconnect();
            System.out.println("✓ Client đã thoát an toàn");
        }));
    }
}