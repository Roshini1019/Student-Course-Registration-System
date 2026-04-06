package ui;

import db.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class CourseSelection {

    JFrame frame;
    JTable table;
    String studentId;

    public CourseSelection(String studentId){

        this.studentId = studentId;

        frame = new JFrame("Course Selection");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        // 🔹 Top Panel
        JPanel topPanel = new JPanel(new BorderLayout());

        JLabel title = new JLabel("Course Selection");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));

        JButton logout = new JButton("Logout");

        logout.addActionListener(e -> {
            new MainPage();
            {
                javax.swing.Timer timerT = new javax.swing.Timer(250, ev -> frame.dispose());
                timerT.setRepeats(false);
                timerT.start();
            }
        });

        topPanel.add(title, BorderLayout.WEST);
        topPanel.add(logout, BorderLayout.EAST);

        // 🔹 Table Model
        DefaultTableModel model = new DefaultTableModel(){

            public Class<?> getColumnClass(int column){
                if(column==0) return Boolean.class;
                return String.class;
            }

            public boolean isCellEditable(int row,int column){
                return column==0;
            }
        };

        // ✅ UPDATED COLUMNS (based on your DB)
        model.addColumn("Select");
        model.addColumn("Course ID");
        model.addColumn("Course Name");
        model.addColumn("Department");
        model.addColumn("Credits");
        model.addColumn("Fees");

        table = new JTable(model);

        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        loadCourses(model);

        JScrollPane scroll = new JScrollPane(table);

        // 🔹 Confirm Button
        JButton confirm = new JButton("Confirm Registration");

        confirm.setBackground(new Color(231,76,60));
        confirm.setForeground(Color.WHITE);

        confirm.addActionListener(e -> registerCourses());

        // 🔹 Layout
        frame.setLayout(new BorderLayout());

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(scroll, BorderLayout.CENTER);
        frame.add(confirm, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    // 🔹 Load Courses
    void loadCourses(DefaultTableModel model){

        try{
            Connection con = DBConnection.getConnection();

            ResultSet rs = con.createStatement().executeQuery("SELECT * FROM courses");

            while(rs.next()){

                model.addRow(new Object[]{
                        false,
                        rs.getString("course_id"),   // 🔥 FIXED
                        rs.getString("course_name"),
                        rs.getString("department"),
                        rs.getInt("credits"),
                        rs.getInt("fees")
                });
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    // 🔹 Register Courses
    void registerCourses(){

        try{
            Connection con = DBConnection.getConnection();

            for(int i=0;i<table.getRowCount();i++){

                Boolean checked = (Boolean)table.getValueAt(i,0);

                if(checked!=null && checked){

                    String courseId = table.getValueAt(i,1).toString(); // 🔥 FIXED

                    // 🔴 Duplicate Check
                    PreparedStatement check = con.prepareStatement(
                            "SELECT * FROM registrations WHERE student_id=? AND course_id=?"
                    );

                    check.setString(1,studentId);
                    check.setString(2,courseId); // 🔥 FIXED

                    ResultSet rs = check.executeQuery();

                    if(rs.next()){
                        JOptionPane.showMessageDialog(frame,
                                "Course " + courseId + " already registered!");
                    }
                    else{

                        PreparedStatement ps = con.prepareStatement(
                                "INSERT INTO registrations(student_id,course_id) VALUES(?,?)"
                        );

                        ps.setString(1,studentId);
                        ps.setString(2,courseId); // 🔥 FIXED

                        ps.executeUpdate();
                    }
                }
            }

            JOptionPane.showMessageDialog(frame,"Registration Completed!");

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}