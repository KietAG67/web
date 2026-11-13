<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Confirm deletion</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<main class="app-shell app-shell--narrow">
    <section class="confirmation">
        <h1 class="confirmation__title">Delete student?</h1>
        <p class="confirmation__message">This action cannot be undone.</p>
        <dl class="student-detail__list">
            <dt class="student-detail__term">Student code</dt><dd class="student-detail__value"><c:out value="${student.studentCode}"/></dd>
            <dt class="student-detail__term">Full name</dt><dd class="student-detail__value"><c:out value="${student.fullName}"/></dd>
            <dt class="student-detail__term">Email</dt><dd class="student-detail__value"><c:out value="${student.email}"/></dd>
        </dl>
        <form class="confirmation__actions" method="post" action="${pageContext.request.contextPath}/student">
            <input type="hidden" name="action" value="delete"><input type="hidden" name="id" value="${student.id}">
            <button class="button button--danger" type="submit">Delete</button>
            <a class="button button--secondary" href="${pageContext.request.contextPath}/student?action=list">Cancel</a>
        </form>
    </section>
</main>
</body>
</html>
