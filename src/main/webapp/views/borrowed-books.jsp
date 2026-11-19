<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thống kê sách đang cho mượn - Thư viện Trường học ABC</title>
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
            <a href="${pageContext.request.contextPath}/books" class="btn btn-outline-light">
                <i class="bi bi-list-ul me-1"></i>Danh sách sách
            </a>
        </div>
    </div>
</nav>

<div class="container my-4">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <div>
            <h2 class="fw-bold text-primary mb-1">Thống kê sách đang cho mượn</h2>
            <p class="text-muted mb-0">Danh sách các phiếu mượn hiện đang ở trạng thái chưa trả.</p>
        </div>
        <a href="${pageContext.request.contextPath}/books" class="btn btn-secondary">
            <i class="bi bi-arrow-left me-1"></i>Quay lại danh sách sách
        </a>
    </div>

    <c:if test="${not empty successMessage}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <i class="bi bi-check-circle-fill me-2"></i><c:out value="${successMessage}"/>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>

    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <i class="bi bi-exclamation-triangle-fill me-2"></i><c:out value="${errorMessage}"/>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>

    <!-- 3.1 Form tìm kiếm -->
    <div class="card shadow-sm border-0 mb-4">
        <div class="card-body">
            <form action="${pageContext.request.contextPath}/borrowed-books" method="get" class="row g-3 align-items-end">
                <div class="col-md-5">
                    <label for="bookTitle" class="form-label fw-semibold">Tên sách</label>
                    <input type="text"
                           class="form-control"
                           id="bookTitle"
                           name="bookTitle"
                           value="<c:out value='${bookTitle}'/>"
                           placeholder="Nhập tên sách cần tìm...">
                </div>
                <div class="col-md-5">
                    <label for="studentName" class="form-label fw-semibold">Tên học sinh</label>
                    <input type="text"
                           class="form-control"
                           id="studentName"
                           name="studentName"
                           value="<c:out value='${studentName}'/>"
                           placeholder="Nhập tên học sinh cần tìm...">
                </div>
                <div class="col-md-2 d-flex gap-2">
                    <button type="submit" class="btn btn-primary w-100">
                        <i class="bi bi-search me-1"></i>Tìm kiếm
                    </button>
                </div>
            </form>
        </div>
    </div>

    <!-- Bảng thống kê sách đang cho mượn -->
    <div class="card shadow-sm border-0">
        <div class="card-body p-0">
            <div class="table-responsive">
                <table class="table table-hover table-striped align-middle mb-0">
                    <thead class="table-primary">
                    <tr>
                        <th>Mã mượn sách</th>
                        <th>Tên sách</th>
                        <th>Tác giả</th>
                        <th>Tên học sinh</th>
                        <th>Lớp</th>
                        <th class="text-center">Ngày mượn</th>
                        <th class="text-center">Ngày trả</th>
                        <th class="text-center">Hành động</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:choose>
                        <c:when test="${empty borrowedBooks}">
                            <tr>
                                <td colspan="8" class="text-center py-4 text-muted">
                                    Không có dữ liệu sách đang cho mượn phù hợp.
                                </td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="item" items="${borrowedBooks}">
                                <tr>
                                    <td class="fw-semibold"><c:out value="${item.borrowId}"/></td>
                                    <td class="fw-semibold text-dark"><c:out value="${item.bookTitle}"/></td>
                                    <td><c:out value="${item.author}"/></td>
                                    <td><c:out value="${item.studentName}"/></td>
                                    <td><span class="badge bg-secondary"><c:out value="${item.className}"/></span></td>
                                    <td class="text-center">
                                        <fmt:formatDate value="${item.borrowDate}" pattern="dd/MM/yyyy"/>
                                    </td>
                                    <td class="text-center">
                                        <fmt:formatDate value="${item.returnDate}" pattern="dd/MM/yyyy"/>
                                    </td>
                                    <td class="text-center">
                                        <button type="button"
                                                class="btn btn-sm btn-warning fw-semibold btn-return-book"
                                                data-borrow-id="<c:out value='${item.borrowId}'/>"
                                                data-student-name="<c:out value='${item.studentName}'/>"
                                                data-book-title="<c:out value='${item.bookTitle}'/>"
                                                data-bs-toggle="modal"
                                                data-bs-target="#returnBookModal">
                                            <i class="bi bi-arrow-return-left me-1"></i>Trả sách
                                        </button>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<!-- 3.2 Modal xác nhận Trả sách -->
<div class="modal fade" id="returnBookModal" tabindex="-1" aria-labelledby="returnBookModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border-0 shadow">
            <form action="${pageContext.request.contextPath}/borrowed-books" method="post">
                <input type="hidden" name="borrowId" id="modalBorrowId">
                <input type="hidden" name="bookTitle" value="<c:out value='${bookTitle}'/>">
                <input type="hidden" name="studentName" value="<c:out value='${studentName}'/>">

                <div class="modal-header bg-warning">
                    <h5 class="modal-title fw-bold text-dark" id="returnBookModalLabel">
                        <i class="bi bi-arrow-return-left me-2"></i>Trả sách
                    </h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body py-4">
                    <p class="mb-0 fs-6" id="returnConfirmMessage"></p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Không</button>
                    <button type="submit" class="btn btn-primary fw-semibold">Trả Sách</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
    (function () {
        const returnButtons = document.querySelectorAll('.btn-return-book');
        const modalBorrowId = document.getElementById('modalBorrowId');
        const returnConfirmMessage = document.getElementById('returnConfirmMessage');

        returnButtons.forEach(function (btn) {
            btn.addEventListener('click', function () {
                const borrowId = btn.getAttribute('data-borrow-id');
                const studentName = btn.getAttribute('data-student-name');
                const bookTitle = btn.getAttribute('data-book-title');

                modalBorrowId.value = borrowId;
                returnConfirmMessage.textContent =
                    'Học sinh ' + studentName + ' thực hiện trả sách ' + bookTitle + '.';
            });
        });
    })();
</script>
</body>
</html>

