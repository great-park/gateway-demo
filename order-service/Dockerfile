# Order Service용 Dockerfile
FROM eclipse-temurin:21-jdk-alpine as builder
WORKDIR /app
COPY . .
RUN ./gradlew clean build -x test

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"] 