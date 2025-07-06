# Use full JDK for build stage (compile + package)
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

RUN chmod +x mvnw

RUN ./mvnw dependency:go-offline -B

COPY src ./src

RUN ./mvnw clean package -DskipTests

# Use slim JRE for running the app
FROM eclipse-temurin:21-jre-alpine

RUN addgroup -S spring && adduser -S spring -G spring

USER spring:spring

WORKDIR /app

COPY --from=builder /app/target/user-service-0.0.1-SNAPSHOT.jar .

ENTRYPOINT ["java", "-jar", "user-service-0.0.1-SNAPSHOT.jar"]

EXPOSE 8071
