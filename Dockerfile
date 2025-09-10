# 1단계: Gradle로 빌드
FROM gradle:7.6.0-jdk17 AS builder
USER root
WORKDIR /app
COPY . .
RUN ./gradlew clean build -x test --no-daemon

# 2단계: 실행용 이미지
FROM openjdk:17-jdk-slim
COPY --from=builder /app/build/libs/book-cycle-0.0.1-SNAPSHOT.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
