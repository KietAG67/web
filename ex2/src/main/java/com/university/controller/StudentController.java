package com.university.controller;

import com.university.dao.StudentDAO;
import com.university.model.Student;
import com.university.util.StudentValidator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

@WebServlet("/student")
public class StudentController extends HttpServlet {
    private static final int PAGE_SIZE = 5;
    private static final Set<String> SORT_KEYS = Set.of("studentCode", "fullName", "email", "major");
    private StudentDAO studentDAO;
    @Override public void init() { studentDAO = new StudentDAO(); }

    @Override protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8"); response.setCharacterEncoding("UTF-8");
        String action = normalize(request.getParameter("action")); if (action.isEmpty()) action = "list";
        try {
            switch (action) {
                case "list" -> list(request, response);
                case "view" -> showStudent(request, response, "/WEB-INF/views/student-detail.jsp", null);
                case "new" -> form(request, response, new Student(), "insert");
                case "edit" -> showStudent(request, response, "/WEB-INF/views/student-form.jsp", "update");
                case "confirmDelete" -> showStudent(request, response, "/WEB-INF/views/student-delete-confirm.jsp", null);
                default -> redirect(request, response, "error", "Unknown action.");
            }
        } catch (SQLException exception) { throw new ServletException("Database operation failed.", exception); }
    }

    @Override protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8"); response.setCharacterEncoding("UTF-8");
        try {
            switch (normalize(request.getParameter("action"))) {
                case "insert" -> save(request, response, false);
                case "update" -> save(request, response, true);
                case "delete" -> delete(request, response);
                default -> redirect(request, response, "error", "Unknown action.");
            }
        } catch (SQLException exception) { throw new ServletException("Database operation failed.", exception); }
    }

    private void list(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
        String search = normalize(request.getParameter("search"));
        String sort = normalize(request.getParameter("sort")); if (!SORT_KEYS.contains(sort)) sort = "studentCode";
        String order = "desc".equalsIgnoreCase(request.getParameter("order")) ? "desc" : "asc";
        int total = studentDAO.countStudents(search);
        int totalPages = Math.max(1, (int) Math.ceil(total / (double) PAGE_SIZE));
        int page = Math.min(parseId(request.getParameter("page"), 1), totalPages);
        request.setAttribute("students", studentDAO.getStudents(search, page, PAGE_SIZE, sort, order));
        request.setAttribute("search", search); request.setAttribute("sort", sort); request.setAttribute("order", order);
        request.setAttribute("page", page); request.setAttribute("totalPages", totalPages); request.setAttribute("totalStudents", total);
        moveFlash(request); request.getRequestDispatcher("/WEB-INF/views/student-list.jsp").forward(request, response);
    }

    private void showStudent(HttpServletRequest request, HttpServletResponse response, String view, String formAction) throws SQLException, ServletException, IOException {
        Student student = studentDAO.getStudentById(parseId(request.getParameter("id"), -1));
        if (student == null) { redirect(request, response, "error", "Student not found."); return; }
        request.setAttribute("student", student); if (formAction != null) request.setAttribute("formAction", formAction);
        request.getRequestDispatcher(view).forward(request, response);
    }

    private void form(HttpServletRequest request, HttpServletResponse response, Student student, String action) throws ServletException, IOException {
        request.setAttribute("student", student); request.setAttribute("formAction", action);
        request.getRequestDispatcher("/WEB-INF/views/student-form.jsp").forward(request, response);
    }

    private void save(HttpServletRequest request, HttpServletResponse response, boolean update) throws SQLException, ServletException, IOException {
        int id = update ? parseId(request.getParameter("id"), -1) : 0;
        if (update && studentDAO.getStudentById(id) == null) { redirect(request, response, "error", "Student not found."); return; }
        Student student = new Student(request.getParameter("studentCode"), request.getParameter("fullName"), request.getParameter("email"), request.getParameter("major"));
        student.setId(id); Map<String, String> errors = StudentValidator.validate(student); Integer excludedId = update ? id : null;
        if (!student.getStudentCode().isEmpty() && studentDAO.studentCodeExists(student.getStudentCode(), excludedId)) errors.put("studentCode", "Student code already exists.");
        if (!student.getEmail().isEmpty() && studentDAO.emailExists(student.getEmail(), excludedId)) errors.put("email", "Email already exists.");
        if (!errors.isEmpty()) { request.setAttribute("errors", errors); form(request, response, student, update ? "update" : "insert"); return; }
        try {
            if (update) studentDAO.updateStudent(student); else studentDAO.insertStudent(student);
            redirect(request, response, "success", update ? "Student updated successfully." : "Student added successfully.");
        } catch (SQLException exception) {
            if (exception.getSQLState() != null && exception.getSQLState().startsWith("23")) {
                errors.put("form", "Student code or email already exists."); request.setAttribute("errors", errors);
                form(request, response, student, update ? "update" : "insert"); return;
            }
            throw exception;
        }
    }

    private void delete(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
        boolean deleted = studentDAO.deleteStudent(parseId(request.getParameter("id"), -1));
        redirect(request, response, deleted ? "success" : "error", deleted ? "Student deleted successfully." : "Student no longer exists.");
    }

    private void redirect(HttpServletRequest request, HttpServletResponse response, String type, String message) throws IOException {
        request.getSession().setAttribute("flashType", type); request.getSession().setAttribute("flashMessage", message);
        response.sendRedirect(request.getContextPath() + "/student?action=list");
    }
    private void moveFlash(HttpServletRequest request) {
        Object message = request.getSession().getAttribute("flashMessage");
        if (message != null) { request.setAttribute("flashType", request.getSession().getAttribute("flashType")); request.setAttribute("flashMessage", message); request.getSession().removeAttribute("flashType"); request.getSession().removeAttribute("flashMessage"); }
    }
    private int parseId(String value, int fallback) { try { int result = Integer.parseInt(value); return result > 0 ? result : fallback; } catch (NumberFormatException exception) { return fallback; } }
    private String normalize(String value) { return value == null ? "" : value.trim(); }
}
