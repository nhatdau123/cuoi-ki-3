package com.abc.library.service;

import com.abc.library.dao.BookDAO;
import com.abc.library.dao.BorrowCardDAO;
import com.abc.library.dao.StudentDAO;
import com.abc.library.model.Book;
import com.abc.library.model.BorrowCard;
import com.abc.library.model.BorrowedBookDTO;
import com.abc.library.model.Student;
import com.abc.library.util.DateUtil;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Service xử lý nghiệp vụ mượn sách, trả sách và thống kê sách đang cho mượn.
 */
public class BorrowCardService {
    private static final Pattern BORROW_ID_PATTERN = Pattern.compile("^MS-\\d{4}$");

    private final BorrowCardDAO borrowCardDAO = new BorrowCardDAO();
    private final BookDAO bookDAO = new BookDAO();
    private final StudentDAO studentDAO = new StudentDAO();

    /**
     * Kiểm tra dữ liệu đầu vào và thực hiện mượn sách.
     *
     * @return Map chứa lỗi theo từng trường (rỗng nếu mượn thành công).
     */
    public Map<String, String> validateAndBorrow(String borrowId, String bookId, String studentId,
                                                 LocalDate borrowDate, String returnDateStr) throws SQLException {
        Map<String, String> errors = new HashMap<>();

        String cleanBorrowId = borrowId == null ? "" : borrowId.trim();
        if (cleanBorrowId.isEmpty()) {
            errors.put("borrowId", "Mã mượn sách không được để trống.");
        } else if (!BORROW_ID_PATTERN.matcher(cleanBorrowId).matches()) {
            errors.put("borrowId", "Mã mượn sách phải đúng định dạng MS-XXXX (ví dụ: MS-0001).");
        } else if (borrowCardDAO.existsById(cleanBorrowId)) {
            errors.put("borrowId", "Mã mượn sách '" + cleanBorrowId + "' đã tồn tại. Vui lòng nhập mã khác.");
        }

        Book book = (bookId == null || bookId.trim().isEmpty()) ? null : bookDAO.findById(bookId.trim());
        if (book == null) {
            errors.put("bookId", "Không tìm thấy thông tin sách được chọn.");
        } else if (book.getQuantity() <= 0) {
            errors.put("bookId", "Cuốn sách này tạm thời đã cho mượn hết, vui lòng chọn sách khác.");
        }

        String cleanStudentId = studentId == null ? "" : studentId.trim();
        Student student = cleanStudentId.isEmpty() ? null : studentDAO.findById(cleanStudentId);
        if (student == null) {
            errors.put("studentId", "Vui lòng chọn học sinh hợp lệ.");
        }

        LocalDate parsedReturnDate = null;
        if (returnDateStr == null || returnDateStr.trim().isEmpty()) {
            errors.put("returnDate", "Ngày trả sách không được để trống.");
        } else {
            try {
                parsedReturnDate = DateUtil.parseDisplayDate(returnDateStr);
                if (parsedReturnDate.isBefore(borrowDate)) {
                    errors.put("returnDate", "Ngày trả sách không được trước ngày mượn sách ("
                            + DateUtil.formatDisplayDate(borrowDate) + ").");
                }
            } catch (IllegalArgumentException e) {
                errors.put("returnDate", e.getMessage());
            }
        }

        if (!errors.isEmpty()) {
            return errors;
        }

        BorrowCard card = new BorrowCard(
                cleanBorrowId,
                book.getBookId(),
                student.getStudentId(),
                true,
                borrowDate,
                parsedReturnDate
        );

        try {
            borrowCardDAO.borrowBook(card);
        } catch (IllegalStateException e) {
            errors.put("general", e.getMessage());
        }

        return errors;
    }

    public List<BorrowedBookDTO> searchBorrowedBooks(String bookTitle, String studentName) throws SQLException {
        return borrowCardDAO.findBorrowedBooks(bookTitle, studentName);
    }

    public void returnBook(String borrowId) throws SQLException {
        if (borrowId == null || borrowId.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã mượn sách không hợp lệ.");
        }
        borrowCardDAO.returnBook(borrowId.trim());
    }
}

