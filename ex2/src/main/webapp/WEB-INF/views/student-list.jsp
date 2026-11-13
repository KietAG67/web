<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html><html lang="en"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1.0">
<title>Student Management</title><link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"></head><body>
<main class="app-shell">
    <header class="app-shell__header"><div><p class="app-shell__eyebrow">Exercise 2</p><h1 class="app-shell__title">Student Management System</h1></div>
        <a class="button" href="${pageContext.request.contextPath}/student?action=new">Add student</a></header>
    <c:if test="${not empty flashMessage}"><div class="alert alert--${flashType}"><c:out value="${flashMessage}"/></div></c:if>
    <section class="student-list">
        <div class="student-list__toolbar"><div><h2 class="student-list__title">Students</h2><p class="student-list__count"><c:out value="${totalStudents}"/> record(s)</p></div>
            <form class="search-form" method="get" action="${pageContext.request.contextPath}/student">
                <input type="hidden" name="action" value="list"><label class="search-form__label" for="search">Search by code or full name</label>
                <div class="search-form__controls"><input class="search-form__input" id="search" name="search" type="search" value="<c:out value='${search}'/>">
                    <button class="button" type="submit">Search</button><a class="button button--secondary" href="${pageContext.request.contextPath}/student?action=list">Clear</a></div>
            </form>
        </div>
        <div class="data-table" role="region" aria-label="Student list" tabindex="0"><table class="data-table__table"><thead><tr>
            <th class="data-table__heading">ID</th>
            <c:set var="codeOrder" value="${sort == 'studentCode' && order == 'asc' ? 'desc' : 'asc'}"/>
            <c:url var="codeUrl" value="/student"><c:param name="action" value="list"/><c:param name="search" value="${search}"/><c:param name="sort" value="studentCode"/><c:param name="order" value="${codeOrder}"/><c:param name="page" value="${page}"/></c:url>
            <th class="data-table__heading"><a class="data-table__sort" href="${codeUrl}">Student code <c:if test="${sort == 'studentCode'}">${order == 'asc' ? '▲' : '▼'}</c:if></a></th>
            <c:set var="nameOrder" value="${sort == 'fullName' && order == 'asc' ? 'desc' : 'asc'}"/>
            <c:url var="nameUrl" value="/student"><c:param name="action" value="list"/><c:param name="search" value="${search}"/><c:param name="sort" value="fullName"/><c:param name="order" value="${nameOrder}"/><c:param name="page" value="${page}"/></c:url>
            <th class="data-table__heading"><a class="data-table__sort" href="${nameUrl}">Full name <c:if test="${sort == 'fullName'}">${order == 'asc' ? '▲' : '▼'}</c:if></a></th>
            <c:set var="emailOrder" value="${sort == 'email' && order == 'asc' ? 'desc' : 'asc'}"/>
            <c:url var="emailUrl" value="/student"><c:param name="action" value="list"/><c:param name="search" value="${search}"/><c:param name="sort" value="email"/><c:param name="order" value="${emailOrder}"/><c:param name="page" value="${page}"/></c:url>
            <th class="data-table__heading"><a class="data-table__sort" href="${emailUrl}">Email <c:if test="${sort == 'email'}">${order == 'asc' ? '▲' : '▼'}</c:if></a></th>
            <c:set var="majorOrder" value="${sort == 'major' && order == 'asc' ? 'desc' : 'asc'}"/>
            <c:url var="majorUrl" value="/student"><c:param name="action" value="list"/><c:param name="search" value="${search}"/><c:param name="sort" value="major"/><c:param name="order" value="${majorOrder}"/><c:param name="page" value="${page}"/></c:url>
            <th class="data-table__heading"><a class="data-table__sort" href="${majorUrl}">Major <c:if test="${sort == 'major'}">${order == 'asc' ? '▲' : '▼'}</c:if></a></th><th class="data-table__heading">Actions</th>
        </tr></thead><tbody>
        <c:forEach items="${students}" var="student"><tr class="data-table__row">
            <td class="data-table__cell"><c:out value="${student.id}"/></td><td class="data-table__cell"><c:out value="${student.studentCode}"/></td>
            <td class="data-table__cell"><c:out value="${student.fullName}"/></td><td class="data-table__cell"><c:out value="${student.email}"/></td><td class="data-table__cell"><c:out value="${student.major}"/></td>
            <td class="data-table__cell data-table__cell--actions"><a class="button button--small button--secondary" href="${pageContext.request.contextPath}/student?action=view&id=${student.id}">View</a><a class="button button--small" href="${pageContext.request.contextPath}/student?action=edit&id=${student.id}">Edit</a><a class="button button--small button--danger" href="${pageContext.request.contextPath}/student?action=confirmDelete&id=${student.id}">Delete</a></td>
        </tr></c:forEach></tbody></table></div>
        <c:if test="${empty students}"><p class="student-list__empty">No results found.</p></c:if>
        <nav class="pagination" aria-label="Student pages"><span class="pagination__status">Page ${page} of ${totalPages}</span>
            <c:if test="${page > 1}"><c:url var="prev" value="/student"><c:param name="action" value="list"/><c:param name="search" value="${search}"/><c:param name="sort" value="${sort}"/><c:param name="order" value="${order}"/><c:param name="page" value="${page - 1}"/></c:url><a class="pagination__link" href="${prev}">Previous</a></c:if>
            <c:forEach begin="1" end="${totalPages}" var="number"><c:url var="pageUrl" value="/student"><c:param name="action" value="list"/><c:param name="search" value="${search}"/><c:param name="sort" value="${sort}"/><c:param name="order" value="${order}"/><c:param name="page" value="${number}"/></c:url><a class="pagination__link ${number == page ? 'pagination__link--active' : ''}" href="${pageUrl}">${number}</a></c:forEach>
            <c:if test="${page < totalPages}"><c:url var="next" value="/student"><c:param name="action" value="list"/><c:param name="search" value="${search}"/><c:param name="sort" value="${sort}"/><c:param name="order" value="${order}"/><c:param name="page" value="${page + 1}"/></c:url><a class="pagination__link" href="${next}">Next</a></c:if>
        </nav>
    </section>
</main></body></html>
