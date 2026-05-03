FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY target/app.jar app.jar
EXPOSE 8001
ENTRYPOINT ["java", "-jar", "app.jar"]