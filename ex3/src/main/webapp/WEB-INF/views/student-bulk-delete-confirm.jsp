<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Confirm bulk deletion</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<main class="app-shell app-shell--narrow">
    <section class="confirmation">
        <h1 class="confirmation__title">Delete selected students?</h1>
        <p class="confirmation__message">All selected records will be deleted in one transaction.</p>
        <ul class="confirmation__list">
            <c:forEach items="${students}" var="student">
                <li><strong><c:out value="${student.studentCode}"/></strong> — <c:out value="${student.fullName}"/></li>
            </c:forEach>
        </ul>
        <form class="confirmation__actions" method="post" action="${pageContext.request.contextPath}/student">
            <input type="hidden" name="action" value="bulkDelete">
            <c:forEach items="${students}" var="student"><input type="hidden" name="ids" value="${student.id}"></c:forEach>
            <button class="button button--danger" type="submit">Delete all selected</button>
            <a class="button button--secondary" href="${pageContext.request.contextPath}/student?action=list">Cancel</a>
        </form>
    </section>
</main>
</body>
</html>
