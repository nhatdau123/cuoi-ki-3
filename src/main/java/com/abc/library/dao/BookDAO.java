package com.abc.library.dao;

import com.abc.library.model.Book;
import com.abc.library.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO thao tác dữ liệu bảng books.
 */
public class BookDAO {

    private static final String SELECT_ALL_BOOKS =
            "SELECT book_id, title, author, description, quantity FROM books ORDER BY book_id ASC";
    private static final String SELECT_BOOK_BY_ID =
            "SELECT book_id, title, author, description, quantity FROM books WHERE book_id = ?";

    /**
     * Lấy toàn bộ danh sách sách trong thư viện.
     */
    public List<Book> findAll() throws SQLException {
        List<Book> books = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL_BOOKS);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                books.add(mapRow(rs));
            }
        }
        return books;
    }

    /**
     * Tìm sách theo mã sách.
     */
    public Book findById(String bookId) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BOOK_BY_ID)) {
            ps.setString(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    private Book mapRow(ResultSet rs) throws SQLException {
        return new Book(
                rs.getString("book_id"),
                rs.getString("title"),
                rs.getString("author"),
                rs.getString("description"),
                rs.getInt("quantity")
        );
    }
}

