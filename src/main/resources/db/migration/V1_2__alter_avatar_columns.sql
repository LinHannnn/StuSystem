-- 修改员工表中的avatar列类型为LONGTEXT
ALTER TABLE employees MODIFY COLUMN avatar LONGTEXT COMMENT '员工头像(base64编码)';

-- 修改用户表中的avatar列类型为LONGTEXT
ALTER TABLE users MODIFY COLUMN avatar LONGTEXT COMMENT '用户头像(base64编码)'; 