package com.university.util;

import com.university.model.Student;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public final class StudentValidator {
    private static final Pattern CODE = Pattern.compile("[A-Z]{2,6}IU\\d{5}");
    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final Set<String> MAJORS = Set.of("Computer Science", "Information Technology", "Software Engineering", "Data Science");
    private StudentValidator() { }
    public static Map<String, String> validate(Student student) {
        Map<String, String> errors = new LinkedHashMap<>();
        student.setStudentCode(normalize(student.getStudentCode()));
        student.setFullName(normalize(student.getFullName()));
        student.setEmail(normalize(student.getEmail()));
        student.setMajor(normalize(student.getMajor()));
        if (student.getStudentCode().isEmpty()) errors.put("studentCode", "Student code is required.");
        else if (student.getStudentCode().length() > 20 || !CODE.matcher(student.getStudentCode()).matches()) errors.put("studentCode", "Student code must match [A-Z]{2,6}IU followed by 5 digits.");
        if (student.getFullName().isEmpty()) errors.put("fullName", "Full name is required.");
        else if (student.getFullName().length() > 100) errors.put("fullName", "Full name must not exceed 100 characters.");
        if (student.getEmail().isEmpty()) errors.put("email", "Email is required.");
        else if (student.getEmail().length() > 100 || !EMAIL.matcher(student.getEmail()).matches()) errors.put("email", "Enter a valid email address of at most 100 characters.");
        if (!MAJORS.contains(student.getMajor())) errors.put("major", "Select a valid major.");
        return errors;
    }
    private static String normalize(String value) { return value == null ? "" : value.trim(); }
}
