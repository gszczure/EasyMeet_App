# Etap budowy
FROM maven:3.8.6-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Etap uruchamiania
FROM openjdk:17-jdk
WORKDIR /app
COPY --from=build /app/target/BACKENDTEST-0.0.1-SNAPSHOT.jar app.jar
ENV PORT 8080
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
