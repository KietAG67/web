package com.university.controller;

import com.university.dao.StudentDAO;
import com.university.model.MajorCount;
import com.university.model.MonthlyGrowth;
import com.university.model.Student;
import com.university.util.CsvWriter;
import com.university.util.StudentValidator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@WebServlet("/student")
public class StudentController extends HttpServlet {
    private static final int PAGE_SIZE = 5;
    private static final Set<String> SORT_KEYS = Set.of("studentCode", "fullName", "email", "major");

    private StudentDAO studentDAO;

    @Override
    public void init() {
        studentDAO = new StudentDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        String action = normalize(request.getParameter("action"));
        if (action.isEmpty()) {
            action = "list";
        }

        try {
            switch (action) {
                case "list" -> listStudents(request, response);
                case "view" -> viewStudent(request, response);
                case "new" -> showForm(request, response, new Student(), "insert");
                case "edit" -> editStudent(request, response);
                case "confirmDelete" -> confirmDelete(request, response);
                case "dashboard" -> showDashboard(request, response);
                case "export" -> exportCsv(request, response);
                default -> redirectWithMessage(request, response, "error", "Unknown action.");
            }
        } catch (SQLException exception) {
            throw new ServletException("Database operation failed.", exception);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        String action = normalize(request.getParameter("action"));

        try {
            switch (action) {
                case "insert" -> insertStudent(request, response);
                case "update" -> updateStudent(request, response);
                case "delete" -> deleteStudent(request, response);
                case "confirmBulkDelete" -> confirmBulkDelete(request, response);
                case "bulkDelete" -> bulkDelete(request, response);
                default -> redirectWithMessage(request, response, "error", "Unknown action.");
            }
        } catch (SQLException exception) {
            throw new ServletException("Database operation failed.", exception);
        }
    }

    private void listStudents(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        String search = normalize(request.getParameter("search"));
        String sort = normalize(request.getParameter("sort"));
        if (!SORT_KEYS.contains(sort)) {
            sort = "studentCode";
        }
        String order = "desc".equalsIgnoreCase(request.getParameter("order")) ? "desc" : "asc";
        int requestedPage = parsePositiveInt(request.getParameter("page"), 1);
        int totalStudents = studentDAO.countStudents(search);
        int totalPages = Math.max(1, (int) Math.ceil(totalStudents / (double) PAGE_SIZE));
        int page = Math.min(requestedPage, totalPages);

        request.setAttribute("students", studentDAO.getStudents(search, page, PAGE_SIZE, sort, order));
        request.setAttribute("search", search);
        request.setAttribute("sort", sort);
        request.setAttribute("order", order);
        request.setAttribute("page", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalStudents", totalStudents);
        moveFlashToRequest(request);
        request.getRequestDispatcher("/WEB-INF/views/student-list.jsp").forward(request, response);
    }

    private void viewStudent(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        Student student = findRequestedStudent(request);
        if (student == null) {
            redirectWithMessage(request, response, "error", "Student not found.");
            return;
        }
        request.setAttribute("student", student);
        request.getRequestDispatcher("/WEB-INF/views/student-detail.jsp").forward(request, response);
    }

    private void editStudent(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        Student student = findRequestedStudent(request);
        if (student == null) {
            redirectWithMessage(request, response, "error", "Student not found.");
            return;
        }
        showForm(request, response, student, "update");
    }

    private void showForm(HttpServletRequest request, HttpServletResponse response,
                          Student student, String formAction) throws ServletException, IOException {
        request.setAttribute("student", student);
        request.setAttribute("formAction", formAction);
        request.getRequestDispatcher("/WEB-INF/views/student-form.jsp").forward(request, response);
    }

    private void insertStudent(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        Student student = readStudent(request);
        Map<String, String> errors = StudentValidator.validate(student);
        addDuplicateErrors(errors, student, null);
        if (!errors.isEmpty()) {
            request.setAttribute("errors", errors);
            showForm(request, response, student, "insert");
            return;
        }

        try {
            studentDAO.insertStudent(student);
            redirectWithMessage(request, response, "success", "Student added successfully.");
        } catch (SQLException exception) {
            if (isConstraintViolation(exception)) {
                errors.put("form", "Student code or email already exists.");
                request.setAttribute("errors", errors);
                showForm(request, response, student, "insert");
                return;
            }
            throw exception;
        }
    }

    private void updateStudent(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        int id = parsePositiveInt(request.getParameter("id"), -1);
        if (id < 1 || studentDAO.getStudentById(id) == null) {
            redirectWithMessage(request, response, "error", "Student not found.");
            return;
        }
        Student student = readStudent(request);
        student.setId(id);
        Map<String, String> errors = StudentValidator.validate(student);
        addDuplicateErrors(errors, student, id);
        if (!errors.isEmpty()) {
            request.setAttribute("errors", errors);
            showForm(request, response, student, "update");
            return;
        }

        try {
            studentDAO.updateStudent(student);
            redirectWithMessage(request, response, "success", "Student updated successfully.");
        } catch (SQLException exception) {
            if (isConstraintViolation(exception)) {
                errors.put("form", "Student code or email already exists.");
                request.setAttribute("errors", errors);
                showForm(request, response, student, "update");
                return;
            }
            throw exception;
        }
    }

    private void confirmDelete(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        Student student = findRequestedStudent(request);
        if (student == null) {
            redirectWithMessage(request, response, "error", "Student not found.");
            return;
        }
        request.setAttribute("student", student);
        request.getRequestDispatcher("/WEB-INF/views/student-delete-confirm.jsp").forward(request, response);
    }

    private void deleteStudent(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        int id = parsePositiveInt(request.getParameter("id"), -1);
        if (id < 1 || !studentDAO.deleteStudent(id)) {
            redirectWithMessage(request, response, "error", "Student no longer exists.");
            return;
        }
        redirectWithMessage(request, response, "success", "Student deleted successfully.");
    }

    private void exportCsv(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        if (!"csv".equalsIgnoreCase(request.getParameter("format"))) {
            redirectWithMessage(request, response, "error", "Unsupported export format.");
            return;
        }
        response.setContentType("text/csv;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=students.csv");
        response.getWriter().write('\ufeff');
        CsvWriter.writeStudents(response.getWriter(), studentDAO.getAllStudents());
    }

    private void showDashboard(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        List<MajorCount> majorCounts = studentDAO.getCountByMajor();
        List<MonthlyGrowth> growth = studentDAO.getGrowthForLastSixMonths();
        request.setAttribute("totalStudents", studentDAO.getTotalStudents());
        request.setAttribute("majorLabelsJson", stringArrayJson(
                majorCounts.stream().map(MajorCount::getMajor).toList()));
        request.setAttribute("majorValuesJson", intArrayJson(
                majorCounts.stream().map(MajorCount::getCount).toList()));
        request.setAttribute("monthLabelsJson", stringArrayJson(
                growth.stream().map(MonthlyGrowth::getMonth).toList()));
        request.setAttribute("monthValuesJson", intArrayJson(
                growth.stream().map(MonthlyGrowth::getCount).toList()));
        request.getRequestDispatcher("/WEB-INF/views/student-dashboard.jsp").forward(request, response);
    }

    private void confirmBulkDelete(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        List<Integer> ids = readIds(request);
        List<Student> students = studentDAO.getStudentsByIds(ids);
        if (students.isEmpty() || students.size() != ids.size()) {
            redirectWithMessage(request, response, "error", "Select valid students to delete.");
            return;
        }
        request.setAttribute("students", students);
        request.getRequestDispatcher("/WEB-INF/views/student-bulk-delete-confirm.jsp")
                .forward(request, response);
    }

    private void bulkDelete(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        List<Integer> ids = readIds(request);
        if (ids.isEmpty()) {
            redirectWithMessage(request, response, "error", "No students were selected.");
            return;
        }
        try {
            int deleted = studentDAO.deleteStudentsInTransaction(ids);
            redirectWithMessage(request, response, "success", deleted + " students deleted successfully.");
        } catch (SQLException exception) {
            redirectWithMessage(request, response, "error", "Bulk delete was rolled back; no students were deleted.");
        }
    }

    private Student readStudent(HttpServletRequest request) {
        return new Student(
                request.getParameter("studentCode"),
                request.getParameter("fullName"),
                request.getParameter("email"),
                request.getParameter("major")
        );
    }

    private void addDuplicateErrors(Map<String, String> errors, Student student, Integer excludedId)
            throws SQLException {
        if (!student.getStudentCode().isEmpty()
                && studentDAO.studentCodeExists(student.getStudentCode(), excludedId)) {
            errors.put("studentCode", "Student code already exists.");
        }
        if (!student.getEmail().isEmpty() && studentDAO.emailExists(student.getEmail(), excludedId)) {
            errors.put("email", "Email already exists.");
        }
    }

    private Student findRequestedStudent(HttpServletRequest request) throws SQLException {
        int id = parsePositiveInt(request.getParameter("id"), -1);
        return id < 1 ? null : studentDAO.getStudentById(id);
    }

    private List<Integer> readIds(HttpServletRequest request) {
        String[] values = request.getParameterValues("ids");
        if (values == null) {
            return List.of();
        }
        Set<Integer> ids = new LinkedHashSet<>();
        for (String value : values) {
            int id = parsePositiveInt(value, -1);
            if (id > 0) {
                ids.add(id);
            }
        }
        return new ArrayList<>(ids);
    }

    private void redirectWithMessage(HttpServletRequest request, HttpServletResponse response,
                                     String type, String message) throws IOException {
        request.getSession().setAttribute("flashType", type);
        request.getSession().setAttribute("flashMessage", message);
        response.sendRedirect(request.getContextPath() + "/student?action=list");
    }

    private void moveFlashToRequest(HttpServletRequest request) {
        Object type = request.getSession().getAttribute("flashType");
        Object message = request.getSession().getAttribute("flashMessage");
        if (message != null) {
            request.setAttribute("flashType", type);
            request.setAttribute("flashMessage", message);
            request.getSession().removeAttribute("flashType");
            request.getSession().removeAttribute("flashMessage");
        }
    }

    private int parsePositiveInt(String value, int fallback) {
        try {
            int parsed = Integer.parseInt(value);
            return parsed > 0 ? parsed : fallback;
        } catch (NumberFormatException exception) {
            return fallback;
        }
    }

    private boolean isConstraintViolation(SQLException exception) {
        return exception.getSQLState() != null && exception.getSQLState().startsWith("23");
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    private String stringArrayJson(List<String> values) {
        return values.stream().map(this::jsonString).collect(java.util.stream.Collectors.joining(",", "[", "]"));
    }

    private String intArrayJson(List<Integer> values) {
        return values.stream().map(String::valueOf).collect(java.util.stream.Collectors.joining(",", "[", "]"));
    }

    private String jsonString(String value) {
        String escaped = value.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\r", "\\r").replace("\n", "\\n")
                .replace("<", "\\u003c").replace(">", "\\u003e").replace("&", "\\u0026")
                .replace("\u2028", "\\u2028").replace("\u2029", "\\u2029");
        return "\"" + escaped + "\"";
    }
}
