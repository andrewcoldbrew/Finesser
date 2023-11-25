package myApp.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {
    private static Connection con;

    private static final String user = "m4sdqj5v85xpq43auqub";

    private static final String password = "pscale_pw_6E6rCBEJg9NBsfLGqEtCVFoCMpwxiaoRgXcP5YcNxYS";

    public static void createConnection() {
        try {
            con = DriverManager.getConnection(
                    "jdbc:mysql://aws.connect.psdb.cloud/bitsdatabase?sslMode=VERIFY_IDENTITY",
                    user,
                    password);
        } catch (SQLException ex) {
            // log an exception, for example:
            System.out.println("Failed to create the database connection.");
        }
    }

    public static Connection getConnection() {
        return con;
    }

    public static void closeConnection() {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                // log an exception, for example:
                System.out.println("Failed to close the database connection.");
            } finally {
                con = null; // reset the connection
            }
        }
    }
}
