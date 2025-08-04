FROM amazoncorretto:24-alpine3.18

LABEL maintainer="Gabriel Honda"
LABEL version="1.0"

COPY target/*.jar /app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app.jar"]
