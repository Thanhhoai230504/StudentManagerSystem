package view;

import client.ClientService;
import controller.ReportController;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.Map;

/**
 * Panel báo cáo và thống kê - CLIENT-SERVER VERSION
 */
public class ReportPanel extends JPanel {
    private ReportController controller;
    private JPanel mainContentPanel;
    private CardLayout cardLayout;
    
    // Color scheme
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private final Color WARNING_COLOR = new Color(243, 156, 18);
    private final Color DANGER_COLOR = new Color(231, 76, 60);
    private final Color LIGHT_BG = new Color(236, 240, 241);
    private final Color CARD_BG = Color.WHITE;
    
    // CONSTRUCTOR MỚI - NHẬN ClientService
    public ReportPanel(ClientService clientService) {
        controller = new ReportController(clientService);
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBackground(LIGHT_BG);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header panel
        add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Main content area với CardLayout
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(LIGHT_BG);
        
        // Thêm các card
        mainContentPanel.add(createDashboardCard(), "dashboard");
        mainContentPanel.add(createClassStatsCard(), "class");
        mainContentPanel.add(createScoreStatsCard(), "score");
        mainContentPanel.add(createGenderStatsCard(), "gender");
        
        add(mainContentPanel, BorderLayout.CENTER);
        
        // Hiển thị dashboard mặc định
        cardLayout.show(mainContentPanel, "dashboard");
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        // Title
        JLabel lblTitle = new JLabel("📊 Báo cáo & Thống kê");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(PRIMARY_COLOR);
        panel.add(lblTitle, BorderLayout.WEST);
        
        // Button panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setBackground(CARD_BG);
        
        JButton btnRefresh = createStyledButton("🔄 Làm mới", PRIMARY_COLOR);
        btnRefresh.addActionListener(e -> refreshData());
        
        JButton btnExportJSON = createStyledButton("💾 Xuất JSON", SUCCESS_COLOR);
        btnExportJSON.addActionListener(e -> xuatBaoCaoJSON());
        
        btnPanel.add(btnRefresh);
        btnPanel.add(btnExportJSON);
        
        panel.add(btnPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createDashboardCard() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 15, 15));
        panel.setBackground(LIGHT_BG);
        
        // Stat cards
        panel.add(createStatCard("👥 Tổng sinh viên", 
            String.valueOf(getTongSinhVien()), 
            PRIMARY_COLOR,
            e -> cardLayout.show(mainContentPanel, "class")));
        
        panel.add(createStatCard("🏫 Số lượng lớp", 
            String.valueOf(getSoLop()), 
            SUCCESS_COLOR,
            e -> cardLayout.show(mainContentPanel, "class")));
        
        panel.add(createStatCard("📝 Điểm TB chung", 
            String.format("%.2f", getDiemTBChung()), 
            WARNING_COLOR,
            e -> cardLayout.show(mainContentPanel, "score")));
        
        panel.add(createStatCard("⚧ Tỷ lệ Nam/Nữ", 
            getTyLeGioiTinh(), 
            DANGER_COLOR,
            e -> cardLayout.show(mainContentPanel, "gender")));
        
        // Detail cards
        JPanel detailPanel = new JPanel(new BorderLayout(15, 15));
        detailPanel.setBackground(LIGHT_BG);
        
        detailPanel.add(createQuickStatsPanel(), BorderLayout.CENTER);
        
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(LIGHT_BG);
        wrapper.add(panel, BorderLayout.NORTH);
        wrapper.add(detailPanel, BorderLayout.CENTER);
        
        return wrapper;
    }
    
    private JPanel createStatCard(String title, String value, Color color, java.awt.event.ActionListener listener) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Title
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTitle.setForeground(new Color(127, 140, 141));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Value
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblValue.setForeground(color);
        lblValue.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        card.add(lblTitle);
        card.add(Box.createVerticalStrut(10));
        card.add(lblValue);
        
        // Click listener
        if (listener != null) {
            card.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    listener.actionPerformed(null);
                }
                
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    card.setBackground(LIGHT_BG);
                }
                
                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    card.setBackground(CARD_BG);
                }
            });
        }
        
        return card;
    }
    
    private JPanel createQuickStatsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel title = new JLabel("📈 Chi tiết thống kê");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(PRIMARY_COLOR);
        panel.add(title, BorderLayout.NORTH);
        
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(CARD_BG);
        content.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
        // Thống kê theo lớp
        Map<String, Integer> soLuong = controller.thongKeSoLuongTheoLop();
        for (Map.Entry<String, Integer> entry : soLuong.entrySet()) {
            content.add(createProgressBar(entry.getKey(), entry.getValue(), getTongSinhVien()));
            content.add(Box.createVerticalStrut(10));
        }
        
        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createProgressBar(String label, int value, int max) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBackground(CARD_BG);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        
        JLabel lblText = new JLabel(label);
        lblText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        JLabel lblValue = new JLabel(String.valueOf(value));
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblValue.setForeground(PRIMARY_COLOR);
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(CARD_BG);
        topPanel.add(lblText, BorderLayout.WEST);
        topPanel.add(lblValue, BorderLayout.EAST);
        
        JProgressBar progressBar = new JProgressBar(0, max);
        progressBar.setValue(value);
        progressBar.setStringPainted(true);
        progressBar.setString(String.format("%.1f%%", (value * 100.0 / max)));
        progressBar.setForeground(PRIMARY_COLOR);
        progressBar.setBackground(LIGHT_BG);
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(progressBar, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createClassStatsCard() {
        return createTableCard("📚 Thống kê chi tiết theo lớp", 
            new String[]{"Lớp", "Số sinh viên", "Tỷ lệ"}, 
            getClassStatsData());
    }
    
    private JPanel createScoreStatsCard() {
        return createTableCard("📝 Thống kê điểm trung bình theo lớp", 
            new String[]{"Lớp", "Điểm TB", "Xếp loại"}, 
            getScoreStatsData());
    }
    
    private JPanel createGenderStatsCard() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel title = new JLabel("⚧ Thống kê theo giới tính");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(PRIMARY_COLOR);
        
        JButton btnBack = createStyledButton("⬅ Quay lại", new Color(149, 165, 166));
        btnBack.addActionListener(e -> cardLayout.show(mainContentPanel, "dashboard"));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(CARD_BG);
        headerPanel.add(title, BorderLayout.WEST);
        headerPanel.add(btnBack, BorderLayout.EAST);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Gender stats
        Map<String, Integer> gioiTinh = controller.thongKeTheoGioiTinh();
        int nam = gioiTinh.getOrDefault("Nam", 0);
        int nu = gioiTinh.getOrDefault("Nữ", 0);
        int total = nam + nu;
        
        JPanel statsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        statsPanel.setBackground(CARD_BG);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        // Nam
        JPanel namPanel = createGenderPanel("👨 Nam", nam, total, PRIMARY_COLOR);
        statsPanel.add(namPanel);
        
        // Nữ
        JPanel nuPanel = createGenderPanel("👩 Nữ", nu, total, new Color(231, 76, 60));
        statsPanel.add(nuPanel);
        
        panel.add(statsPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createGenderPanel(String title, int count, int total, Color color) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(LIGHT_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(color);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblCount = new JLabel(String.valueOf(count));
        lblCount.setFont(new Font("Segoe UI", Font.BOLD, 48));
        lblCount.setForeground(color);
        lblCount.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        double percent = total > 0 ? (count * 100.0 / total) : 0;
        JLabel lblPercent = new JLabel(String.format("%.1f%%", percent));
        lblPercent.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        lblPercent.setForeground(new Color(127, 140, 141));
        lblPercent.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(lblTitle);
        panel.add(Box.createVerticalStrut(20));
        panel.add(lblCount);
        panel.add(Box.createVerticalStrut(10));
        panel.add(lblPercent);
        
        return panel;
    }
    
    private JPanel createTableCard(String title, String[] columns, Object[][] data) {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(PRIMARY_COLOR);
        
        JButton btnBack = createStyledButton("⬅ Quay lại", new Color(149, 165, 166));
        btnBack.addActionListener(e -> cardLayout.show(mainContentPanel, "dashboard"));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(CARD_BG);
        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(btnBack, BorderLayout.EAST);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        
        JTable table = new JTable(data, columns);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(35);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(LIGHT_BG);
        table.getTableHeader().setForeground(PRIMARY_COLOR);
        table.setGridColor(new Color(189, 195, 199));
        table.setSelectionBackground(new Color(52, 152, 219, 50));
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(color.darker());
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(color);
            }
        });
        
        return button;
    }
    
    // Helper methods
    private int getTongSinhVien() {
        return controller.thongKeSoLuongTheoLop().values().stream()
            .mapToInt(Integer::intValue).sum();
    }
    
    private int getSoLop() {
        return controller.thongKeSoLuongTheoLop().size();
    }
    
    private double getDiemTBChung() {
        Map<String, Double> diemTB = controller.thongKeDiemTrungBinhTheoLop();
        if (diemTB.isEmpty()) return 0.0;
        return diemTB.values().stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }
    
    private String getTyLeGioiTinh() {
        Map<String, Integer> gt = controller.thongKeTheoGioiTinh();
        int nam = gt.getOrDefault("Nam", 0);
        int nu = gt.getOrDefault("Nữ", 0);
        return nam + "/" + nu;
    }
    
    private Object[][] getClassStatsData() {
        Map<String, Integer> data = controller.thongKeSoLuongTheoLop();
        int total = getTongSinhVien();
        Object[][] result = new Object[data.size()][3];
        int i = 0;
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            result[i][0] = entry.getKey();
            result[i][1] = entry.getValue();
            result[i][2] = String.format("%.1f%%", (entry.getValue() * 100.0 / total));
            i++;
        }
        return result;
    }
    
    private Object[][] getScoreStatsData() {
        Map<String, Double> data = controller.thongKeDiemTrungBinhTheoLop();
        Object[][] result = new Object[data.size()][3];
        int i = 0;
        for (Map.Entry<String, Double> entry : data.entrySet()) {
            result[i][0] = entry.getKey();
            result[i][1] = String.format("%.2f", entry.getValue());
            result[i][2] = getXepLoai(entry.getValue());
            i++;
        }
        return result;
    }
    
    private String getXepLoai(double diem) {
        if (diem >= 8.0) return "🏆 Giỏi";
        if (diem >= 6.5) return "⭐ Khá";
        if (diem >= 5.0) return "✓ Trung bình";
        return "⚠ Yếu";
    }
    
    private void xuatBaoCaoJSON() {
        boolean success = controller.xuatBaoCaoJSON();
        if (success) {
            JOptionPane.showMessageDialog(this,
                "✓ Xuất báo cáo JSON thành công!\n\n" +
                "📊 File báo cáo chi tiết:\n" +
                "   📁 data/report_full.json\n" +
                "   📋 Bao gồm:\n" +
                "      • Danh sách sinh viên + điểm số\n" +
                "      • Thống kê theo lớp (sĩ số, điểm TB, giới tính)\n" +
                "      • Thống kê theo môn học (điểm TB, cao nhất, thấp nhất)\n" +
                "      • Thống kê tổng hợp (giới tính, xếp loại)\n" +
                "      • Metadata (ngày xuất, phiên bản)\n\n" +
                "📄 File danh sách sinh viên:\n" +
                "   📁 data/students.json\n" +
                "   📋 Danh sách sinh viên đơn giản",
                "Thành công",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                "✗ Lỗi xuất báo cáo JSON!\n\n" +
                "Vui lòng kiểm tra:\n" +
                "• Thư mục data/ đã tồn tại\n" +
                "• Quyền ghi file\n" +
                "• Thư viện Gson đã được thêm\n" +
                "• Có dữ liệu trong hệ thống",
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void refreshData() {
        // Refresh lại card hiện tại
        mainContentPanel.removeAll();
        mainContentPanel.add(createDashboardCard(), "dashboard");
        mainContentPanel.add(createClassStatsCard(), "class");
        mainContentPanel.add(createScoreStatsCard(), "score");
        mainContentPanel.add(createGenderStatsCard(), "gender");
        cardLayout.show(mainContentPanel, "dashboard");
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }
    public void xuatBaoCao() {
        xuatBaoCaoJSON();
    }
}