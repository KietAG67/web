package com.university.dao;

import com.university.model.Student;
import java.sql.*;
import java.util.*;

public class StudentDAO {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/student_management?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Bangkok";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = System.getenv("DB_PASSWORD");
    private static final Map<String, String> SORT_COLUMNS = Map.of("studentCode", "student_code", "fullName", "full_name", "email", "email", "major", "major");

    private Connection getConnection() throws SQLException {
        if (DB_PASSWORD == null) throw new SQLException("Environment variable DB_PASSWORD is not configured.");
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public List<Student> searchStudents(String keyword) throws SQLException {
        return queryStudents(keyword, 1, Integer.MAX_VALUE, "studentCode", "asc");
    }

    public List<Student> getStudents(String search, int page, int pageSize, String sort, String order) throws SQLException {
        return queryStudents(search, page, pageSize, sort, order);
    }

    private List<Student> queryStudents(String search, int page, int pageSize, String sort, String order) throws SQLException {
        String keyword = normalize(search);
        boolean hasSearch = !keyword.isEmpty();
        String column = SORT_COLUMNS.getOrDefault(sort, "student_code");
        String direction = "desc".equalsIgnoreCase(order) ? "DESC" : "ASC";
        String sql = "SELECT * FROM students" + (hasSearch ? " WHERE student_code LIKE ? OR full_name LIKE ?" : "")
                + " ORDER BY " + column + " " + direction + " LIMIT ? OFFSET ?";
        List<Student> students = new ArrayList<>();
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            int index = 1;
            if (hasSearch) {
                statement.setString(index++, "%" + keyword + "%");
                statement.setString(index++, "%" + keyword + "%");
            }
            statement.setInt(index++, pageSize);
            statement.setLong(index, (long) (page - 1) * pageSize);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) students.add(mapStudent(resultSet));
            }
        }
        return students;
    }

    public int countStudents(String search) throws SQLException {
        String keyword = normalize(search);
        boolean hasSearch = !keyword.isEmpty();
        String sql = "SELECT COUNT(*) FROM students" + (hasSearch ? " WHERE student_code LIKE ? OR full_name LIKE ?" : "");
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            if (hasSearch) {
                statement.setString(1, "%" + keyword + "%");
                statement.setString(2, "%" + keyword + "%");
            }
            try (ResultSet resultSet = statement.executeQuery()) { return resultSet.next() ? resultSet.getInt(1) : 0; }
        }
    }

    public Student getStudentById(int id) throws SQLException {
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM students WHERE id = ?")) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) { return resultSet.next() ? mapStudent(resultSet) : null; }
        }
    }

    public boolean insertStudent(Student student) throws SQLException {
        String sql = "INSERT INTO students (student_code, full_name, email, major) VALUES (?, ?, ?, ?)";
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            setFields(statement, student); return statement.executeUpdate() == 1;
        }
    }

    public boolean updateStudent(Student student) throws SQLException {
        String sql = "UPDATE students SET student_code = ?, full_name = ?, email = ?, major = ? WHERE id = ?";
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            setFields(statement, student); statement.setInt(5, student.getId()); return statement.executeUpdate() == 1;
        }
    }

    public boolean deleteStudent(int id) throws SQLException {
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement("DELETE FROM students WHERE id = ?")) {
            statement.setInt(1, id); return statement.executeUpdate() == 1;
        }
    }

    public boolean studentCodeExists(String value, Integer excludedId) throws SQLException { return valueExists("student_code", value, excludedId); }
    public boolean emailExists(String value, Integer excludedId) throws SQLException { return valueExists("email", value, excludedId); }

    private boolean valueExists(String column, String value, Integer excludedId) throws SQLException {
        String safeColumn = "email".equals(column) ? "email" : "student_code";
        String sql = "SELECT COUNT(*) FROM students WHERE " + safeColumn + " = ?" + (excludedId == null ? "" : " AND id <> ?");
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, value); if (excludedId != null) statement.setInt(2, excludedId);
            try (ResultSet resultSet = statement.executeQuery()) { return resultSet.next() && resultSet.getInt(1) > 0; }
        }
    }

    private void setFields(PreparedStatement statement, Student student) throws SQLException {
        statement.setString(1, student.getStudentCode()); statement.setString(2, student.getFullName());
        statement.setString(3, student.getEmail()); statement.setString(4, student.getMajor());
    }
    private Student mapStudent(ResultSet resultSet) throws SQLException {
        Student student = new Student(); student.setId(resultSet.getInt("id")); student.setStudentCode(resultSet.getString("student_code"));
        student.setFullName(resultSet.getString("full_name")); student.setEmail(resultSet.getString("email")); student.setMajor(resultSet.getString("major"));
        student.setCreatedAt(resultSet.getTimestamp("created_at")); student.setUpdatedAt(resultSet.getTimestamp("updated_at")); return student;
    }
    private String normalize(String value) { return value == null ? "" : value.trim(); }
}
