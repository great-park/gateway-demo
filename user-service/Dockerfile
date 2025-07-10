FROM openjdk:21-jdk-slim

WORKDIR /app

# Gradle wrapper와 build 파일들 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .

# 소스 코드 복사
COPY src src

# Gradle 빌드
RUN chmod +x ./gradlew
RUN ./gradlew build -x test

# Spring Boot JAR 파일 찾기 및 복사 (plain이 아닌 JAR 파일)
RUN find build/libs -name "*.jar" ! -name "*plain*" -exec cp {} app.jar \;

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"] 