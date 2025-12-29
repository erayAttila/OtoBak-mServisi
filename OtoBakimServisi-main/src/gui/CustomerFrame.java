package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import model.User;
import model.Car;
import model.Service;
import service.CarService;
import service.AppointmentService;
import service.ServiceService;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import java.util.TreeMap;

public class CustomerFrame extends JFrame {

    private static final long serialVersionUID = -3022569469026363011L;
    private User user;

    private CarService carService = new CarService();
    private AppointmentService appointmentService = new AppointmentService();
    private ServiceService serviceService = new ServiceService();

    private DefaultTableModel appointmentsTableModel;
    private JComboBox<Car> cmbCars = new JComboBox<>();
    private Map<String, String[]> markaModelMap = new TreeMap<>();

    private Font labelFont = new Font("Segoe UI Semibold", Font.PLAIN, 15);
    private Font inputFont = new Font("Segoe UI", Font.PLAIN, 15);
    private Font buttonFont = new Font("Segoe UI", Font.BOLD, 15);

    public CustomerFrame(User user) {
        this.user = user;
        verileriHazirla(); 

        setTitle("Oto Servis Müşteri Paneli - " + user.getName());
        setSize(1000, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));

        tabbedPane.add("Araçlarım", createCarPanel());
        tabbedPane.add("Randevu Al", createAppointmentPanel());
        tabbedPane.add("Randevularım", createAppointmentsListPanel());

       
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 1) { 
                refreshCarComboBox();
            }
        });

        add(tabbedPane);
        refreshCarComboBox(); // İlk açılışta yükle
    }

    private void verileriHazirla() {
        markaModelMap.put("Seçiniz", new String[]{"Önce Marka Seçin"});
        markaModelMap.put("Alfa Romeo", new String[]{"Giulietta", "Giulia", "Stelvio", "Tonale"});
        markaModelMap.put("Audi", new String[]{"A1", "A3", "A4", "A5", "A6", "Q2", "Q3", "Q5", "Q7"});
        markaModelMap.put("BMW", new String[]{"1 Serisi", "2 Serisi", "3 Serisi", "4 Serisi", "5 Serisi", "X1", "X3", "X5"});
        markaModelMap.put("Fiat", new String[]{"Egea Sedan", "Egea Cross", "Doblo", "Fiorino", "500", "Linea"});
        markaModelMap.put("Ford", new String[]{"Fiesta", "Focus", "Puma", "Kuga", "Courier"});
        markaModelMap.put("Honda", new String[]{"Civic", "City", "Accord", "CR-V"});
        markaModelMap.put("Hyundai", new String[]{"i10", "i20", "Bayon", "Tucson", "Kona"});
        markaModelMap.put("Mercedes-Benz", new String[]{"A-Serisi", "C-Serisi", "E-Serisi", "CLA", "GLA", "GLC"});
        markaModelMap.put("Peugeot", new String[]{"208", "308", "408", "2008", "3008", "5008"});
        markaModelMap.put("Renault", new String[]{"Clio", "Megane Sedan", "Austral", "Captur", "Taliant", "Kangoo"});
        markaModelMap.put("Togg", new String[]{"T10X", "T10F"});
        markaModelMap.put("Volkswagen", new String[]{"Polo", "Golf", "Passat", "Tiguan", "T-Roc", "Caddy", "Caravelle"});
        markaModelMap.put("Volvo", new String[]{"S60", "S90", "XC40", "XC60", "XC90"});
    }

    private JPanel createCarPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 242, 245));

        JPanel formWrapper = new JPanel(new GridBagLayout());
        formWrapper.setOpaque(false);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(25, 40, 25, 40)
        ));

        JTextField txtPlaka = new JTextField();
        JTextField txtYil = new JTextField();
        setupInput(txtPlaka);
        setupInput(txtYil);

        JComboBox<String> cmbMarka = new JComboBox<>(markaModelMap.keySet().toArray(new String[0]));
        JComboBox<String> cmbModel = new JComboBox<>(markaModelMap.get("Seçiniz"));
        setupCombo(cmbMarka);
        setupCombo(cmbModel);

        cmbMarka.addActionListener(e -> {
            String selectedBrand = (String) cmbMarka.getSelectedItem();
            cmbModel.removeAllItems();
            if (markaModelMap.containsKey(selectedBrand)) {
                for (String m : markaModelMap.get(selectedBrand)) {
                    cmbModel.addItem(m);
                }
            }
        });

        JButton btnEkle = new JButton("ARACI SİSTEME KAYDET");
        btnEkle.setFont(buttonFont);
        btnEkle.setBackground(new Color(220, 220, 220));
        btnEkle.setForeground(Color.BLACK);
        btnEkle.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        card.add(createLabeledInput("Araç Plakası:", txtPlaka));
        card.add(createLabeledInput("Marka:", cmbMarka));
        card.add(createLabeledInput("Model:", cmbModel));
        card.add(createLabeledInput("Üretim Yılı:", txtYil));
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        card.add(btnEkle);

        formWrapper.add(card);
        panel.add(formWrapper, BorderLayout.NORTH);

        String[] kolonlar = {"ID", "Plaka", "Marka", "Model", "Yıl"};
        DefaultTableModel tableModel = new DefaultTableModel(kolonlar, 0);
        JTable table = createStyledTable(tableModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        Runnable tabloyuDoldur = () -> {
            tableModel.setRowCount(0);
            carService.getCarsByUser(user.getUserId()).forEach(car -> {
                tableModel.addRow(new Object[]{car.getCarId(), car.getPlate(),
                        car.getBrand(), car.getModel(), car.getYear()});
            });
        };
        tabloyuDoldur.run();

        btnEkle.addActionListener(e -> {
            try {
                if (carService.addCar(user.getUserId(), txtPlaka.getText().trim(),
                        (String) cmbMarka.getSelectedItem(), (String) cmbModel.getSelectedItem(),
                        Integer.parseInt(txtYil.getText().trim()))) {
                    JOptionPane.showMessageDialog(this, "Araç Kaydedildi");
                    tabloyuDoldur.run();
                    refreshCarComboBox();
                    txtPlaka.setText("");
                    txtYil.setText("");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Hata: Bilgileri kontrol ediniz.");
            }
        });

        return panel;
    }

    private JPanel createAppointmentPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 242, 245));

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(30, 45, 30, 45)
        ));
        card.setPreferredSize(new Dimension(500, 620));

        JLabel lblTitle = new JLabel("Randevu Talebi Oluştur");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(41, 128, 185));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblTitle);
        card.add(Box.createRigidArea(new Dimension(0, 25)));

        setupCombo(cmbCars);
        card.add(createLabeledInput("İşlem Yapılacak Araç:", cmbCars));

        // --- HİZMET SEÇİMİ VE SÜRE GÖSTERİMİ DÜZELTMESİ BURADA ---
        JComboBox<Service> cmbServices = new JComboBox<>();
        setupCombo(cmbServices);
        cmbServices.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Service) {
                    Service s = (Service) value;
                    setText(s.getName() + " (" + s.getDuration() + " dk) - " + s.getPrice() + " TL");
                }
                return this;
            }
        });
        serviceService.getAllServices().forEach(cmbServices::addItem);
        card.add(createLabeledInput("Hizmet Seçimi:", cmbServices));
        // ---------------------------------------------------------

        // Tarih Kutuları
        String[] days = new String[31];
        for (int i = 0; i < 31; i++) days[i] = String.format("%02d", i + 1);
        String[] months = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
        String[] years = {"2025", "2026"};

        JComboBox<String> cmbD = new JComboBox<>(days);
        JComboBox<String> cmbM = new JComboBox<>(months);
        JComboBox<String> cmbY = new JComboBox<>(years);
        setupCombo(cmbD); setupCombo(cmbM); setupCombo(cmbY);

        JPanel dRow = new JPanel(new GridLayout(1, 3, 10, 0));
        dRow.setBackground(Color.WHITE);
        dRow.add(cmbD); dRow.add(cmbM); dRow.add(cmbY);
        card.add(createLabeledInput("Tarih:", dRow));

        // Saat Kutusu
        String[] hrs = {"08:00", "08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30",
                        "13:00", "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00"};
        JComboBox<String> cmbT = new JComboBox<>(hrs);
        setupCombo(cmbT);
        card.add(createLabeledInput("Saat:", cmbT));

        JButton btnCr = new JButton("RANDEVU TALEBİNİ GÖNDER");
        btnCr.setFont(buttonFont);
        btnCr.setBackground(new Color(220, 220, 220));
        btnCr.setForeground(Color.BLACK);
        btnCr.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(Box.createRigidArea(new Dimension(0, 20)));
        card.add(btnCr);

        btnCr.addActionListener(e -> {
            try {
                Car c = (Car) cmbCars.getSelectedItem();
                Service s = (Service) cmbServices.getSelectedItem();
                String dt = cmbY.getSelectedItem() + "-" + cmbM.getSelectedItem() + "-" + cmbD.getSelectedItem();

                if (appointmentService.createAppointment(user.getUserId(),
                        c.getCarId(), s.getServiceId(), LocalDate.parse(dt),
                        LocalTime.parse((String) cmbT.getSelectedItem()))) {
                    JOptionPane.showMessageDialog(this, "Randevu Oluşturuldu!");
                    loadAppointments();
                } else {
                    JOptionPane.showMessageDialog(this, "Uyarı: Seçilen saat dolu veya geçersiz!", "Hata", JOptionPane.WARNING_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Hata: Lütfen bilgileri kontrol edin.");
            }
        });

        panel.add(card);
        return panel;
    }

    private JPanel createAppointmentsListPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String[] kolonlar = {"ID", "Plaka", "Hizmet", "Tarih", "Saat", "Durum"};
        appointmentsTableModel = new DefaultTableModel(kolonlar, 0);
        JTable table = createStyledTable(appointmentsTableModel);

        // Durum Renklendirme
        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String st = (String) value;
                
                if (isSelected) {
                    c.setForeground(Color.WHITE);
                } else {
                    if ("Onaylandı".equals(st)) c.setForeground(new Color(39, 174, 96));
                    else if ("Beklemede".equals(st)) c.setForeground(new Color(243, 156, 18));
                    else if ("İptal Edildi".equals(st)) c.setForeground(new Color(231, 76, 60));
                    else c.setForeground(Color.BLACK);
                }
                return c;
            }
        });

        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton btnCancel = new JButton("X SEÇİLİ RANDEVUYU İPTAL ET");
        btnCancel.setFont(buttonFont);
        btnCancel.setBackground(new Color(220, 220, 220));
        btnCancel.setForeground(Color.BLACK);

        btnCancel.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                int id = (int) appointmentsTableModel.getValueAt(row, 0);
                if (appointmentService.updateStatus(id, "CANCELLED")) {
                    JOptionPane.showMessageDialog(this, "Randevu İptal Edildi");
                    loadAppointments();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Lütfen bir randevu seçin.");
            }
        });

        panel.add(btnCancel, BorderLayout.SOUTH);
        loadAppointments();
        return panel;
    }

    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(35);
        table.setFont(inputFont);
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 15));
        return table;
    }

    private JPanel createLabeledInput(String text, JComponent input) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Color.WHITE);
        JLabel lbl = new JLabel(text);
        lbl.setFont(labelFont);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        input.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(lbl);
        p.add(Box.createRigidArea(new Dimension(0, 5)));
        p.add(input);
        p.add(Box.createRigidArea(new Dimension(0, 15)));
        return p;
    }

    private void setupInput(JTextField f) {
        f.setFont(inputFont);
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
    }

    private void setupCombo(JComboBox<?> c) {
        c.setFont(inputFont);
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        c.setBackground(Color.WHITE);
    }

    private void refreshCarComboBox() {
        cmbCars.removeAllItems();
        carService.getCarsByUser(user.getUserId()).forEach(cmbCars::addItem);
    }

    private void loadAppointments() {
        if (appointmentsTableModel == null) return;
        appointmentsTableModel.setRowCount(0);
        appointmentService.getAppointmentsByUser(user.getUserId()).forEach(a -> {
            Car c = carService.getCarById(a.getCarId());
            Service s = serviceService.getServiceById(a.getServiceId());
            
            String durum = "Beklemede";
            if ("APPROVED".equals(a.getStatus())) durum = "Onaylandı";
            else if ("CANCELLED".equals(a.getStatus())) durum = "İptal Edildi";
            
            appointmentsTableModel.addRow(new Object[]{
                    a.getAppointmentId(),
                    (c != null ? c.getPlate() : "-"),
                    (s != null ? s.getName() : "-"),
                    a.getDate(),
                    a.getTime(),
                    durum
            });
        });
    }
}