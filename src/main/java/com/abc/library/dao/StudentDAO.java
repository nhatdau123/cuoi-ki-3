package com.abc.library.dao;

import com.abc.library.model.Student;
import com.abc.library.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO thao tác dữ liệu bảng students.
 */
public class StudentDAO {

    private static final String SELECT_ALL_STUDENTS =
            "SELECT student_id, full_name, class_name FROM students ORDER BY student_id ASC";
    private static final String SELECT_STUDENT_BY_ID =
            "SELECT student_id, full_name, class_name FROM students WHERE student_id = ?";

    /**
     * Lấy toàn bộ danh sách học sinh.
     */
    public List<Student> findAll() throws SQLException {
        List<Student> students = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL_STUDENTS);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                students.add(mapRow(rs));
            }
        }
        return students;
    }

    /**
     * Tìm học sinh theo mã học sinh.
     */
    public Student findById(String studentId) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_STUDENT_BY_ID)) {
            ps.setString(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    private Student mapRow(ResultSet rs) throws SQLException {
        return new Student(
                rs.getString("student_id"),
                rs.getString("full_name"),
                rs.getString("class_name")
        );
    }
}

