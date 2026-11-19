package com.abc.library.model;

import java.time.LocalDate;

/**
 * Thực thể Thẻ mượn sách (bảng borrow_cards).
 */
public class BorrowCard {
    private String borrowId;
    private String bookId;
    private String studentId;
    private boolean status;
    private LocalDate borrowDate;
    private LocalDate returnDate;

    public BorrowCard() {
    }

    public BorrowCard(String borrowId, String bookId, String studentId, boolean status,
                      LocalDate borrowDate, LocalDate returnDate) {
        this.borrowId = borrowId;
        this.bookId = bookId;
        this.studentId = studentId;
        this.status = status;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
    }

    public String getBorrowId() {
        return borrowId;
    }

    public void setBorrowId(String borrowId) {
        this.borrowId = borrowId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }
}

