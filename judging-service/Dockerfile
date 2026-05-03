FROM eclipse-temurin:21-jre-alpine
RUN apk add --no-cache g++
WORKDIR /app
COPY target/app.jar app.jar
EXPOSE 8002
ENTRYPOINT ["java", "-jar", "app.jar"]