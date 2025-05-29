# 学生管理系统 (StuSystem)

这是一个基于Spring Boot开发的教育管理系统，提供学生、教师、课程和班级等信息的管理功能。

## 技术栈

- Java 17
- Spring Boot 3.3.2
- Spring Security
- Spring Data JPA
- MySQL
- JWT认证
- Swagger/OpenAPI文档

## 功能特点

- 用户认证与授权
- 学生信息管理
- 教师/员工信息管理
- 课程管理
- 班级管理
- 部门管理
- 文件上传

## 快速开始

### 前置条件

- JDK 17或更高版本
- Maven 3.6或更高版本
- MySQL 8.0或更高版本

### 安装步骤

1. 克隆仓库
```bash
git clone https://github.com/你的用户名/StuSystem.git
cd StuSystem
```

2. 配置数据库
在`src/main/resources/application.properties`中配置您的数据库连接信息

3. 构建项目
```bash
mvn clean package
```

4. 运行项目
```bash
java -jar target/edumanage-0.0.1-SNAPSHOT.jar
```

## API文档

启动应用后，访问以下URL查看API文档：
```
http://localhost:8080/swagger-ui.html
```

## 许可证

[MIT](LICENSE) 