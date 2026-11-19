package com.abc.library.dao;

import com.abc.library.model.BorrowCard;
import com.abc.library.model.BorrowedBookDTO;
import com.abc.library.util.DBConnection;
import com.abc.library.util.DateUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO thao tác dữ liệu bảng borrow_cards và các giao dịch (Transaction) mượn/trả sách.
 */
public class BorrowCardDAO {

    private static final String EXISTS_BY_ID =
            "SELECT 1 FROM borrow_cards WHERE borrow_id = ?";
    private static final String DECREMENT_BOOK_QUANTITY =
            "UPDATE books SET quantity = quantity - 1 WHERE book_id = ? AND quantity > 0";
    private static final String INSERT_BORROW_CARD =
            "INSERT INTO borrow_cards (borrow_id, book_id, student_id, status, borrow_date, return_date) " +
            "VALUES (?, ?, ?, TRUE, ?, ?)";

    private static final String SELECT_BORROW_FOR_RETURN =
            "SELECT book_id FROM borrow_cards WHERE borrow_id = ? AND status = TRUE FOR UPDATE";
    private static final String MARK_BORROW_RETURNED =
            "UPDATE borrow_cards SET status = FALSE WHERE borrow_id = ? AND status = TRUE";
    private static final String INCREMENT_BOOK_QUANTITY =
            "UPDATE books SET quantity = quantity + 1 WHERE book_id = ?";

    /**
     * Kiểm tra mã mượn sách đã tồn tại trong CSDL hay chưa.
     */
    public boolean existsById(String borrowId) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(EXISTS_BY_ID)) {
            ps.setString(1, borrowId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Thực hiện mượn sách trong một JDBC Transaction:
     * 1. Trừ số lượng sách đi 1 (chỉ khi quantity > 0).
     * 2. Thêm bản ghi mới vào borrow_cards với status = TRUE.
     */
    public void borrowBook(BorrowCard borrowCard) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement psUpdateBook = conn.prepareStatement(DECREMENT_BOOK_QUANTITY)) {
                psUpdateBook.setString(1, borrowCard.getBookId());
                int updatedRows = psUpdateBook.executeUpdate();
                if (updatedRows == 0) {
                    conn.rollback();
                    throw new IllegalStateException("Cuốn sách này tạm thời đã cho mượn hết, vui lòng chọn sách khác.");
                }
            }

            try (PreparedStatement psInsert = conn.prepareStatement(INSERT_BORROW_CARD)) {
                psInsert.setString(1, borrowCard.getBorrowId());
                psInsert.setString(2, borrowCard.getBookId());
                psInsert.setString(3, borrowCard.getStudentId());
                psInsert.setDate(4, DateUtil.toSqlDate(borrowCard.getBorrowDate()));
                psInsert.setDate(5, DateUtil.toSqlDate(borrowCard.getReturnDate()));
                psInsert.executeUpdate();
            }

            conn.commit();
        } catch (SQLException | RuntimeException e) {
            rollbackQuietly(conn);
            throw e;
        } finally {
            resetAndClose(conn);
        }
    }

    /**
     * Tìm kiếm danh sách sách đang cho mượn (status = TRUE) theo tên sách và tên học sinh.
     */
    public List<BorrowedBookDTO> findBorrowedBooks(String bookTitleKeyword, String studentNameKeyword) throws SQLException {
        StringBuilder sql = new StringBuilder(
                "SELECT bc.borrow_id, b.book_id, b.title AS book_title, b.author, " +
                "s.student_id, s.full_name AS student_name, s.class_name, " +
                "bc.borrow_date, bc.return_date " +
                "FROM borrow_cards bc " +
                "INNER JOIN books b ON bc.book_id = b.book_id " +
                "INNER JOIN students s ON bc.student_id = s.student_id " +
                "WHERE bc.status = TRUE"
        );

        List<String> params = new ArrayList<>();
        if (bookTitleKeyword != null && !bookTitleKeyword.trim().isEmpty()) {
            sql.append(" AND b.title LIKE ?");
            params.add("%" + bookTitleKeyword.trim() + "%");
        }
        if (studentNameKeyword != null && !studentNameKeyword.trim().isEmpty()) {
            sql.append(" AND s.full_name LIKE ?");
            params.add("%" + studentNameKeyword.trim() + "%");
        }
        sql.append(" ORDER BY bc.borrow_date DESC, bc.borrow_id ASC");

        List<BorrowedBookDTO> result = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setString(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(new BorrowedBookDTO(
                            rs.getString("borrow_id"),
                            rs.getString("book_id"),
                            rs.getString("book_title"),
                            rs.getString("author"),
                            rs.getString("student_id"),
                            rs.getString("student_name"),
                            rs.getString("class_name"),
                            rs.getDate("borrow_date"),
                            rs.getDate("return_date")
                    ));
                }
            }
        }
        return result;
    }

    /**
     * Thực hiện trả sách trong một JDBC Transaction:
     * 1. Khóa và kiểm tra thẻ mượn đang có status = TRUE.
     * 2. Cập nhật status = FALSE.
     * 3. Cộng thêm 1 vào quantity của sách tương ứng.
     */
    public void returnBook(String borrowId) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            String bookId;
            try (PreparedStatement psSelect = conn.prepareStatement(SELECT_BORROW_FOR_RETURN)) {
                psSelect.setString(1, borrowId);
                try (ResultSet rs = psSelect.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        throw new IllegalStateException("Thẻ mượn không tồn tại hoặc sách đã được trả trước đó.");
                    }
                    bookId = rs.getString("book_id");
                }
            }

            try (PreparedStatement psUpdateCard = conn.prepareStatement(MARK_BORROW_RETURNED)) {
                psUpdateCard.setString(1, borrowId);
                int updated = psUpdateCard.executeUpdate();
                if (updated == 0) {
                    conn.rollback();
                    throw new IllegalStateException("Không thể cập nhật trạng thái trả sách.");
                }
            }

            try (PreparedStatement psUpdateBook = conn.prepareStatement(INCREMENT_BOOK_QUANTITY)) {
                psUpdateBook.setString(1, bookId);
                psUpdateBook.executeUpdate();
            }

            conn.commit();
        } catch (SQLException | RuntimeException e) {
            rollbackQuietly(conn);
            throw e;
        } finally {
            resetAndClose(conn);
        }
    }

    private void rollbackQuietly(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException ignored) {
                // Bỏ qua lỗi phụ khi rollback
            }
        }
    }

    private void resetAndClose(Connection conn) {
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ignored) {
                // Bỏ qua lỗi phụ
            }
            try {
                conn.close();
            } catch (SQLException ignored) {
                // Bỏ qua lỗi phụ
            }
        }
    }
}

