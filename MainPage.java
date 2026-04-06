package ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;

public class MainPage {

    JFrame frame;

    public MainPage() {

        frame = new JFrame("Course Registration System");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel bg = new JPanel() {
            Image img;
            {
                try {
                    img = ImageIO.read(new File("assets/big_university_bg.png"));
                } catch(Exception ex) {}
            }
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                
                if (img != null) {
                    // Fill image over the whole background
                    g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
                    
                    // Add a translucent dark overlay for better text readability and cinematic look
                    g2.setColor(new Color(0, 0, 0, 100)); // 100 alpha overlay
                    g2.fillRect(0, 0, getWidth(), getHeight());
                } else {
                    GradientPaint gp = new GradientPaint(
                            0, 0, new Color(15, 32, 39),
                            getWidth(), getHeight(), new Color(32, 58, 67)
                    );
                    g2.setPaint(gp);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };

        bg.setLayout(new GridBagLayout());

        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                // Translucent dark background matching the new login screens
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Glassmorphism-style semi-transparent background
                g2.setColor(new Color(25, 25, 25, 200)); 
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.dispose();
            }
        };
        card.setOpaque(false); // Make sure the panel itself isn't drawing a solid square
        card.setPreferredSize(new Dimension(500, 450));
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Welcome to Portal", JLabel.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 36));

        JButton studentBtn = createButton("Student Login", new Color(160, 90, 240)); // Match purple login theme
        JButton employeeBtn = createButton("Employee Login", new Color(52, 152, 219));
        JButton exitBtn = createButton("Exit System", new Color(231, 76, 60));

        studentBtn.addActionListener(e -> {
            new StudentLogin();
            {
                javax.swing.Timer timerT = new javax.swing.Timer(250, ev -> frame.dispose());
                timerT.setRepeats(false);
                timerT.start();
            }
        });

        employeeBtn.addActionListener(e -> {
            new EmployeeLogin();
            {
                javax.swing.Timer timerT = new javax.swing.Timer(250, ev -> frame.dispose());
                timerT.setRepeats(false);
                timerT.start();
            }
        });

        exitBtn.addActionListener(e -> System.exit(0));

        gbc.gridy = 0;
        card.add(title, gbc);
        gbc.gridy = 1;
        gbc.insets = new Insets(20, 15, 10, 15);
        card.add(studentBtn, gbc);
        gbc.gridy = 2;
        gbc.insets = new Insets(10, 15, 10, 15);
        card.add(employeeBtn, gbc);
        gbc.gridy = 3;
        gbc.insets = new Insets(10, 15, 10, 15);
        card.add(exitBtn, gbc);

        bg.add(card);
        frame.add(bg);

        frame.setVisible(true);
    }

    JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setForeground(Color.WHITE);
        btn.setBackground(color);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btn.setPreferredSize(new Dimension(280, 55));
        
        // Remove default swing button styling
        btn.setBorder(BorderFactory.createEmptyBorder());
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}