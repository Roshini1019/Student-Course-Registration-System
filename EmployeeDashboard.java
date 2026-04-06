package ui;

import db.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class EmployeeDashboard {

    JFrame frame;
    JTable studentTable, courseTable;

    public EmployeeDashboard() {

        frame = new JFrame("Employee Dashboard");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel bg = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(58,123,213),
                        getWidth(), getHeight(), new Color(0,210,255)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        bg.setLayout(new BorderLayout());

        JLabel title = new JLabel("Employee Dashboard", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(Color.WHITE);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 16));

        tabs.add("Students", createStudentsPanel());
        tabs.add("Courses", createCoursesPanel());

        JButton logout = new JButton("Logout");
        logout.setBackground(new Color(231,76,60));
        logout.setForeground(Color.WHITE);

        logout.addActionListener(e -> {
            new MainPage();
            {
                javax.swing.Timer timerT = new javax.swing.Timer(250, ev -> frame.dispose());
                timerT.setRepeats(false);
                timerT.start();
            }
        });

        JPanel bottom = new JPanel();
        bottom.add(logout);

        bg.add(title, BorderLayout.NORTH);
        bg.add(tabs, BorderLayout.CENTER);
        bg.add(bottom, BorderLayout.SOUTH);

        frame.add(bg);
        frame.setVisible(true);
    }

    // ================= STUDENTS PANEL =================
    JPanel createStudentsPanel() {

        DefaultTableModel model = new DefaultTableModel();

        model.addColumn("Student Name");
        model.addColumn("Course Name");
        model.addColumn("Date & Time");

        studentTable = new JTable(model);
        styleTable(studentTable);

        loadStudentData(model);

        return new JPanel(new BorderLayout()) {{
            add(new JScrollPane(studentTable), BorderLayout.CENTER);
        }};
    }

    // ================= COURSES PANEL =================
    JPanel createCoursesPanel() {

        DefaultTableModel model = new DefaultTableModel();

        model.addColumn("Course ID");
        model.addColumn("Course Name");
        model.addColumn("Department");
        model.addColumn("Credits");
        model.addColumn("Fees");

        courseTable = new JTable(model);
        styleTable(courseTable);

        loadCourseData(model);

        JButton add = new JButton("Add");
        JButton delete = new JButton("Delete");
        JButton refresh = new JButton("Refresh");

        styleButton(add, new Color(46,204,113));
        styleButton(delete, new Color(231,76,60));
        styleButton(refresh, new Color(52,152,219));

        add.addActionListener(e -> addCourse());
        delete.addActionListener(e -> deleteCourse());
        refresh.addActionListener(e -> {
            model.setRowCount(0);
            loadCourseData(model);
        });

        JPanel btnPanel = new JPanel();
        btnPanel.add(add);
        btnPanel.add(delete);
        btnPanel.add(refresh);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(courseTable), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    // ================= LOAD STUDENT DATA =================
    void loadStudentData(DefaultTableModel model) {

        try {
            Connection con = DBConnection.getConnection();

            String query =
                    "SELECT students.name, courses.course_name, registrations.registration_date " +
                    "FROM registrations " +
                    "JOIN students ON registrations.student_id = students.id " +
                    "JOIN courses ON registrations.course_id = courses.course_id";

            ResultSet rs = con.createStatement().executeQuery(query);

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString(1),
                        rs.getString(2),
                        rs.getTimestamp(3)
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= LOAD COURSE DATA =================
    void loadCourseData(DefaultTableModel model) {

        try {
            Connection con = DBConnection.getConnection();

            ResultSet rs = con.createStatement().executeQuery("SELECT * FROM courses");

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("course_id"),   // 🔥 VARCHAR
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

    // ================= ADD COURSE =================
    void addCourse() {

        try {

            String id = JOptionPane.showInputDialog("Course ID ");
            String name = JOptionPane.showInputDialog("Course Name");
            String dept = JOptionPane.showInputDialog("Department");
            int credits = Integer.parseInt(JOptionPane.showInputDialog("Credits"));
            int fees = Integer.parseInt(JOptionPane.showInputDialog("Fees"));

            Connection con = DBConnection.getConnection();

            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO courses(course_id, course_name, department, credits, fees) VALUES(?,?,?,?,?)"
            );

            ps.setString(1, id);   // 🔥 VARCHAR
            ps.setString(2, name);
            ps.setString(3, dept);
            ps.setInt(4, credits);
            ps.setInt(5, fees);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(frame, "Course Added!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= DELETE COURSE =================
    void deleteCourse() {

        try {

            String id = JOptionPane.showInputDialog("Enter Course ID to Delete");

            Connection con = DBConnection.getConnection();

            PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM courses WHERE course_id=?"
            );

            ps.setString(1, id);  // 🔥 VARCHAR

            int rows = ps.executeUpdate();

            if (rows > 0)
                JOptionPane.showMessageDialog(frame, "Deleted");
            else
                JOptionPane.showMessageDialog(frame, "Not Found");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= STYLE =================
    void styleTable(JTable table) {
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        table.getTableHeader().setBackground(new Color(52,73,94));
        table.getTableHeader().setForeground(Color.WHITE);
    }

    void styleButton(JButton btn, Color color) {
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
    }
}