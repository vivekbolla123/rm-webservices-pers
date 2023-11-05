FROM openjdk:18
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-Xms2048m", "-Xmx16384m", "-jar", "/app.jar"]
