package ui;

import db.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.print.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReceiptPrinter {

    public static void showReceipt(String studentId, JFrame parent) {
        String studentName = "Unknown";
        int totalFees = 0;
        DefaultTableModel model = new DefaultTableModel(new String[]{"Sr. No", "Particulars", "Amount"}, 0);

        try {
            Connection con = DBConnection.getConnection();

            // Get student name
            PreparedStatement ps1 = con.prepareStatement("SELECT name FROM students WHERE id=?");
            ps1.setString(1, studentId);
            ResultSet rs1 = ps1.executeQuery();
            if (rs1.next()) {
                studentName = rs1.getString(1);
            }

            // Get courses
            PreparedStatement ps2 = con.prepareStatement(
                    "SELECT c.course_name, c.fees FROM registrations r JOIN courses c ON r.course_id = c.course_id WHERE r.student_id=?");
            ps2.setString(1, studentId);
            ResultSet rs2 = ps2.executeQuery();

            int sr = 1;
            while (rs2.next()) {
                String cname = rs2.getString(1);
                int fees = rs2.getInt(2);
                model.addRow(new Object[]{sr++, cname, fees});
                totalFees += fees;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JDialog dialog = new JDialog(parent, "Fee Receipt", true);
        dialog.setSize(650, 750);
        dialog.setLocationRelativeTo(parent);
        dialog.setLayout(new BorderLayout());

        ReceiptPanel receiptPanel = new ReceiptPanel(studentId, studentName, model, totalFees);

        JButton printBtn = new JButton("Print Receipt");
        printBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        printBtn.setBackground(new Color(41, 128, 185));
        printBtn.setForeground(Color.WHITE);
        printBtn.setFocusPainted(false);
        printBtn.addActionListener(e -> {
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPrintable(receiptPanel);
            if (job.printDialog()) {
                try {
                    job.print();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        JPanel btnPanel = new JPanel();
        btnPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        btnPanel.add(printBtn);

        dialog.add(new JScrollPane(receiptPanel), BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
}

class ReceiptPanel extends JPanel implements Printable {
    String studentId, studentName;
    DefaultTableModel coursesModel;
    int totalFees;

    public ReceiptPanel(String id, String name, DefaultTableModel model, int total) {
        this.studentId = id;
        this.studentName = name;
        this.coursesModel = model;
        this.totalFees = total;

        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(600, 700));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw Gradient Background (like Student Dashboard)
        GradientPaint gp = new GradientPaint(
                0, 0, new Color(36, 37, 38),
                getWidth(), getHeight(), new Color(0, 210, 255));
        g2.setPaint(gp);
        g2.fillRect(0, 0, getWidth(), getHeight());

        int startX = 30;
        int startY = 80;
        int width = 540;
        int height = 540;

        // Title text above box
        g2.setColor(Color.WHITE); 
        g2.setFont(new Font("Segoe UI", Font.BOLD, 28));
        g2.drawString("Receipt for Fees", 200, 50);

        // Fill background color
        g2.setColor(Color.WHITE); // professional solid white background for receipt paper
        g2.fillRect(startX, startY, width, height);
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(1));
        g2.drawRect(startX, startY, width, height);

        // Inside box - Header
        g2.setFont(new Font("Serif", Font.BOLD, 14));
        String receiptTxt = "Receipt";
        g2.drawString(receiptTxt, 270, 100);
        g2.drawLine(270, 102, 315, 102); // underline

        g2.setFont(new Font("Serif", Font.BOLD, 18));
        g2.drawString("College of Engineering & Technology", 150, 125);
        g2.setFont(new Font("Serif", Font.PLAIN, 12));
        g2.drawString("Address: 123 University Road, Tech City", 80, 145);
        g2.drawString("Phone: +1 800 555 1234", 400, 145);

        g2.drawString("Receipt No. " + (int) (Math.random() * 9000 + 1000), 40, 175);
        g2.drawLine(startX, 185, startX + width, 185); // horizontal line

        g2.drawString("Name of Student: " + studentName, 40, 215);
        g2.drawString("Student ID: " + studentId, 400, 215);
        String date = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        g2.drawString("Date of payment: " + date, 390, 245);

        g2.drawLine(startX, 260, startX + width, 260); // horiz line
        
        // Table headers
        g2.setFont(new Font("Serif", Font.BOLD, 13));
        g2.drawString("Sr. No.", 40, 275);
        g2.drawString("Particulars", 250, 275);
        g2.drawString("Amount", 480, 275);
        g2.drawLine(startX, 285, startX + width, 285); // horiz line

        // Draw vertical lines for table
        g2.drawLine(90, 260, 90, 480);
        g2.drawLine(460, 260, 460, 480);

        // Data rows
        g2.setFont(new Font("Serif", Font.PLAIN, 13));
        int y = 305;
        for (int i = 0; i < coursesModel.getRowCount(); i++) {
            g2.drawString(coursesModel.getValueAt(i, 0).toString(), 55, y); // Sr.No
            g2.drawString(coursesModel.getValueAt(i, 1).toString(), 110, y); // Particulars
            g2.drawString(coursesModel.getValueAt(i, 2).toString(), 485, y); // Amount
            y += 25;
        }

        // Total block
        g2.drawLine(460, 480, startX + width, 480);
        g2.setFont(new Font("Serif", Font.BOLD, 14));
        g2.drawString("Total", 400, 498);
        g2.drawString("₹ " + totalFees + "/-", 485, 498);
        g2.drawLine(startX, 510, startX + width, 510);

        // Footer info
        g2.setFont(new Font("Serif", Font.PLAIN, 12));
        g2.drawString("Paid By: Cash / Online", 40, 530);
        g2.drawString("Balance if any: 0", 400, 530);

        // Signatures labels
        g2.drawString("Signature of Centre Head", 40, 595);
        g2.drawString("Signature of Student", 420, 595);

        // Disclaimer
        g2.drawLine(startX, 605, startX + width, 605);
        g2.setFont(new Font("Serif", Font.PLAIN, 11));
        g2.drawString("All above mentioned Amount once paid are non refundable in any case whatsoever.", 100, 617);

        // ================= STAMP AND SIGNATURES =================
        // 1. VP / Head Signature
        g2.setColor(Color.BLUE);
        g2.setFont(new Font("Monotype Corsiva", Font.ITALIC | Font.BOLD, 26)); 
        if(!g2.getFont().getFamily().equals("Monotype Corsiva")) {
            g2.setFont(new Font("Serif", Font.ITALIC, 24));
        }
        g2.drawString("Dr. A. Sharma", 60, 570);

        // 2. STAMP (Rotated)
        Graphics2D gStamp = (Graphics2D) g2.create();
        gStamp.translate(260, 550);
        gStamp.rotate(Math.toRadians(-15)); // rotate -15 deg
        gStamp.setColor(new Color(0, 0, 153, 200)); // transparent blue
        gStamp.setStroke(new BasicStroke(2));
        gStamp.drawRect(-80, -30, 160, 60);
        gStamp.drawRect(-76, -26, 152, 52); // double border
        gStamp.setFont(new Font("SansSerif", Font.BOLD, 12));
        gStamp.drawString("COLLEGE OF", -42, -5);
        gStamp.drawString("ENGG. & TECH.", -45, 10);
        gStamp.setFont(new Font("SansSerif", Font.PLAIN, 10));
        gStamp.drawString("OFFICIAL SEAL", -38, 22);
        gStamp.dispose();
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        if (pageIndex > 0) return NO_SUCH_PAGE;

        Graphics2D g2 = (Graphics2D) graphics;
        g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

        // Scale to fit page width
        double scale = pageFormat.getImageableWidth() / this.getWidth();
        if (scale < 1.0) {
            g2.scale(scale, scale);
        }

        this.paint(g2);

        return PAGE_EXISTS;
    }
}
