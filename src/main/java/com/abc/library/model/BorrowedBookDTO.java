package com.abc.library.model;

import java.sql.Date;

/**
 * DTO hiển thị thông tin chi tiết sách đang cho mượn ở Màn hình 3.
 * Sử dụng java.sql.Date (kế thừa java.util.Date) để tương thích trực tiếp với JSTL fmt:formatDate.
 */
public class BorrowedBookDTO {
    private String borrowId;
    private String bookId;
    private String bookTitle;
    private String author;
    private String studentId;
    private String studentName;
    private String className;
    private Date borrowDate;
    private Date returnDate;

    public BorrowedBookDTO() {
    }

    public BorrowedBookDTO(String borrowId, String bookId, String bookTitle, String author,
                           String studentId, String studentName, String className,
                           Date borrowDate, Date returnDate) {
        this.borrowId = borrowId;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.author = author;
        this.studentId = studentId;
        this.studentName = studentName;
        this.className = className;
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

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Date getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(Date borrowDate) {
        this.borrowDate = borrowDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }
}

