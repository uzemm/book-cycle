FROM openjdk:17-jdk-slim
COPY build/libs/*.jar /app.jar
COPY wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh
ENTRYPOINT ["/wait-for-it.sh", "book-cycle-db", "3306", "--", "java", "-jar", "/app.jar"]
