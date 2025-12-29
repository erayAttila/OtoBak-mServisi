package gui;


import javax.swing.*;
import java.awt.*;
import model.User;
import service.UserService;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginFrame extends JFrame {
    
	private static final long serialVersionUID = 1L;
	private JTextField emailField;
    private JPasswordField passwordField;
    private UserService userService = new UserService();

    public LoginFrame() {
        setTitle("Oto Servis - Giriş");
        setSize(450, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(240, 242, 245));

        // Ekranın tam ortasına yerleştirmek için GridBagLayout
        setLayout(new GridBagLayout());
        
        // --- BEYAZ KART PANELİ ---
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(30, 45, 30, 45)
        ));
        card.setPreferredSize(new Dimension(350, 400));

        // 1. BAŞLIK
        JLabel lblWelcome = new JLabel("Hoş Geldiniz");
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblWelcome.setForeground(new Color(41, 128, 185));
        lblWelcome.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblSub = new JLabel("Lütfen bilgilerinizi giriniz");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(Color.GRAY);
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(lblWelcome);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(lblSub);
        card.add(Box.createRigidArea(new Dimension(0, 30)));

        // 2. ORTALANMIŞ FORM ALANLARI
        addCenteredField(card, "E-mail:", emailField = new JTextField());
        addCenteredField(card, "Şifre:", passwordField = new JPasswordField());

        card.add(Box.createRigidArea(new Dimension(0, 10)));

        // 3. GİRİŞ BUTONU 
        JButton loginButton = new JButton("GİRİŞ YAP");
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        loginButton.setBackground(new Color(220, 220, 220)); 
        loginButton.setForeground(Color.BLACK); 
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        card.add(loginButton);

        // 4. KAYIT LİNKİ
        JLabel lblReg = new JLabel("<html>Hesabınız yok mu? <font color='#2980b9'><b>Hemen Kaydolun</b></font></html>");
        lblReg.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblReg.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblReg.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { dispose(); new RegisterFrame().setVisible(true); }
        });
        
        card.add(Box.createRigidArea(new Dimension(0, 25)));
        card.add(lblReg);

        add(card); 

        loginButton.addActionListener(e -> login());
    }

    
    private void addCenteredField(JPanel panel, String labelText, JTextField field) {
        JLabel label = new JLabel(labelText);
        label.setAlignmentX(Component.CENTER_ALIGNMENT); 
        label.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
        label.setForeground(new Color(52, 73, 94));
        
        field.setMaximumSize(new Dimension(250, 35)); 
        field.setAlignmentX(Component.CENTER_ALIGNMENT); 
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(field);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
    }

    private void login() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        
        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurun.");
            return;
        }

        User user = userService.login(email, password);
        if (user != null) {
            dispose();
            if ("ADMIN".equalsIgnoreCase(user.getRole())) new AdminFrame().setVisible(true);
            else new CustomerFrame(user).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "E-mail veya şifre hatalı!", "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}