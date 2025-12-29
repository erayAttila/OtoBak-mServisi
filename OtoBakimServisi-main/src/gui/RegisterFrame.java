package gui;

import javax.swing.*;
import java.awt.*;
import model.User;
import service.UserService;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RegisterFrame extends JFrame {
    private static final long serialVersionUID = 1L;
	private JTextField txtName, txtEmail, txtPhone;
    private JPasswordField txtPassword;
    private UserService userService = new UserService();

    public RegisterFrame() {
        setTitle("Oto Servis - Yeni Kayıt");
        setSize(450, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(240, 242, 245));

        setLayout(new GridBagLayout());

      
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(30, 45, 30, 45)
        ));
        card.setPreferredSize(new Dimension(380, 520));

        
        JLabel lblTitle = new JLabel("Yeni Kayıt Oluştur");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(41, 128, 185)); // Mavi renk
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblTitle);
        card.add(Box.createRigidArea(new Dimension(0, 25)));

        
        addCenteredField(card, "Ad Soyad:", txtName = new JTextField());
        addCenteredField(card, "E-mail:", txtEmail = new JTextField());
        addCenteredField(card, "Telefon:", txtPhone = new JTextField());
        addCenteredField(card, "Şifre:", txtPassword = new JPasswordField());

        card.add(Box.createRigidArea(new Dimension(0, 10)));

        
        JButton btnReg = new JButton("KAYDOL VE BAŞLA");
        btnReg.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnReg.setBackground(new Color(220, 220, 220)); 
        btnReg.setForeground(Color.BLACK); 
        btnReg.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnReg.setFocusPainted(false);
        btnReg.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnReg.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(btnReg);

       
        JLabel lblBack = new JLabel("<html>Zaten hesabınız var mı? <font color='#2980b9'><b>Giriş Yapın</b></font></html>");
        lblBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblBack.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblBack.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { dispose(); new LoginFrame().setVisible(true); }
        });
        card.add(Box.createRigidArea(new Dimension(0, 20)));
        card.add(lblBack);

        add(card);

        btnReg.addActionListener(e -> register());
    }

    
    private void addCenteredField(JPanel panel, String labelText, JTextField field) {
        JLabel label = new JLabel(labelText);
        label.setAlignmentX(Component.CENTER_ALIGNMENT); 
        label.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 13));
        label.setForeground(new Color(52, 73, 94));
        
        field.setMaximumSize(new Dimension(280, 35)); 
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

    private void register() {
        String name = txtName.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();
        String pass = new String(txtPassword.getPassword());

        if(name.isEmpty() || email.isEmpty() || phone.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tüm alanları doldurun!");
            return;
        }

        User newUser = new User(0, name, email, pass, "CUSTOMER", phone);
        if(userService.register(newUser)) {
            JOptionPane.showMessageDialog(this, "Kayıt başarılı!");
            dispose();
            new LoginFrame().setVisible(true);
        }
    }
}