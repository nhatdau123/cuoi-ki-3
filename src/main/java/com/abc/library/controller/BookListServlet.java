package com.abc.library.controller;

import com.abc.library.model.Book;
import com.abc.library.service.BookService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * Controller cho Màn hình 1: Danh sách sách trong thư viện (/books).
 */
@WebServlet("/books")
public class BookListServlet extends HttpServlet {
    private final BookService bookService = new BookService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        transferFlashMessages(req);

        try {
            List<Book> books = bookService.getAllBooks();
            req.setAttribute("books", books);
        } catch (SQLException e) {
            req.setAttribute("books", Collections.emptyList());
            req.setAttribute("errorMessage", "Không thể tải danh sách sách từ cơ sở dữ liệu: " + e.getMessage());
        }

        req.getRequestDispatcher("/views/book-list.jsp").forward(req, resp);
    }

    private void transferFlashMessages(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) {
            return;
        }
        Object success = session.getAttribute("successMessage");
        if (success != null) {
            req.setAttribute("successMessage", success);
            session.removeAttribute("successMessage");
        }
        Object error = session.getAttribute("errorMessage");
        if (error != null) {
            req.setAttribute("errorMessage", error);
            session.removeAttribute("errorMessage");
        }
    }
}

