<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Danh sách sách - Thư viện Trường học ABC</title>
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
    <div class="d-flex justify-content-between align-items-center mb-3">
        <div>
            <h2 class="fw-bold text-primary mb-1">Danh sách sách trong thư viện</h2>
            <p class="text-muted mb-0">Chọn cuốn sách bạn muốn mượn từ danh sách bên dưới.</p>
        </div>
        <a href="${pageContext.request.contextPath}/borrowed-books" class="btn btn-info text-white fw-semibold">
            <i class="bi bi-journal-check me-1"></i>Thống kê sách đang cho mượn
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

    <div class="card shadow-sm border-0">
        <div class="card-body p-0">
            <div class="table-responsive">
                <table class="table table-hover table-striped align-middle mb-0">
                    <thead class="table-primary">
                    <tr>
                        <th style="width: 110px;">Mã sách</th>
                        <th style="width: 240px;">Tên sách</th>
                        <th style="width: 180px;">Tác giả</th>
                        <th class="text-center" style="width: 110px;">Số lượng</th>
                        <th>Mô tả</th>
                        <th class="text-center" style="width: 120px;">Hành động</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:choose>
                        <c:when test="${empty books}">
                            <tr>
                                <td colspan="6" class="text-center py-4 text-muted">
                                    Hiện chưa có cuốn sách nào trong thư viện.
                                </td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="book" items="${books}">
                                <tr>
                                    <td class="fw-semibold"><c:out value="${book.bookId}"/></td>
                                    <td class="fw-semibold text-dark"><c:out value="${book.title}"/></td>
                                    <td><c:out value="${book.author}"/></td>
                                    <td class="text-center">
                                        <c:choose>
                                            <c:when test="${book.quantity > 0}">
                                                <span class="badge bg-success fs-6">${book.quantity}</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-danger fs-6">0</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="text-muted"><c:out value="${book.description}"/></td>
                                    <td class="text-center">
                                        <c:choose>
                                            <c:when test="${book.quantity == 0}">
                                                <button type="button"
                                                        class="btn btn-sm btn-primary"
                                                        data-bs-toggle="modal"
                                                        data-bs-target="#outOfStockModal">
                                                    <i class="bi bi-book me-1"></i>Mượn
                                                </button>
                                            </c:when>
                                            <c:otherwise>
                                                <a href="${pageContext.request.contextPath}/borrow?bookId=${book.bookId}"
                                                   class="btn btn-sm btn-primary">
                                                    <i class="bi bi-book me-1"></i>Mượn
                                                </a>
                                            </c:otherwise>
                                        </c:choose>
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

<!-- Modal thông báo hết sách -->
<div class="modal fade" id="outOfStockModal" tabindex="-1" aria-labelledby="outOfStockModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border-0 shadow">
            <div class="modal-header bg-danger text-white">
                <h5 class="modal-title fw-bold" id="outOfStockModalLabel">
                    <i class="bi bi-exclamation-octagon-fill me-2"></i>Lỗi !!!!
                </h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body py-4">
                <p class="mb-0 fs-6">Cuốn sách này tạm thời đã cho mượn hết, vui lòng chọn sách khác</p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-primary px-4" data-bs-dismiss="modal">OK</button>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

