<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mượn sách - Thư viện Trường học ABC</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<nav class="navbar navbar-expand-lg navbar-dark bg-primary shadow-sm">
    <div class="container">
        <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/books">
            <i class="bi bi-book-half me-2"></i>Thư viện Trường học ABC
        </a>
        <div class="ms-auto">
            <a href="${pageContext.request.contextPath}/borrowed-books" class="btn btn-outline-light">
                <i class="bi bi-bar-chart-line me-1"></i>Thống kê sách đang cho mượn
            </a>
        </div>
    </div>
</nav>

<div class="container my-4">
    <div class="row justify-content-center">
        <div class="col-lg-7 col-md-9">
            <div class="card shadow-sm border-0">
                <div class="card-header bg-primary text-white py-3">
                    <h4 class="mb-0 fw-bold">
                        <i class="bi bi-journal-plus me-2"></i>Mượn sách
                    </h4>
                </div>
                <div class="card-body p-4">

                    <c:if test="${not empty errors['general']}">
                        <div class="alert alert-danger">
                            <i class="bi bi-exclamation-triangle-fill me-2"></i><c:out value="${errors['general']}"/>
                        </div>
                    </c:if>
                    <c:if test="${not empty errors['bookId']}">
                        <div class="alert alert-danger">
                            <i class="bi bi-exclamation-triangle-fill me-2"></i><c:out value="${errors['bookId']}"/>
                        </div>
                    </c:if>

                    <form id="borrowForm" action="${pageContext.request.contextPath}/borrow" method="post" novalidate>
                        <input type="hidden" name="bookId" value="<c:out value='${book.bookId}'/>">

                        <!-- 1. Mã mượn sách -->
                        <div class="mb-3">
                            <label for="borrowId" class="form-label fw-semibold">
                                Mã mượn sách <span class="text-danger">*</span>
                            </label>
                            <input type="text"
                                   class="form-control ${not empty errors['borrowId'] ? 'is-invalid' : ''}"
                                   id="borrowId"
                                   name="borrowId"
                                   value="<c:out value='${borrowId}'/>"
                                   placeholder="Ví dụ: MS-0001"
                                   pattern="^MS-\d{4}$"
                                   required>
                            <div class="form-text">Định dạng bắt buộc: <code>MS-XXXX</code> (4 chữ số, ví dụ: MS-0006).</div>
                            <div class="invalid-feedback" id="borrowIdFeedback">
                                <c:choose>
                                    <c:when test="${not empty errors['borrowId']}">
                                        <c:out value="${errors['borrowId']}"/>
                                    </c:when>
                                    <c:otherwise>
                                        Mã mượn sách phải đúng định dạng MS-XXXX (ví dụ: MS-0001).
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>

                        <!-- 2. Tên sách -->
                        <div class="mb-3">
                            <label for="bookTitle" class="form-label fw-semibold">Tên sách</label>
                            <input type="text"
                                   class="form-control bg-light"
                                   id="bookTitle"
                                   value="<c:out value='${book.title}'/>"
                                   readonly
                                   disabled>
                        </div>

                        <!-- 3. Tên học sinh -->
                        <div class="mb-3">
                            <label for="studentId" class="form-label fw-semibold">
                                Tên học sinh <span class="text-danger">*</span>
                            </label>
                            <select class="form-select ${not empty errors['studentId'] ? 'is-invalid' : ''}"
                                    id="studentId"
                                    name="studentId"
                                    required>
                                <option value="">-- Chọn học sinh --</option>
                                <c:forEach var="student" items="${students}">
                                    <option value="<c:out value='${student.studentId}'/>"
                                            ${student.studentId == selectedStudentId ? 'selected' : ''}>
                                        <c:out value="${student.fullName}"/> - <c:out value="${student.className}"/>
                                    </option>
                                </c:forEach>
                            </select>
                            <div class="invalid-feedback" id="studentIdFeedback">
                                <c:choose>
                                    <c:when test="${not empty errors['studentId']}">
                                        <c:out value="${errors['studentId']}"/>
                                    </c:when>
                                    <c:otherwise>
                                        Vui lòng chọn học sinh mượn sách.
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>

                        <!-- 4. Ngày mượn sách -->
                        <div class="mb-3">
                            <label for="borrowDate" class="form-label fw-semibold">Ngày mượn sách</label>
                            <input type="text"
                                   class="form-control bg-light"
                                   id="borrowDate"
                                   name="borrowDate"
                                   value="<c:out value='${borrowDateStr}'/>"
                                   readonly>
                        </div>

                        <!-- 5. Ngày trả sách -->
                        <div class="mb-4">
                            <label for="returnDate" class="form-label fw-semibold">
                                Ngày trả sách <span class="text-danger">*</span>
                            </label>
                            <input type="text"
                                   class="form-control ${not empty errors['returnDate'] ? 'is-invalid' : ''}"
                                   id="returnDate"
                                   name="returnDate"
                                   value="<c:out value='${returnDate}'/>"
                                   placeholder="dd/MM/yyyy (ví dụ: 25/11/2026)"
                                   required>
                            <div class="form-text">Nhập theo định dạng <code>dd/MM/yyyy</code> và không được trước ngày mượn.</div>
                            <div class="invalid-feedback" id="returnDateFeedback">
                                <c:choose>
                                    <c:when test="${not empty errors['returnDate']}">
                                        <c:out value="${errors['returnDate']}"/>
                                    </c:when>
                                    <c:otherwise>
                                        Vui lòng nhập ngày trả hợp lệ (dd/MM/yyyy) và không trước ngày mượn.
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>

                        <!-- 6. Nút bấm -->
                        <div class="d-flex justify-content-end gap-2">
                            <button type="button"
                                    class="btn btn-secondary px-4"
                                    data-bs-toggle="modal"
                                    data-bs-target="#cancelConfirmModal">
                                <i class="bi bi-arrow-left me-1"></i>Trở về danh sách
                            </button>
                            <button type="submit" class="btn btn-primary px-4">
                                <i class="bi bi-check-lg me-1"></i>Mượn sách
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Modal xác nhận Hủy / Trở về danh sách -->
<div class="modal fade" id="cancelConfirmModal" tabindex="-1" aria-labelledby="cancelConfirmModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border-0 shadow">
            <div class="modal-header bg-warning">
                <h5 class="modal-title fw-bold text-dark" id="cancelConfirmModalLabel">
                    <i class="bi bi-question-circle-fill me-2"></i>Xác nhận hủy
                </h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body py-4">
                <p class="mb-0">Bạn có chắc chắn muốn hủy thao tác mượn sách và quay về danh sách sách không?</p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Ở lại</button>
                <a href="${pageContext.request.contextPath}/books" class="btn btn-danger">Đồng ý quay về</a>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
    (function () {
        const form = document.getElementById('borrowForm');
        const borrowIdInput = document.getElementById('borrowId');
        const studentIdSelect = document.getElementById('studentId');
        const borrowDateInput = document.getElementById('borrowDate');
        const returnDateInput = document.getElementById('returnDate');

        const borrowIdFeedback = document.getElementById('borrowIdFeedback');
        const studentIdFeedback = document.getElementById('studentIdFeedback');
        const returnDateFeedback = document.getElementById('returnDateFeedback');

        const BORROW_ID_REGEX = /^MS-\d{4}$/;
        const DATE_REGEX = /^(\d{2})\/(\d{2})\/(\d{4})$/;

        function parseStrictDate(str) {
            const match = DATE_REGEX.exec(str.trim());
            if (!match) return null;
            const day = parseInt(match[1], 10);
            const month = parseInt(match[2], 10);
            const year = parseInt(match[3], 10);
            if (month < 1 || month > 12 || day < 1 || day > 31) return null;

            const date = new Date(year, month - 1, day);
            if (date.getFullYear() !== year || date.getMonth() !== month - 1 || date.getDate() !== day) {
                return null;
            }
            date.setHours(0, 0, 0, 0);
            return date;
        }

        form.addEventListener('submit', function (event) {
            let isValid = true;

            const borrowIdVal = borrowIdInput.value.trim();
            if (!borrowIdVal) {
                borrowIdInput.classList.add('is-invalid');
                borrowIdFeedback.textContent = 'Mã mượn sách không được để trống.';
                isValid = false;
            } else if (!BORROW_ID_REGEX.test(borrowIdVal)) {
                borrowIdInput.classList.add('is-invalid');
                borrowIdFeedback.textContent = 'Mã mượn sách phải đúng định dạng MS-XXXX (ví dụ: MS-0001).';
                isValid = false;
            } else {
                borrowIdInput.classList.remove('is-invalid');
            }

            if (!studentIdSelect.value) {
                studentIdSelect.classList.add('is-invalid');
                studentIdFeedback.textContent = 'Vui lòng chọn học sinh mượn sách.';
                isValid = false;
            } else {
                studentIdSelect.classList.remove('is-invalid');
            }

            const borrowDate = parseStrictDate(borrowDateInput.value);
            const returnDateVal = returnDateInput.value.trim();
            const returnDate = parseStrictDate(returnDateVal);

            if (!returnDateVal) {
                returnDateInput.classList.add('is-invalid');
                returnDateFeedback.textContent = 'Ngày trả sách không được để trống.';
                isValid = false;
            } else if (!returnDate) {
                returnDateInput.classList.add('is-invalid');
                returnDateFeedback.textContent = 'Ngày trả sách không hợp lệ. Vui lòng nhập đúng định dạng dd/MM/yyyy.';
                isValid = false;
            } else if (borrowDate && returnDate < borrowDate) {
                returnDateInput.classList.add('is-invalid');
                returnDateFeedback.textContent = 'Ngày trả sách không được trước ngày mượn sách (' + borrowDateInput.value + ').';
                isValid = false;
            } else {
                returnDateInput.classList.remove('is-invalid');
            }

            if (!isValid) {
                event.preventDefault();
                event.stopPropagation();
            }
        });
    })();
</script>
</body>
</html>

