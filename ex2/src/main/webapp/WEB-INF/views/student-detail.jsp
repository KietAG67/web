<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Student details</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<main class="app-shell app-shell--narrow">
    <section class="student-detail">
        <h1 class="student-detail__title">Student details</h1>
        <dl class="student-detail__list">
            <dt class="student-detail__term">ID</dt><dd class="student-detail__value"><c:out value="${student.id}"/></dd>
            <dt class="student-detail__term">Student code</dt><dd class="student-detail__value"><c:out value="${student.studentCode}"/></dd>
            <dt class="student-detail__term">Full name</dt><dd class="student-detail__value"><c:out value="${student.fullName}"/></dd>
            <dt class="student-detail__term">Email</dt><dd class="student-detail__value"><c:out value="${student.email}"/></dd>
            <dt class="student-detail__term">Major</dt><dd class="student-detail__value"><c:out value="${student.major}"/></dd>
            <dt class="student-detail__term">Created</dt><dd class="student-detail__value"><c:out value="${student.createdAt}"/></dd>
            <dt class="student-detail__term">Updated</dt><dd class="student-detail__value"><c:out value="${student.updatedAt}"/></dd>
        </dl>
        <div class="student-detail__actions">
            <a class="button" href="${pageContext.request.contextPath}/student?action=edit&id=${student.id}">Edit</a>
            <a class="button button--secondary" href="${pageContext.request.contextPath}/student?action=list">Back</a>
        </div>
    </section>
</main>
</body>
</html>
