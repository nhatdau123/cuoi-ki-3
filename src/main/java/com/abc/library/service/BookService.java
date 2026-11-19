package com.abc.library.service;

import com.abc.library.dao.BookDAO;
import com.abc.library.model.Book;

import java.sql.SQLException;
import java.util.List;

/**
 * Service xử lý nghiệp vụ liên quan đến Sách.
 */
public class BookService {
    private final BookDAO bookDAO = new BookDAO();

    public List<Book> getAllBooks() throws SQLException {
        return bookDAO.findAll();
    }

    public Book getBookById(String bookId) throws SQLException {
        if (bookId == null || bookId.trim().isEmpty()) {
            return null;
        }
        return bookDAO.findById(bookId.trim());
    }
}

