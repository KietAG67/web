<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html><html lang="en"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1.0">
<title>Student Management</title><link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"></head><body>
<main class="app-shell">
    <header class="app-shell__header"><div><p class="app-shell__eyebrow">Exercise 1</p><h1 class="app-shell__title">Student Management System</h1></div><a class="button" href="${pageContext.request.contextPath}/student?action=new">Add student</a></header>
    <c:if test="${not empty flashMessage}"><div class="alert alert--${flashType}"><c:out value="${flashMessage}"/></div></c:if>
    <section class="student-list">
        <div class="student-list__toolbar"><h2 class="student-list__title">Students</h2>
            <form class="search-form" method="get" action="${pageContext.request.contextPath}/student"><input type="hidden" name="action" value="list"><label class="search-form__label" for="search">Search by code or full name</label>
                <div class="search-form__controls"><input class="search-form__input" id="search" name="search" type="search" value="<c:out value='${search}'/>"><button class="button" type="submit">Search</button><a class="button button--secondary" href="${pageContext.request.contextPath}/student?action=list">Clear</a></div></form>
        </div>
        <div class="data-table" role="region" aria-label="Student list" tabindex="0"><table class="data-table__table"><thead><tr><th class="data-table__heading">ID</th><th class="data-table__heading">Student code</th><th class="data-table__heading">Full name</th><th class="data-table__heading">Email</th><th class="data-table__heading">Major</th><th class="data-table__heading">Actions</th></tr></thead><tbody>
        <c:forEach items="${students}" var="student"><tr class="data-table__row"><td class="data-table__cell"><c:out value="${student.id}"/></td><td class="data-table__cell"><c:out value="${student.studentCode}"/></td><td class="data-table__cell"><c:out value="${student.fullName}"/></td><td class="data-table__cell"><c:out value="${student.email}"/></td><td class="data-table__cell"><c:out value="${student.major}"/></td>
            <td class="data-table__cell data-table__cell--actions"><a class="button button--small button--secondary" href="${pageContext.request.contextPath}/student?action=view&id=${student.id}">View</a><a class="button button--small" href="${pageContext.request.contextPath}/student?action=edit&id=${student.id}">Edit</a><a class="button button--small button--danger" href="${pageContext.request.contextPath}/student?action=confirmDelete&id=${student.id}">Delete</a></td></tr></c:forEach>
        </tbody></table></div><c:if test="${empty students}"><p class="student-list__empty">No results found.</p></c:if>
    </section>
</main></body></html>
