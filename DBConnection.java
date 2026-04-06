package db;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    public static Connection getConnection() {

        Connection con = null;

        try {

            // 🔥 VERY IMPORTANT
            Class.forName("com.mysql.cj.jdbc.Driver");

            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/course_registration",
                    "root",
                    "Roshini@123");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return con;
    }
}