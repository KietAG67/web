<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${formAction == 'insert' ? 'Add' : 'Edit'} student</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<main class="app-shell app-shell--narrow">
    <section class="student-form">
        <h1 class="student-form__title">${formAction == 'insert' ? 'Add new student' : 'Edit student'}</h1>
        <c:if test="${not empty errors.form}"><div class="alert alert--error"><c:out value="${errors.form}"/></div></c:if>
        <form method="post" action="${pageContext.request.contextPath}/student" novalidate>
            <input type="hidden" name="action" value="${formAction}">
            <c:if test="${formAction == 'update'}"><input type="hidden" name="id" value="${student.id}"></c:if>

            <div class="student-form__group">
                <label class="student-form__label" for="studentCode">Student code</label>
                <input class="student-form__input ${not empty errors.studentCode ? 'student-form__input--error' : ''}"
                       id="studentCode" name="studentCode" type="text" maxlength="20"
                       value="<c:out value='${student.studentCode}'/>" required>
                <c:if test="${not empty errors.studentCode}"><p class="student-form__error"><c:out value="${errors.studentCode}"/></p></c:if>
            </div>
            <div class="student-form__group">
                <label class="student-form__label" for="fullName">Full name</label>
                <input class="student-form__input ${not empty errors.fullName ? 'student-form__input--error' : ''}"
                       id="fullName" name="fullName" type="text" maxlength="100"
                       value="<c:out value='${student.fullName}'/>" required>
                <c:if test="${not empty errors.fullName}"><p class="student-form__error"><c:out value="${errors.fullName}"/></p></c:if>
            </div>
            <div class="student-form__group">
                <label class="student-form__label" for="email">Email</label>
                <input class="student-form__input ${not empty errors.email ? 'student-form__input--error' : ''}"
                       id="email" name="email" type="email" maxlength="100"
                       value="<c:out value='${student.email}'/>" required>
                <c:if test="${not empty errors.email}"><p class="student-form__error"><c:out value="${errors.email}"/></p></c:if>
            </div>
            <div class="student-form__group">
                <label class="student-form__label" for="major">Major</label>
                <select class="student-form__input ${not empty errors.major ? 'student-form__input--error' : ''}" id="major" name="major" required>
                    <option value="">Select a major</option>
                    <option value="Computer Science" ${student.major == 'Computer Science' ? 'selected' : ''}>Computer Science</option>
                    <option value="Information Technology" ${student.major == 'Information Technology' ? 'selected' : ''}>Information Technology</option>
                    <option value="Software Engineering" ${student.major == 'Software Engineering' ? 'selected' : ''}>Software Engineering</option>
                    <option value="Data Science" ${student.major == 'Data Science' ? 'selected' : ''}>Data Science</option>
                </select>
                <c:if test="${not empty errors.major}"><p class="student-form__error"><c:out value="${errors.major}"/></p></c:if>
            </div>
            <div class="student-form__actions">
                <button class="button" type="submit">Save</button>
                <a class="button button--secondary" href="${pageContext.request.contextPath}/student?action=list">Cancel</a>
            </div>
        </form>
    </section>
</main>
</body>
</html>
