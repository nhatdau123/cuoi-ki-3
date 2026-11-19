package com.abc.library.controller;

import com.abc.library.model.Book;
import com.abc.library.model.Student;
import com.abc.library.service.BookService;
import com.abc.library.service.BorrowCardService;
import com.abc.library.service.StudentService;
import com.abc.library.util.DateUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Controller cho Màn hình 2: Form Mượn Sách (/borrow).
 */
@WebServlet("/borrow")
public class BorrowBookServlet extends HttpServlet {
    private final BookService bookService = new BookService();
    private final StudentService studentService = new StudentService();
    private final BorrowCardService borrowCardService = new BorrowCardService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String bookId = req.getParameter("bookId");

        try {
            Book book = bookService.getBookById(bookId);
            if (book == null) {
                req.getSession().setAttribute("errorMessage", "Không tìm thấy cuốn sách được chọn.");
                resp.sendRedirect(req.getContextPath() + "/books");
                return;
            }
            if (book.getQuantity() <= 0) {
                req.getSession().setAttribute("errorMessage",
                        "Cuốn sách này tạm thời đã cho mượn hết, vui lòng chọn sách khác.");
                resp.sendRedirect(req.getContextPath() + "/books");
                return;
            }

            prepareFormAttributes(req, book);
            req.getRequestDispatcher("/views/borrow-form.jsp").forward(req, resp);
        } catch (SQLException e) {
            req.getSession().setAttribute("errorMessage", "Lỗi hệ thống khi tải form mượn sách: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/books");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String borrowId = req.getParameter("borrowId");
        String bookId = req.getParameter("bookId");
        String studentId = req.getParameter("studentId");
        String returnDate = req.getParameter("returnDate");
        LocalDate borrowDate = LocalDate.now();

        try {
            Map<String, String> errors = borrowCardService.validateAndBorrow(
                    borrowId, bookId, studentId, borrowDate, returnDate
            );

            if (errors.isEmpty()) {
                req.getSession().setAttribute("successMessage",
                        "Mượn sách thành công! Mã mượn sách: " + borrowId.trim());
                resp.sendRedirect(req.getContextPath() + "/books");
                return;
            }

            Book book = bookService.getBookById(bookId);
            if (book == null) {
                req.getSession().setAttribute("errorMessage", "Cuốn sách không tồn tại.");
                resp.sendRedirect(req.getContextPath() + "/books");
                return;
            }

            prepareFormAttributes(req, book);
            req.setAttribute("errors", errors);
            req.setAttribute("borrowId", borrowId == null ? "" : borrowId.trim());
            req.setAttribute("selectedStudentId", studentId == null ? "" : studentId.trim());
            req.setAttribute("returnDate", returnDate == null ? "" : returnDate.trim());
            req.getRequestDispatcher("/views/borrow-form.jsp").forward(req, resp);
        } catch (SQLException e) {
            req.getSession().setAttribute("errorMessage", "Lỗi cơ sở dữ liệu khi mượn sách: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/books");
        }
    }

    private void prepareFormAttributes(HttpServletRequest req, Book book) throws SQLException {
        List<Student> students = studentService.getAllStudents();
        req.setAttribute("book", book);
        req.setAttribute("students", students);
        req.setAttribute("borrowDateStr", DateUtil.formatDisplayDate(LocalDate.now()));
    }
}

