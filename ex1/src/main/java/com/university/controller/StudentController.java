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

@WebServlet("/student")
public class StudentController extends HttpServlet {
    private StudentDAO studentDAO;
    @Override public void init() { studentDAO = new StudentDAO(); }
    @Override protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8"); response.setCharacterEncoding("UTF-8"); String action = text(request.getParameter("action")); if (action.isEmpty()) action = "list";
        try {
            switch (action) {
                case "list" -> list(request, response);
                case "view" -> show(request, response, "/WEB-INF/views/student-detail.jsp", null);
                case "new" -> form(request, response, new Student(), "insert");
                case "edit" -> show(request, response, "/WEB-INF/views/student-form.jsp", "update");
                case "confirmDelete" -> show(request, response, "/WEB-INF/views/student-delete-confirm.jsp", null);
                default -> redirect(request, response, "error", "Unknown action.");
            }
        } catch (SQLException exception) { throw new ServletException("Database operation failed.", exception); }
    }
    @Override protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8"); response.setCharacterEncoding("UTF-8");
        try {
            switch (text(request.getParameter("action"))) {
                case "insert" -> save(request, response, false);
                case "update" -> save(request, response, true);
                case "delete" -> delete(request, response);
                default -> redirect(request, response, "error", "Unknown action.");
            }
        } catch (SQLException exception) { throw new ServletException("Database operation failed.", exception); }
    }
    private void list(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
        String search = text(request.getParameter("search"));
        request.setAttribute("students", search.isEmpty() ? studentDAO.getAllStudents() : studentDAO.searchStudents(search)); request.setAttribute("search", search);
        Object message = request.getSession().getAttribute("flashMessage"); if (message != null) { request.setAttribute("flashType", request.getSession().getAttribute("flashType")); request.setAttribute("flashMessage", message); request.getSession().removeAttribute("flashType"); request.getSession().removeAttribute("flashMessage"); }
        request.getRequestDispatcher("/WEB-INF/views/student-list.jsp").forward(request, response);
    }
    private void show(HttpServletRequest request, HttpServletResponse response, String view, String formAction) throws SQLException, ServletException, IOException {
        Student student = studentDAO.getStudentById(id(request.getParameter("id"))); if (student == null) { redirect(request, response, "error", "Student not found."); return; }
        request.setAttribute("student", student); if (formAction != null) request.setAttribute("formAction", formAction); request.getRequestDispatcher(view).forward(request, response);
    }
    private void form(HttpServletRequest request, HttpServletResponse response, Student student, String action) throws ServletException, IOException {
        request.setAttribute("student", student); request.setAttribute("formAction", action); request.getRequestDispatcher("/WEB-INF/views/student-form.jsp").forward(request, response);
    }
    private void save(HttpServletRequest request, HttpServletResponse response, boolean update) throws SQLException, ServletException, IOException {
        int studentId = update ? id(request.getParameter("id")) : 0; if (update && studentDAO.getStudentById(studentId) == null) { redirect(request, response, "error", "Student not found."); return; }
        Student student = new Student(request.getParameter("studentCode"), request.getParameter("fullName"), request.getParameter("email"), request.getParameter("major")); student.setId(studentId);
        Map<String, String> errors = StudentValidator.validate(student); Integer excluded = update ? studentId : null;
        if (!student.getStudentCode().isEmpty() && studentDAO.studentCodeExists(student.getStudentCode(), excluded)) errors.put("studentCode", "Student code already exists.");
        if (!student.getEmail().isEmpty() && studentDAO.emailExists(student.getEmail(), excluded)) errors.put("email", "Email already exists.");
        if (!errors.isEmpty()) { request.setAttribute("errors", errors); form(request, response, student, update ? "update" : "insert"); return; }
        try { if (update) studentDAO.updateStudent(student); else studentDAO.insertStudent(student); redirect(request, response, "success", update ? "Student updated successfully." : "Student added successfully."); }
        catch (SQLException exception) { if (exception.getSQLState() != null && exception.getSQLState().startsWith("23")) { errors.put("form", "Student code or email already exists."); request.setAttribute("errors", errors); form(request, response, student, update ? "update" : "insert"); } else throw exception; }
    }
    private void delete(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException { boolean deleted = studentDAO.deleteStudent(id(request.getParameter("id"))); redirect(request, response, deleted ? "success" : "error", deleted ? "Student deleted successfully." : "Student no longer exists."); }
    private void redirect(HttpServletRequest request, HttpServletResponse response, String type, String message) throws IOException { request.getSession().setAttribute("flashType", type); request.getSession().setAttribute("flashMessage", message); response.sendRedirect(request.getContextPath() + "/student?action=list"); }
    private int id(String value) { try { int parsed = Integer.parseInt(value); return parsed > 0 ? parsed : -1; } catch (NumberFormatException exception) { return -1; } }
    private String text(String value) { return value == null ? "" : value.trim(); }
}
