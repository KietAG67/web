<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Student dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<main class="app-shell">
    <header class="app-shell__header">
        <div><p class="app-shell__eyebrow">Exercise 3</p><h1 class="app-shell__title">Statistics dashboard</h1></div>
        <a class="button button--secondary" href="${pageContext.request.contextPath}/student?action=list">Back to students</a>
    </header>
    <section class="dashboard">
        <article class="dashboard__summary"><p class="dashboard__label">Total students</p><p class="dashboard__value">${totalStudents}</p></article>
        <article class="dashboard__card"><h2 class="dashboard__title">Students by major</h2><canvas id="major-chart"></canvas></article>
        <article class="dashboard__card"><h2 class="dashboard__title">Students added — last six months</h2><canvas id="growth-chart"></canvas></article>
    </section>
</main>
<script src="${pageContext.request.contextPath}/js/chart.js"></script>
<script>
    new Chart(document.getElementById('major-chart'), {
        type: 'bar', data: { labels: ${majorLabelsJson}, datasets: [{ label: 'Students', data: ${majorValuesJson}, backgroundColor: '#2563eb' }] },
        options: { responsive: true, plugins: { legend: { display: false } }, scales: { y: { beginAtZero: true, ticks: { precision: 0 } } } }
    });
    new Chart(document.getElementById('growth-chart'), {
        type: 'line', data: { labels: ${monthLabelsJson}, datasets: [{ label: 'Students added', data: ${monthValuesJson}, borderColor: '#0f766e', backgroundColor: 'rgba(15,118,110,.15)', fill: true, tension: .25 }] },
        options: { responsive: true, scales: { y: { beginAtZero: true, ticks: { precision: 0 } } } }
    });
</script>
</body>
</html>
