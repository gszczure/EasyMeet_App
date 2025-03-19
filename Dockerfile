FROM openjdk:17-jdk

#ARG JAR_FILE=target/BACKENDTEST-0.0.1-SNAPSHOT.jar
#
#COPY ${JAR_FILE} app.jar
#
#ENV PORT 8080
#
#EXPOSE 8080
#
#ENTRYPOINT ["java", "-jar", "/app.jar"]


WORKDIR /app

COPY . .

RUN ./mvnw clean package -DskipTests

CMD ["java", "-jar", "/app/target/BACKENDTEST-0.0.1-SNAPSHOT.jar"]
