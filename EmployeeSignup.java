package ui;

import db.DBConnection;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class EmployeeSignup {

    JFrame frame;
    JTextField idField, nameField;
    JPasswordField passField;

    public EmployeeSignup() {
        frame = new JFrame("Employee Signup");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new GridLayout(1, 2));

        // ================= LEFT PANEL (Dark Theme Form) =================
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBackground(new Color(32, 33, 36)); // Dark grey

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 40, 10, 40);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        JLabel title = new JLabel("Employee Signup");
        title.setFont(new Font("Segoe UI", Font.BOLD, 36));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Create a new administrative account");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(150, 150, 150));

        idField = new JTextField();
        styleTextField(idField, "Employee ID");

        nameField = new JTextField();
        styleTextField(nameField, "Full Name");

        passField = new JPasswordField();
        styleTextField(passField, "Password");

        JButton registerBtn = new JButton("Register");
        registerBtn.setBackground(new Color(160, 90, 240)); // Purple
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        registerBtn.setFocusPainted(false);
        registerBtn.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
        registerBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerBtn.addActionListener(e -> registerEmployee());

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        footerPanel.setBackground(new Color(32, 33, 36));
        JLabel hasAccountLbl = new JLabel("Already have an account?");
        hasAccountLbl.setForeground(new Color(150, 150, 150));
        hasAccountLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JButton loginBtn = new JButton("Login");
        loginBtn.setBackground(new Color(50, 50, 50));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginBtn.addActionListener(e -> {
            new EmployeeLogin();
            {
                javax.swing.Timer timerT = new javax.swing.Timer(250, ev -> frame.dispose());
                timerT.setRepeats(false);
                timerT.start();
            }
        });

        footerPanel.add(hasAccountLbl);
        footerPanel.add(loginBtn);

        // Layout construction
        gbc.gridy = 0; gbc.insets = new Insets(0, 40, 5, 40); leftPanel.add(title, gbc);
        gbc.gridy = 1; gbc.insets = new Insets(0, 40, 30, 40); leftPanel.add(subtitle, gbc);
        
        gbc.gridy = 2; gbc.insets = new Insets(10, 40, 15, 40); leftPanel.add(idField, gbc);
        gbc.gridy = 3; gbc.insets = new Insets(10, 40, 15, 40); leftPanel.add(nameField, gbc);
        gbc.gridy = 4; gbc.insets = new Insets(10, 40, 40, 40); leftPanel.add(passField, gbc);
        
        gbc.gridy = 5; gbc.insets = new Insets(10, 40, 50, 40); leftPanel.add(registerBtn, gbc);
        gbc.gridy = 6; gbc.insets = new Insets(20, 40, 0, 40); leftPanel.add(footerPanel, gbc);

        // ================= RIGHT PANEL (Illustration) =================
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(new Color(160, 90, 240)); 
        
        JLabel illustrationLabel = new JLabel() {
            Image img;
            {
                try {
                    img = ImageIO.read(new File("assets/employee_login.png"));
                } catch(Exception ex) {
                    System.out.println("Could not load employee illustration.");
                }
            }
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if(img != null) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    
                    float imgRatio = (float)img.getWidth(null) / img.getHeight(null);
                    float panelRatio = (float)getWidth() / getHeight();
                    
                    int w = getWidth();
                    int h = getHeight();
                    if(imgRatio > panelRatio) {
                        w = (int)(getHeight() * imgRatio);
                    } else {
                        h = (int)(getWidth() / imgRatio);
                    }
                    int x = (getWidth() - w)/2;
                    int y = (getHeight() - h)/2;
                    
                    g.drawImage(img, x, y, w, h, this);
                }
            }
        };



        rightPanel.add(illustrationLabel, BorderLayout.CENTER);

        // Back button absolute positioning
        JButton backBtn = new JButton("← Back");
        backBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backBtn.setForeground(Color.WHITE);
        backBtn.setContentAreaFilled(false);
        backBtn.setBorderPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> {
            new MainPage();
            {
                javax.swing.Timer timerT = new javax.swing.Timer(250, ev -> frame.dispose());
                timerT.setRepeats(false);
                timerT.start();
            }
        });
        
        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backPanel.setOpaque(false);
        backPanel.add(backBtn);
        
        JPanel leftContainer = new JPanel(new BorderLayout());
        leftContainer.setBackground(new Color(32, 33, 36));
        leftContainer.add(backPanel, BorderLayout.NORTH);
        
        JPanel centerWrap = new JPanel(new GridBagLayout());
        centerWrap.setBackground(new Color(32, 33, 36));
        centerWrap.add(leftPanel);
        leftContainer.add(centerWrap, BorderLayout.CENTER);

        mainPanel.add(leftContainer);
        mainPanel.add(rightPanel);

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private void styleTextField(JTextField field, String placeholder) {
        field.setBackground(new Color(32, 33, 36));
        field.setForeground(Color.WHITE);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, new Color(100, 100, 100)),
                BorderFactory.createEmptyBorder(5, 0, 5, 0)
        ));
        
        field.setText(placeholder);
        field.setForeground(new Color(150, 150, 150));

        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.WHITE);
                    if(field instanceof JPasswordField) {
                        ((JPasswordField)field).setEchoChar('\u2022');
                    }
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (field.getText().isEmpty()) {
                    field.setForeground(new Color(150, 150, 150));
                    field.setText(placeholder);
                    if(field instanceof JPasswordField) {
                        ((JPasswordField)field).setEchoChar((char) 0);
                    }
                }
            }
        });
        
        if(field instanceof JPasswordField) {
            ((JPasswordField)field).setEchoChar((char) 0);
        }
    }

    void registerEmployee() {
        try {
            Connection con = DBConnection.getConnection();

            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO employees(empid,emp_name,password) VALUES(?,?,?)"
            );

            String id = idField.getText();
            String name = nameField.getText();
            String pass = new String(passField.getPassword());
            
            if (id.isEmpty() || id.equals("Employee ID") || name.isEmpty() || name.equals("Full Name") || pass.isEmpty() || pass.equals("Password")) {
                JOptionPane.showMessageDialog(frame, "Please enter all fields!");
                return;
            }

            ps.setString(1, id);
            ps.setString(2, name);
            ps.setString(3, pass);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(frame,"Registered successfully!");
            new EmployeeLogin();
            {
                javax.swing.Timer timerT = new javax.swing.Timer(250, ev -> frame.dispose());
                timerT.setRepeats(false);
                timerT.start();
            }

        } catch(Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error in registration. It might be a duplicate ID.");
        }
    }
}