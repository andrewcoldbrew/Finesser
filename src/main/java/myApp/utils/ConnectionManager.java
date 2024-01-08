package myApp.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {
    private static Connection con;

    private static final String user = "mvv14svtqy8jssqc9c57";
    private static final String password = "pscale_pw_gn08b8E1x0Jicq68X8QHHdJ2cDema31j1zi798SDO3h";

    public static void createConnection() {
        try {
            con = DriverManager.getConnection(
                    "jdbc:mysql://aws.connect.psdb.cloud/bitsdatabase?sslMode=VERIFY_IDENTITY",
                    user,
                    password);
        } catch (SQLException ex) {
            System.err.println("Failed to create the database connection: " + ex.getMessage());
        }
    }

    public static Connection getConnection() {
        if (con == null) {
            createConnection();
        }

        try {
            if (con != null && !con.isValid(5)) {
                createConnection();
            }
        } catch (SQLException ex) {
            System.err.println("Failed to check if the connection is valid: " + ex.getMessage());
        }

        return con;
    }

    public static void closeConnection() {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                System.err.println("Failed to close the database connection: " + e.getMessage());
            } finally {
                con = null;
            }
        }
    }
}
