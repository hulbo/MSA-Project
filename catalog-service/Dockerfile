FROM openjdk:17
ARG JAR_FILE=target/catalogservice.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
