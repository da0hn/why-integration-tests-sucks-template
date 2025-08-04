FROM maven:3.9.7-eclipse-temurin-24-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -B package -DskipTests

FROM amazoncorretto:24-alpine3.18
COPY --from=build /app/target/*.jar /app.jar

LABEL maintainer="Gabriel Honda"
LABEL version="1.0"

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
