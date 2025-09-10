# 1단계: Gradle 빌드
FROM gradle:7.6.0-jdk17 AS builder
WORKDIR /app
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew clean build -x test --no-daemon

# 2단계: 실행용 이미지
FROM openjdk:17-jdk-slim
COPY --from=builder /app/build/libs/*.jar /app.jar
COPY wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh
ENTRYPOINT ["/wait-for-it.sh", "book-cycle-db", "3306", "--", "java", "-jar", "/app.jar"]
