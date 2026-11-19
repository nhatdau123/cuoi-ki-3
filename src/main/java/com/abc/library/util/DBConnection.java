package com.abc.library.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Tiện ích quản lý kết nối JDBC tới MySQL database library_management.
 */
public class DBConnection {
    private static final String DB_URL = System.getProperty(
            "db.url",
            "jdbc:mysql://localhost:3306/library_management?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Ho_Chi_Minh"
    );
    private static final String DB_USER = System.getProperty("db.user", "root");
    private static final String DB_PASSWORD = System.getProperty("db.password", "00000000");

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Không tìm thấy MySQL JDBC Driver.", e);
        }
    }

    private DBConnection() {
    }

    /**
     * Mở một kết nối mới tới MySQL.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
}

