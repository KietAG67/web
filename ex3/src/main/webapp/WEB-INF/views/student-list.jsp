<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Student Management</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<main class="app-shell">
    <header class="app-shell__header">
        <div>
            <p class="app-shell__eyebrow">Lab 04-05</p>
            <h1 class="app-shell__title">Student Management System</h1>
        </div>
        <nav class="app-shell__actions" aria-label="Primary navigation">
            <c:url var="dashboardUrl" value="/student"><c:param name="action" value="dashboard"/></c:url>
            <c:url var="newUrl" value="/student"><c:param name="action" value="new"/></c:url>
            <a class="button button--secondary" href="${dashboardUrl}">Dashboard</a>
            <a class="button" href="${newUrl}">Add student</a>
        </nav>
    </header>

    <c:if test="${not empty flashMessage}">
        <div class="alert alert--${flashType}" role="status"><c:out value="${flashMessage}"/></div>
    </c:if>

    <section class="student-list" aria-labelledby="student-list-title">
        <div class="student-list__toolbar">
            <div>
                <h2 id="student-list-title" class="student-list__title">Students</h2>
                <p class="student-list__count"><c:out value="${totalStudents}"/> record(s)</p>
            </div>
            <form class="search-form" method="get" action="${pageContext.request.contextPath}/student">
                <input type="hidden" name="action" value="list">
                <label class="search-form__label" for="search">Search by code or full name</label>
                <div class="search-form__controls">
                    <input class="search-form__input" id="search" name="search" type="search"
                           value="<c:out value='${search}'/>" placeholder="e.g. ITITIU or Nguyen">
                    <button class="button" type="submit">Search</button>
                    <c:if test="${not empty search}">
                        <a class="button button--secondary" href="${pageContext.request.contextPath}/student?action=list">Clear</a>
                    </c:if>
                </div>
            </form>
        </div>

        <form method="post" action="${pageContext.request.contextPath}/student" id="bulk-form">
            <input type="hidden" name="action" value="confirmBulkDelete">
            <div class="data-table" role="region" aria-label="Student list" tabindex="0">
                <table class="data-table__table">
                    <thead>
                    <tr>
                        <th class="data-table__heading data-table__heading--check">
                            <input id="select-all" type="checkbox" aria-label="Select all students">
                        </th>
                        <th class="data-table__heading">ID</th>
                        <c:set var="nextCodeOrder" value="${sort == 'studentCode' && order == 'asc' ? 'desc' : 'asc'}"/>
                        <c:url var="sortCodeUrl" value="/student">
                            <c:param name="action" value="list"/><c:param name="search" value="${search}"/>
                            <c:param name="sort" value="studentCode"/><c:param name="order" value="${nextCodeOrder}"/>
                            <c:param name="page" value="${page}"/>
                        </c:url>
                        <th class="data-table__heading"><a class="data-table__sort" href="${sortCodeUrl}">Student code <c:if test="${sort == 'studentCode'}">${order == 'asc' ? '▲' : '▼'}</c:if></a></th>
                        <c:set var="nextNameOrder" value="${sort == 'fullName' && order == 'asc' ? 'desc' : 'asc'}"/>
                        <c:url var="sortNameUrl" value="/student">
                            <c:param name="action" value="list"/><c:param name="search" value="${search}"/>
                            <c:param name="sort" value="fullName"/><c:param name="order" value="${nextNameOrder}"/>
                            <c:param name="page" value="${page}"/>
                        </c:url>
                        <th class="data-table__heading"><a class="data-table__sort" href="${sortNameUrl}">Full name <c:if test="${sort == 'fullName'}">${order == 'asc' ? '▲' : '▼'}</c:if></a></th>
                        <c:set var="nextEmailOrder" value="${sort == 'email' && order == 'asc' ? 'desc' : 'asc'}"/>
                        <c:url var="sortEmailUrl" value="/student">
                            <c:param name="action" value="list"/><c:param name="search" value="${search}"/>
                            <c:param name="sort" value="email"/><c:param name="order" value="${nextEmailOrder}"/>
                            <c:param name="page" value="${page}"/>
                        </c:url>
                        <th class="data-table__heading"><a class="data-table__sort" href="${sortEmailUrl}">Email <c:if test="${sort == 'email'}">${order == 'asc' ? '▲' : '▼'}</c:if></a></th>
                        <c:set var="nextMajorOrder" value="${sort == 'major' && order == 'asc' ? 'desc' : 'asc'}"/>
                        <c:url var="sortMajorUrl" value="/student">
                            <c:param name="action" value="list"/><c:param name="search" value="${search}"/>
                            <c:param name="sort" value="major"/><c:param name="order" value="${nextMajorOrder}"/>
                            <c:param name="page" value="${page}"/>
                        </c:url>
                        <th class="data-table__heading"><a class="data-table__sort" href="${sortMajorUrl}">Major <c:if test="${sort == 'major'}">${order == 'asc' ? '▲' : '▼'}</c:if></a></th>
                        <th class="data-table__heading">Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${students}" var="student">
                        <tr class="data-table__row">
                            <td class="data-table__cell"><input class="student-checkbox" type="checkbox" name="ids" value="${student.id}" aria-label="Select <c:out value='${student.studentCode}'/>"></td>
                            <td class="data-table__cell"><c:out value="${student.id}"/></td>
                            <td class="data-table__cell"><c:out value="${student.studentCode}"/></td>
                            <td class="data-table__cell"><c:out value="${student.fullName}"/></td>
                            <td class="data-table__cell"><c:out value="${student.email}"/></td>
                            <td class="data-table__cell"><c:out value="${student.major}"/></td>
                            <td class="data-table__cell data-table__cell--actions">
                                <c:url var="viewUrl" value="/student"><c:param name="action" value="view"/><c:param name="id" value="${student.id}"/></c:url>
                                <c:url var="editUrl" value="/student"><c:param name="action" value="edit"/><c:param name="id" value="${student.id}"/></c:url>
                                <c:url var="deleteUrl" value="/student"><c:param name="action" value="confirmDelete"/><c:param name="id" value="${student.id}"/></c:url>
                                <a class="button button--small button--secondary" href="${viewUrl}">View</a>
                                <a class="button button--small" href="${editUrl}">Edit</a>
                                <a class="button button--small button--danger" href="${deleteUrl}">Delete</a>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>

            <c:if test="${empty students}">
                <p class="student-list__empty">No results found.</p>
            </c:if>

            <div class="student-list__footer">
                <div class="student-list__bulk-actions">
                    <button class="button button--danger" type="submit">Delete selected</button>
                    <c:url var="exportUrl" value="/student"><c:param name="action" value="export"/><c:param name="format" value="csv"/></c:url>
                    <a class="button button--secondary" href="${exportUrl}">Export to CSV</a>
                </div>

                <nav class="pagination" aria-label="Student pages">
                    <span class="pagination__status">Page <c:out value="${page}"/> of <c:out value="${totalPages}"/></span>
                    <c:if test="${page > 1}">
                        <c:url var="previousUrl" value="/student">
                            <c:param name="action" value="list"/><c:param name="search" value="${search}"/>
                            <c:param name="sort" value="${sort}"/><c:param name="order" value="${order}"/>
                            <c:param name="page" value="${page - 1}"/>
                        </c:url>
                        <a class="pagination__link" href="${previousUrl}">Previous</a>
                    </c:if>
                    <c:forEach begin="1" end="${totalPages}" var="pageNumber">
                        <c:url var="pageUrl" value="/student">
                            <c:param name="action" value="list"/><c:param name="search" value="${search}"/>
                            <c:param name="sort" value="${sort}"/><c:param name="order" value="${order}"/>
                            <c:param name="page" value="${pageNumber}"/>
                        </c:url>
                        <a class="pagination__link ${pageNumber == page ? 'pagination__link--active' : ''}" href="${pageUrl}"><c:out value="${pageNumber}"/></a>
                    </c:forEach>
                    <c:if test="${page < totalPages}">
                        <c:url var="nextUrl" value="/student">
                            <c:param name="action" value="list"/><c:param name="search" value="${search}"/>
                            <c:param name="sort" value="${sort}"/><c:param name="order" value="${order}"/>
                            <c:param name="page" value="${page + 1}"/>
                        </c:url>
                        <a class="pagination__link" href="${nextUrl}">Next</a>
                    </c:if>
                </nav>
            </div>
        </form>
    </section>
</main>
<script src="${pageContext.request.contextPath}/js/student-list.js"></script>
</body>
</html>
