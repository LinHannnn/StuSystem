-- 删除student_number列，添加student_id列
ALTER TABLE students
DROP COLUMN student_number,
ADD COLUMN student_id VARCHAR(50) NOT NULL AFTER id,
ADD UNIQUE INDEX idx_student_id (student_id); 