package com.university.util;

import com.university.model.Student;
import java.io.PrintWriter;
import java.util.List;

public final class CsvWriter {
    private CsvWriter() { }
    public static void writeStudents(PrintWriter writer, List<Student> students) {
        writer.println("student_code,full_name,email,major");
        for (Student student : students) {
            writer.printf("%s,%s,%s,%s%n", escape(student.getStudentCode()), escape(student.getFullName()), escape(student.getEmail()), escape(student.getMajor()));
        }
    }
    private static String escape(String value) {
        String safe = value == null ? "" : value;
        if (safe.contains(",") || safe.contains("\"") || safe.contains("\n") || safe.contains("\r")) return '"' + safe.replace("\"", "\"\"") + '"';
        return safe;
    }
}
