package com.university.dao;

import com.university.model.MajorCount;
import com.university.model.MonthlyGrowth;
import com.university.model.Student;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StudentDAO {
    private static final String DB_URL =
            "jdbc:mysql://localhost:3306/student_management?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Bangkok";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = System.getenv("DB_PASSWORD");

    private static final Map<String, String> SORT_COLUMNS = Map.of(
            "studentCode", "student_code",
            "fullName", "full_name",
            "email", "email",
            "major", "major"
    );

    private Connection getConnection() throws SQLException {
        if (DB_PASSWORD == null) {
            throw new SQLException("Environment variable DB_PASSWORD is not configured.");
        }
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public List<Student> getAllStudents() throws SQLException {
        String sql = "SELECT * FROM students ORDER BY student_code";
        List<Student> students = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                students.add(mapStudent(resultSet));
            }
        }
        return students;
    }

    public List<Student> searchStudents(String keyword) throws SQLException {
        String sql = "SELECT * FROM students " +
                "WHERE student_code LIKE ? OR full_name LIKE ? ORDER BY student_code";
        String pattern = "%" + normalize(keyword) + "%";
        List<Student> students = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, pattern);
            statement.setString(2, pattern);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    students.add(mapStudent(resultSet));
                }
            }
        }
        return students;
    }

    public List<Student> getStudents(String search, int page, int pageSize,
                                     String sort, String order) throws SQLException {
        String sortColumn = SORT_COLUMNS.getOrDefault(sort, "student_code");
        String sortOrder = "desc".equalsIgnoreCase(order) ? "DESC" : "ASC";
        boolean hasSearch = !normalize(search).isEmpty();
        String where = hasSearch ? " WHERE student_code LIKE ? OR full_name LIKE ?" : "";
        String sql = "SELECT * FROM students" + where +
                " ORDER BY " + sortColumn + " " + sortOrder + " LIMIT ? OFFSET ?";
        List<Student> students = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            int index = 1;
            if (hasSearch) {
                String pattern = "%" + normalize(search) + "%";
                statement.setString(index++, pattern);
                statement.setString(index++, pattern);
            }
            statement.setInt(index++, pageSize);
            statement.setInt(index, (page - 1) * pageSize);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    students.add(mapStudent(resultSet));
                }
            }
        }
        return students;
    }

    public int countStudents(String search) throws SQLException {
        boolean hasSearch = !normalize(search).isEmpty();
        String where = hasSearch ? " WHERE student_code LIKE ? OR full_name LIKE ?" : "";
        String sql = "SELECT COUNT(*) FROM students" + where;
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            if (hasSearch) {
                String pattern = "%" + normalize(search) + "%";
                statement.setString(1, pattern);
                statement.setString(2, pattern);
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getInt(1) : 0;
            }
        }
    }

    public Student getStudentById(int id) throws SQLException {
        String sql = "SELECT * FROM students WHERE id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? mapStudent(resultSet) : null;
            }
        }
    }

    public boolean insertStudent(Student student) throws SQLException {
        String sql = "INSERT INTO students (student_code, full_name, email, major) VALUES (?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            setStudentFields(statement, student);
            return statement.executeUpdate() == 1;
        }
    }

    public boolean updateStudent(Student student) throws SQLException {
        String sql = "UPDATE students SET student_code = ?, full_name = ?, email = ?, major = ? WHERE id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            setStudentFields(statement, student);
            statement.setInt(5, student.getId());
            return statement.executeUpdate() == 1;
        }
    }

    public boolean deleteStudent(int id) throws SQLException {
        String sql = "DELETE FROM students WHERE id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() == 1;
        }
    }

    public boolean studentCodeExists(String studentCode, Integer excludedId) throws SQLException {
        return valueExists("student_code", studentCode, excludedId);
    }

    public boolean emailExists(String email, Integer excludedId) throws SQLException {
        return valueExists("email", email, excludedId);
    }

    private boolean valueExists(String column, String value, Integer excludedId) throws SQLException {
        String safeColumn = "email".equals(column) ? "email" : "student_code";
        String sql = "SELECT COUNT(*) FROM students WHERE " + safeColumn + " = ?" +
                (excludedId == null ? "" : " AND id <> ?");
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, value);
            if (excludedId != null) {
                statement.setInt(2, excludedId);
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() && resultSet.getInt(1) > 0;
            }
        }
    }

    public int getTotalStudents() throws SQLException {
        return countStudents("");
    }

    public List<MajorCount> getCountByMajor() throws SQLException {
        String sql = "SELECT major, COUNT(*) AS total FROM students GROUP BY major ORDER BY major";
        List<MajorCount> counts = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                counts.add(new MajorCount(resultSet.getString("major"), resultSet.getInt("total")));
            }
        }
        return counts;
    }

    public List<MonthlyGrowth> getGrowthForLastSixMonths() throws SQLException {
        String sql = "SELECT DATE_FORMAT(created_at, '%Y-%m') AS month_key, COUNT(*) AS total " +
                "FROM students WHERE created_at >= DATE_FORMAT(DATE_SUB(CURRENT_DATE, INTERVAL 5 MONTH), '%Y-%m-01') " +
                "GROUP BY month_key ORDER BY month_key";
        Map<String, Integer> counts = new LinkedHashMap<>();
        YearMonth current = YearMonth.now();
        DateTimeFormatter keyFormat = DateTimeFormatter.ofPattern("yyyy-MM");
        DateTimeFormatter labelFormat = DateTimeFormatter.ofPattern("MMM yyyy");
        for (int offset = 5; offset >= 0; offset--) {
            YearMonth month = current.minusMonths(offset);
            counts.put(month.format(keyFormat), 0);
        }

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                counts.put(resultSet.getString("month_key"), resultSet.getInt("total"));
            }
        }

        List<MonthlyGrowth> growth = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            YearMonth month = YearMonth.parse(entry.getKey(), keyFormat);
            growth.add(new MonthlyGrowth(month.format(labelFormat), entry.getValue()));
        }
        return growth;
    }

    public List<Student> getStudentsByIds(List<Integer> ids) throws SQLException {
        if (ids.isEmpty()) {
            return List.of();
        }
        String placeholders = String.join(",", java.util.Collections.nCopies(ids.size(), "?"));
        String sql = "SELECT * FROM students WHERE id IN (" + placeholders + ") ORDER BY student_code";
        List<Student> students = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int index = 0; index < ids.size(); index++) {
                statement.setInt(index + 1, ids.get(index));
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    students.add(mapStudent(resultSet));
                }
            }
        }
        return students;
    }

    public int deleteStudentsInTransaction(List<Integer> ids) throws SQLException {
        if (ids.isEmpty()) {
            return 0;
        }
        String placeholders = String.join(",", java.util.Collections.nCopies(ids.size(), "?"));
        String sql = "DELETE FROM students WHERE id IN (" + placeholders + ")";
        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                for (int index = 0; index < ids.size(); index++) {
                    statement.setInt(index + 1, ids.get(index));
                }
                int deleted = statement.executeUpdate();
                if (deleted != ids.size()) {
                    throw new SQLException("The selected student set changed before deletion.");
                }
                connection.commit();
                return deleted;
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    private void setStudentFields(PreparedStatement statement, Student student) throws SQLException {
        statement.setString(1, student.getStudentCode());
        statement.setString(2, student.getFullName());
        statement.setString(3, student.getEmail());
        statement.setString(4, student.getMajor());
    }

    private Student mapStudent(ResultSet resultSet) throws SQLException {
        Student student = new Student();
        student.setId(resultSet.getInt("id"));
        student.setStudentCode(resultSet.getString("student_code"));
        student.setFullName(resultSet.getString("full_name"));
        student.setEmail(resultSet.getString("email"));
        student.setMajor(resultSet.getString("major"));
        student.setCreatedAt(resultSet.getTimestamp("created_at"));
        student.setUpdatedAt(resultSet.getTimestamp("updated_at"));
        return student;
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}
