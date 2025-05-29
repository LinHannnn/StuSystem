#!/bin/bash

echo "正在清理并打包应用程序..."
mvn clean package -DskipTests

if [ $? -eq 0 ]; then
    echo "打包成功！"
    echo "JAR文件位置: $(pwd)/target/edumanage-0.0.1-SNAPSHOT.jar"
    echo ""
    echo "部署说明:"
    echo "1. 将JAR文件复制到服务器"
    echo "2. 确保服务器上存在/tmp/uploads/avatars目录并有适当的权限"
    echo "   mkdir -p /tmp/uploads/avatars"
    echo "   chmod 777 /tmp/uploads/avatars"
    echo "3. 使用以下命令运行应用程序:"
    echo "   nohup java -jar edumanage-0.0.1-SNAPSHOT.jar > app.log 2>&1 &"
    echo ""
    echo "4. 验证应用程序是否正常运行:"
    echo "   curl http://localhost:8080/ping"
    echo "   curl http://localhost:8080/simple-test"
    echo "   curl http://localhost:8080/api/health/basic"
else
    echo "打包失败，请检查错误信息"
fi 