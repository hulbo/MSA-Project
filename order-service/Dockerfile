FROM openjdk:17
ARG JAR_FILE=target/orderservice.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
