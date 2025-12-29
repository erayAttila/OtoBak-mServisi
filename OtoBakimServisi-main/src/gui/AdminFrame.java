package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import service.AppointmentService;
import service.CarService;
import service.ServiceService;
import service.UserService;
import model.Appointment;
import model.Car;
import model.Service;
import model.User;
import java.awt.*;
import java.util.List;

public class AdminFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private AppointmentService appointmentService = new AppointmentService();
    private CarService carService = new CarService();
    private ServiceService serviceService = new ServiceService();
    private UserService userService = new UserService();
    
    private DefaultTableModel appointmentTableModel;
    private DefaultTableModel serviceTableModel;
    
    private Font tableFont = new Font("Segoe UI", Font.PLAIN, 15);
    private Font headerFont = new Font("Segoe UI", Font.BOLD, 15);
    private Font buttonFont = new Font("Segoe UI", Font.BOLD, 14);

    public AdminFrame() {
        setTitle("Oto Servis Yönetim Paneli (ADMİN)");
        setSize(1250, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));

        tabbedPane.add("📋 Randevu Yönetimi", createAppointmentManagementPanel());
        tabbedPane.add("🛠️ Hizmet Yönetimi", createServiceManagementPanel());

        add(tabbedPane);
    }

    private JPanel createAppointmentManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String[] kolonlar = {"ID", "Müşteri", "Telefon", "Araç/Plaka", "Hizmet", "Tarih/Saat", "Durum"};
        appointmentTableModel = new DefaultTableModel(kolonlar, 0);
       
        JTable table = createStyledTable(appointmentTableModel, true);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        JButton btnApprove = createLargeButton("✅ SEÇİLİ RANDEVUYU ONAYLA", new Color(46, 204, 113));
        JButton btnCancel = createLargeButton("❌ SEÇİLİ RANDEVUYU İPTAL ET", new Color(231, 76, 60));

        buttonPanel.add(btnApprove);
        buttonPanel.add(btnCancel);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        btnApprove.addActionListener(e -> updateAppointmentStatus(table, "APPROVED"));
        btnCancel.addActionListener(e -> updateAppointmentStatus(table, "CANCELLED"));

        loadAllAppointments();
        return panel;
    }

    private JPanel createServiceManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel addServicePanel = new JPanel();
        addServicePanel.setLayout(new BoxLayout(addServicePanel, BoxLayout.Y_AXIS));
        addServicePanel.setBackground(Color.WHITE);
        addServicePanel.setBorder(BorderFactory.createTitledBorder("Yeni Hizmet Ekle"));

        JTextField txtServiceName = new JTextField();
        JTextField txtDuration = new JTextField();
        JTextField txtPrice = new JTextField();

        addServicePanel.add(createLabeledInput("Hizmet Adı:", txtServiceName));
        addServicePanel.add(createLabeledInput("Süre (Dakika):", txtDuration));
        addServicePanel.add(createLabeledInput("Fiyat (TL):", txtPrice));

        JButton btnAddService = createLargeButton("➕ HİZMETİ SİSTEME EKLE", new Color(52, 152, 219));
        btnAddService.setAlignmentX(Component.CENTER_ALIGNMENT);
        addServicePanel.add(btnAddService);
        addServicePanel.add(Box.createRigidArea(new Dimension(0, 10)));

        panel.add(addServicePanel, BorderLayout.NORTH);

        String[] kolonlar = {"ID", "Hizmet Adı", "Süre (Dk)", "Fiyat (TL)"};
        serviceTableModel = new DefaultTableModel(kolonlar, 0);
        
        JTable table = createStyledTable(serviceTableModel, false);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton btnDeleteService = createLargeButton("🗑️ SEÇİLİ HİZMETİ SİL", new Color(231, 76, 60));
        panel.add(btnDeleteService, BorderLayout.SOUTH);

        btnAddService.addActionListener(e -> {
            try {
                String name = txtServiceName.getText().trim();
                int duration = Integer.parseInt(txtDuration.getText().trim());
                double price = Double.parseDouble(txtPrice.getText().trim());

                if (serviceService.addService(name, duration, price)) {
                    JOptionPane.showMessageDialog(this, "Hizmet başarıyla eklendi.");
                    loadServices();
                    txtServiceName.setText(""); txtDuration.setText(""); txtPrice.setText("");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Hata: Lütfen bilgileri kontrol edin.");
            }
        });

        btnDeleteService.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                int id = (int) serviceTableModel.getValueAt(row, 0);
                if (serviceService.deleteService(id)) {
                    JOptionPane.showMessageDialog(this, "Hizmet silindi.");
                    loadServices();
                }
            }
        });

        loadServices();
        return panel;
    }

    
    private JTable createStyledTable(DefaultTableModel model, boolean isAppointmentTable) {
        JTable table = new JTable(model);
        table.setRowHeight(40);
        table.setFont(tableFont);
        JTableHeader header = table.getTableHeader();
        header.setFont(headerFont);

        if (isAppointmentTable) {
           
            table.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    
                    if (value != null) {
                        String status = value.toString();
                        setHorizontalAlignment(JLabel.CENTER);

                        if (status.equalsIgnoreCase("Onaylandı")) {
                            c.setForeground(new Color(39, 174, 96)); // Yeşil
                            setFont(new Font("Segoe UI", Font.BOLD, 15));
                        } else if (status.equalsIgnoreCase("Beklemede")) {
                            c.setForeground(new Color(243, 156, 18)); // Turuncu
                            setFont(new Font("Segoe UI", Font.BOLD, 15));
                        } else if (status.equalsIgnoreCase("İptal Edildi")) {
                            c.setForeground(new Color(231, 76, 60)); // Kırmızı
                            setFont(new Font("Segoe UI", Font.BOLD, 15));
                        }
                    }

                    if (isSelected) {
                        c.setForeground(Color.WHITE);
                    }
                    return c;
                }
            });
        }
        return table;
    }

    private JPanel createLabeledInput(String labelText, JTextField field) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Color.WHITE);
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 13));
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        field.setMaximumSize(new Dimension(400, 35));
        field.setAlignmentX(Component.CENTER_ALIGNMENT);
        field.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        p.add(lbl); p.add(Box.createRigidArea(new Dimension(0, 5))); p.add(field); p.add(Box.createRigidArea(new Dimension(0, 10)));
        return p;
    }

    private JButton createLargeButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(buttonFont);
        btn.setBackground(bg);
        btn.setForeground(Color.BLACK);
        btn.setPreferredSize(new Dimension(0, 50));
        btn.setFocusPainted(false);
        return btn;
    }

    private void loadAllAppointments() {
        appointmentTableModel.setRowCount(0);
        List<Appointment> apps = appointmentService.getAllAppointments();
        for (Appointment a : apps) {
            User u = userService.getUserById(a.getUserId());
            Car c = carService.getCarById(a.getCarId());
            Service s = serviceService.getServiceById(a.getServiceId());
            String durum = "Beklemede";
            if("APPROVED".equals(a.getStatus())) durum = "Onaylandı";
            else if("CANCELLED".equals(a.getStatus())) durum = "İptal Edildi";

            appointmentTableModel.addRow(new Object[]{
                a.getAppointmentId(), (u != null ? u.getName() : "-"), (u != null ? u.getPhone() : "-"),
                (c != null ? c.getPlate() : "-"), (s != null ? s.getName() : "-"),
                a.getDate() + " " + a.getTime(), durum
            });
        }
    }

    private void loadServices() {
        serviceTableModel.setRowCount(0);
        serviceService.getAllServices().forEach(s -> {
            serviceTableModel.addRow(new Object[]{s.getServiceId(), s.getName(), s.getDuration(), s.getPrice()});
        });
    }

    private void updateAppointmentStatus(JTable table, String status) {
        int row = table.getSelectedRow();
        if (row != -1) {
            int id = (int) appointmentTableModel.getValueAt(row, 0);
            if (appointmentService.updateStatus(id, status)) {
                loadAllAppointments();
            }
        }
    }
}