CREATE DATABASE IF NOT EXISTS student_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE student_management;

CREATE TABLE IF NOT EXISTS students (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_code VARCHAR(20) NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    major VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_student_code (student_code),
    INDEX idx_email (email)
) ENGINE=InnoDB;

INSERT INTO students (student_code, full_name, email, major) VALUES
('ITITIU21001', 'Nguyen Van An', 'nguyenvanan@student.hcmiu.edu.vn', 'Information Technology'),
('ITITIU21002', 'Tran Thi Binh', 'tranthib@student.hcmiu.edu.vn', 'Information Technology'),
('ITCSIU21001', 'Le Van Cuong', 'levanc@student.hcmiu.edu.vn', 'Computer Science'),
('ITDSIU21001', 'Pham Thi Dung', 'phamthid@student.hcmiu.edu.vn', 'Data Science'),
('ITSEIU21001', 'Hoang Van Em', 'hoangvane@student.hcmiu.edu.vn', 'Software Engineering')
ON DUPLICATE KEY UPDATE student_code = VALUES(student_code);
