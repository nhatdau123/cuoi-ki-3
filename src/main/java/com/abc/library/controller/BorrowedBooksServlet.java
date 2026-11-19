package com.abc.library.controller;

import com.abc.library.model.BorrowedBookDTO;
import com.abc.library.service.BorrowCardService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * Controller cho Màn hình 3: Thống kê sách đang cho mượn, tìm kiếm và trả sách (/borrowed-books).
 */
@WebServlet("/borrowed-books")
public class BorrowedBooksServlet extends HttpServlet {
    private final BorrowCardService borrowCardService = new BorrowCardService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        transferFlashMessages(req);

        String bookTitle = req.getParameter("bookTitle");
        String studentName = req.getParameter("studentName");

        try {
            List<BorrowedBookDTO> borrowedBooks = borrowCardService.searchBorrowedBooks(bookTitle, studentName);
            req.setAttribute("borrowedBooks", borrowedBooks);
        } catch (SQLException e) {
            req.setAttribute("borrowedBooks", Collections.emptyList());
            req.setAttribute("errorMessage", "Không thể tải danh sách sách đang mượn: " + e.getMessage());
        }

        req.setAttribute("bookTitle", bookTitle == null ? "" : bookTitle.trim());
        req.setAttribute("studentName", studentName == null ? "" : studentName.trim());
        req.getRequestDispatcher("/views/borrowed-books.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String borrowId = req.getParameter("borrowId");
        String bookTitle = req.getParameter("bookTitle");
        String studentName = req.getParameter("studentName");

        try {
            borrowCardService.returnBook(borrowId);
            req.getSession().setAttribute("successMessage",
                    "Trả sách thành công cho mã mượn sách " + borrowId + "!");
        } catch (IllegalArgumentException | IllegalStateException e) {
            req.getSession().setAttribute("errorMessage", e.getMessage());
        } catch (SQLException e) {
            req.getSession().setAttribute("errorMessage", "Lỗi cơ sở dữ liệu khi trả sách: " + e.getMessage());
        }

        StringBuilder redirectUrl = new StringBuilder(req.getContextPath()).append("/borrowed-books");
        boolean hasQuery = false;
        if (bookTitle != null && !bookTitle.trim().isEmpty()) {
            redirectUrl.append("?bookTitle=").append(URLEncoder.encode(bookTitle.trim(), StandardCharsets.UTF_8));
            hasQuery = true;
        }
        if (studentName != null && !studentName.trim().isEmpty()) {
            redirectUrl.append(hasQuery ? "&" : "?")
                    .append("studentName=").append(URLEncoder.encode(studentName.trim(), StandardCharsets.UTF_8));
        }

        resp.sendRedirect(redirectUrl.toString());
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

