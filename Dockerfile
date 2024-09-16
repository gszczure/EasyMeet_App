FROM openjdk:17-jdk

ARG JAR_FILE=target/BACKENDTEST-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} app.jar

ENV PORT 8080

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app.jar"]

