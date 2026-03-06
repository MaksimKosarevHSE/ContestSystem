FROM maven:4.0.0-rc-5-eclipse-temurin-21-alpine as build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:resolve

COPY src ./src
RUN mvn clean package

FROM openjdk:27-ea-jdk-oracle

COPY --from=build /app/target/*.jar app.jar

CMD ["java", "-jar", "/app.jar"]