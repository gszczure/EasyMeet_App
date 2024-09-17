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


# Ustaw katalog roboczy na /app
WORKDIR /app

# Skopiuj wszystkie pliki projektu do kontenera
COPY . .

# Zainstaluj zależności i zbuduj aplikację (skipTests oznacza, że pomijamy testy, aby przyspieszyć proces)
RUN ./mvnw clean package -DskipTests

# Uruchom aplikację Spring Boot (plik jar zostanie wygenerowany w katalogu target po budowie)
CMD ["java", "-jar", "/app/target/BACKENDTEST-0.0.1-SNAPSHOT.jar"]
