# Sử dụng Maven để build ứng dụng
FROM maven:3.8.5-openjdk-17 AS builder
WORKDIR /app

# Copy file pom.xml vào container
COPY pom.xml .

# Tải dependencies để cache
RUN mvn dependency:go-offline -B

# Copy toàn bộ mã nguồn
COPY src ./src

# Build ứng dụng
RUN mvn clean package -DskipTests

# Sử dụng JDK để chạy ứng dụng
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copy file JAR từ builder
COPY --from=builder /app/target/*.jar app.jar

# Mở cổng 8080
EXPOSE 8080

# Lệnh khởi động
CMD ["java", "-jar", "app.jar"]
