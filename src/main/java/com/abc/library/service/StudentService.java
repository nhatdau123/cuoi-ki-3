package com.abc.library.service;

import com.abc.library.dao.StudentDAO;
import com.abc.library.model.Student;

import java.sql.SQLException;
import java.util.List;

/**
 * Service xử lý nghiệp vụ liên quan đến Học sinh.
 */
public class StudentService {
    private final StudentDAO studentDAO = new StudentDAO();

    public List<Student> getAllStudents() throws SQLException {
        return studentDAO.findAll();
    }

    public Student getStudentById(String studentId) throws SQLException {
        if (studentId == null || studentId.trim().isEmpty()) {
            return null;
        }
        return studentDAO.findById(studentId.trim());
    }
}

