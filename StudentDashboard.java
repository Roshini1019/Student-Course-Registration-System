package ui;

import db.DBConnection;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

public class StudentDashboard {

    JFrame frame;
    String studentId;

    JTable availableTable, registeredTable;

    JProgressBar courseProgressBar;
    JProgressBar creditProgressBar;
    JLabel feesAmountLabel;
    int enrolledCourses = 0;
    int enrolledCredits = 0;
    final int MAX_COURSES = 5;
    final int MAX_CREDITS = 18;

    public StudentDashboard(String studentId) {

        this.studentId = studentId;

        // Reset Look and Feel to default Swing for simplicity
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
        }

        frame = new JFrame("Student Dashboard");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 🔥 Gradient BG (Original Style)
        JPanel bg = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(36, 37, 38),
                        getWidth(), getHeight(), new Color(0, 210, 255));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        bg.setLayout(new BorderLayout());

        JLabel title = new JLabel("Student Dashboard", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(Color.WHITE);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 16));

        tabs.add("Available Courses", createAvailablePanel());
        tabs.add("Registered Courses", createRegisteredPanel());

        JButton logout = new JButton("Logout");
        logout.setBackground(new Color(231, 76, 60));
        logout.setForeground(Color.WHITE);

        logout.addActionListener(e -> {
            new MainPage();
            {
                javax.swing.Timer timerT = new javax.swing.Timer(250, ev -> frame.dispose());
                timerT.setRepeats(false);
                timerT.start();
            }
        });

        courseProgressBar = new JProgressBar(0, MAX_COURSES);
        courseProgressBar.setStringPainted(true);
        courseProgressBar.setForeground(new Color(46, 204, 113));

        creditProgressBar = new JProgressBar(0, MAX_CREDITS);
        creditProgressBar.setStringPainted(true);
        creditProgressBar.setForeground(new Color(230, 126, 34));

        JPanel progressPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        progressPanel.setOpaque(false);

        JPanel courseP = new JPanel(new BorderLayout());
        courseP.setOpaque(false);
        JLabel cLabel = new JLabel("Courses Enrolled This Semester: ");
        cLabel.setForeground(Color.WHITE);
        cLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        courseP.add(cLabel, BorderLayout.WEST);
        courseP.add(courseProgressBar, BorderLayout.CENTER);

        JPanel creditP = new JPanel(new BorderLayout());
        creditP.setOpaque(false);
        JLabel crLabel = new JLabel("Credits Enrolled This Semester: ");
        crLabel.setForeground(Color.WHITE);
        crLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        creditP.add(crLabel, BorderLayout.WEST);
        creditP.add(creditProgressBar, BorderLayout.CENTER);

        JPanel feesP = new JPanel(new BorderLayout());
        feesP.setOpaque(false);
        JLabel fLabel = new JLabel("Total Registered Fees: ");
        fLabel.setForeground(Color.WHITE);
        fLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        feesAmountLabel = new JLabel("   ₹0");
        feesAmountLabel.setForeground(new Color(241, 196, 15));
        feesAmountLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        feesP.add(fLabel, BorderLayout.WEST);
        feesP.add(feesAmountLabel, BorderLayout.CENTER);

        progressPanel.add(courseP);
        progressPanel.add(creditP);
        progressPanel.add(feesP);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        bottom.add(progressPanel, BorderLayout.CENTER);

        JPanel logoutPanel = new JPanel();
        logoutPanel.setOpaque(false);
        logoutPanel.add(logout);
        bottom.add(logoutPanel, BorderLayout.EAST);

        bg.add(title, BorderLayout.NORTH);
        bg.add(tabs, BorderLayout.CENTER);
        bg.add(bottom, BorderLayout.SOUTH);

        updateProgressBars();

        frame.add(bg);
        frame.setVisible(true);
    }

    // ================= AVAILABLE COURSES =================
    JPanel createAvailablePanel() {

        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0)
                    return Boolean.class;
                return super.getColumnClass(columnIndex);
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0; // Only checkbox is editable
            }
        };

        model.addColumn("Select");
        model.addColumn("Course ID");
        model.addColumn("Course Name");
        model.addColumn("Department");
        model.addColumn("Credits");
        model.addColumn("Fees");

        availableTable = new JTable(model);
        styleTable(availableTable);
        availableTable.getColumnModel().getColumn(0).setMaxWidth(60);

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        availableTable.setRowSorter(sorter);

        loadAvailableCourses(model);

        // UI Components
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel searchLbl = new JLabel("Search Course:");
        searchLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JTextField searchField = new JTextField(25);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                filter();
            }

            public void removeUpdate(DocumentEvent e) {
                filter();
            }

            public void changedUpdate(DocumentEvent e) {
                filter();
            }

            private void filter() {
                String text = searchField.getText();
                if (text.trim().length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });

        topPanel.add(searchLbl);
        topPanel.add(searchField);

        JButton register = new JButton("Register Selected Courses");
        JButton refresh = new JButton("Refresh");

        register.addActionListener(e -> registerCourses(model));
        refresh.addActionListener(e -> {
            model.setRowCount(0);
            loadAvailableCourses(model);
        });

        JPanel btn = new JPanel();
        btn.add(register);
        btn.add(refresh);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(availableTable), BorderLayout.CENTER);
        panel.add(btn, BorderLayout.SOUTH);

        return panel;
    }

    // ================= REGISTERED COURSES =================
    JPanel createRegisteredPanel() {

        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0)
                    return Boolean.class;
                return super.getColumnClass(columnIndex);
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0;
            }
        };

        model.addColumn("Select");
        model.addColumn("Course ID");
        model.addColumn("Course Name");
        model.addColumn("Date & Time");

        registeredTable = new JTable(model);
        styleTable(registeredTable);
        registeredTable.getColumnModel().getColumn(0).setMaxWidth(60);

        loadRegisteredCourses(model);

        JButton drop = new JButton("Drop Selected Courses");
        JButton refresh = new JButton("Refresh");
        JButton receiptBtn = new JButton("Generate Receipt");

        drop.addActionListener(e -> dropCourses(model));
        refresh.addActionListener(e -> {
            model.setRowCount(0);
            loadRegisteredCourses(model);
        });
        receiptBtn.addActionListener(e -> ReceiptPrinter.showReceipt(studentId, frame));

        JPanel btn = new JPanel();
        btn.add(drop);
        btn.add(refresh);
        btn.add(receiptBtn);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(registeredTable), BorderLayout.CENTER);
        panel.add(btn, BorderLayout.SOUTH);

        return panel;
    }

    // ================= LOAD AVAILABLE =================
    void loadAvailableCourses(DefaultTableModel model) {

        try {
            Connection con = DBConnection.getConnection();
            ResultSet rs = con.createStatement().executeQuery("SELECT * FROM courses");

            while (rs.next()) {
                model.addRow(new Object[] {
                        false,
                        rs.getString("course_id"),
                        rs.getString("course_name"),
                        rs.getString("department"),
                        rs.getInt("credits"),
                        "₹" + rs.getInt("fees")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= LOAD REGISTERED =================
    void loadRegisteredCourses(DefaultTableModel model) {

        try {
            Connection con = DBConnection.getConnection();
            String q = "SELECT courses.course_id, courses.course_name, registrations.registration_date " +
                    "FROM registrations " +
                    "JOIN courses ON registrations.course_id = courses.course_id " +
                    "WHERE student_id=?";
            PreparedStatement ps = con.prepareStatement(q);
            ps.setString(1, studentId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[] {
                        false,
                        rs.getString(1),
                        rs.getString(2),
                        rs.getTimestamp(3)
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= REGISTER =================
    void registerCourses(DefaultTableModel model) {

        // Stop editing if a cell is currently focused, to ensure checkbox state is
        // committed.
        if (availableTable.isEditing()) {
            availableTable.getCellEditor().stopCellEditing();
        }

        ArrayList<Integer> selectedRows = new ArrayList<>();
        int selectedCredits = 0;

        for (int i = 0; i < model.getRowCount(); i++) {
            Boolean isSelected = (Boolean) model.getValueAt(i, 0);
            if (isSelected != null && isSelected) {
                selectedRows.add(i);
                selectedCredits += Integer.parseInt(model.getValueAt(i, 4).toString());
            }
        }

        if (selectedRows.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Select at least one course first!");
            return;
        }

        if (enrolledCourses + selectedRows.size() > MAX_COURSES) {
            JOptionPane.showMessageDialog(frame,
                    "Registration Failed: Maximum " + MAX_COURSES
                            + " courses allowed per semester. You are trying to register for "
                            + (enrolledCourses + selectedRows.size()) + ".");
            return;
        }

        if (enrolledCredits + selectedCredits > MAX_CREDITS) {
            JOptionPane.showMessageDialog(frame,
                    "Registration Failed: Maximum " + MAX_CREDITS
                            + " credits allowed per semester. You are trying to reach "
                            + (enrolledCredits + selectedCredits) + " credits.");
            return;
        }

        try {
            Connection con = DBConnection.getConnection();

            int successCount = 0;
            String duplicateCourses = "";

            PreparedStatement checkStmt = con
                    .prepareStatement("SELECT 1 FROM registrations WHERE student_id=? AND course_id=?");
            checkStmt.setString(1, studentId);

            PreparedStatement insertStmt = con
                    .prepareStatement("INSERT INTO registrations(student_id, course_id) VALUES(?,?)");
            insertStmt.setString(1, studentId);

            for (int i : selectedRows) {
                String courseId = model.getValueAt(i, 1).toString();

                checkStmt.setString(2, courseId);
                ResultSet checkRs = checkStmt.executeQuery();

                if (checkRs.next()) {
                    duplicateCourses += courseId + " ";
                } else {
                    insertStmt.setString(2, courseId);
                    insertStmt.executeUpdate();
                    successCount++;
                    // Uncheck it
                    model.setValueAt(false, i, 0);
                }
            }

            String msg = "";
            if (successCount > 0) {
                msg += "Successfully registered for " + successCount + " courses!\n";
            }
            if (!duplicateCourses.isEmpty()) {
                msg += "Skipped (Already Registered): " + duplicateCourses;
            }

            JOptionPane.showMessageDialog(frame, msg);
            updateProgressBars();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= DROP =================
    void dropCourses(DefaultTableModel model) {

        if (registeredTable.isEditing()) {
            registeredTable.getCellEditor().stopCellEditing();
        }

        ArrayList<Integer> selectedRows = new ArrayList<>();

        for (int i = 0; i < model.getRowCount(); i++) {
            Boolean isSelected = (Boolean) model.getValueAt(i, 0);
            if (isSelected != null && isSelected) {
                selectedRows.add(i);
            }
        }

        if (selectedRows.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Select at least one course to drop!");
            return;
        }

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("DELETE FROM registrations WHERE student_id=? AND course_id=?");
            ps.setString(1, studentId);

            for (int i : selectedRows) {
                String courseId = model.getValueAt(i, 1).toString();
                ps.setString(2, courseId);
                ps.executeUpdate();
            }

            JOptionPane.showMessageDialog(frame, "Dropped " + selectedRows.size() + " courses!");

            // Refresh table
            model.setRowCount(0);
            loadRegisteredCourses(model);
            updateProgressBars();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void styleTable(JTable table) {
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
    }

    void updateProgressBars() {
        try {
            Connection con = DBConnection.getConnection();
            String q = "SELECT COUNT(registrations.course_id), SUM(courses.credits), SUM(courses.fees) " +
                    "FROM registrations " +
                    "JOIN courses ON registrations.course_id = courses.course_id " +
                    "WHERE student_id=?";
            PreparedStatement ps = con.prepareStatement(q);
            ps.setString(1, studentId);
            ResultSet rs = ps.executeQuery();

            int totalFees = 0;

            if (rs.next()) {
                enrolledCourses = rs.getInt(1);
                enrolledCredits = rs.getInt(2);
                totalFees = rs.getInt(3);
                if (rs.wasNull()) {
                    enrolledCredits = 0;
                    totalFees = 0;
                }
            } else {
                enrolledCourses = 0;
                enrolledCredits = 0;
            }

            courseProgressBar.setValue(enrolledCourses);
            courseProgressBar.setString(enrolledCourses + " / " + MAX_COURSES + " max");

            creditProgressBar.setValue(enrolledCredits);
            creditProgressBar.setString(enrolledCredits + " / " + MAX_CREDITS + " max");

            feesAmountLabel.setText("   ₹" + totalFees);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}