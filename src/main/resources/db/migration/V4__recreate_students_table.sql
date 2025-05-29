-- 删除旧表
DROP TABLE IF EXISTS students;

-- 创建新表
CREATE TABLE students (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id VARCHAR(50) NOT NULL,
    student_name VARCHAR(255) NOT NULL,
    gender INT NOT NULL,
    phone_number VARCHAR(255) NOT NULL,
    id_card VARCHAR(255),
    address VARCHAR(255),
    education VARCHAR(255),
    graduation_date DATE,
    class_id VARCHAR(255),
    status INT DEFAULT 1,
    create_time DATETIME,
    update_time DATETIME,
    UNIQUE INDEX idx_student_id (student_id)
); 