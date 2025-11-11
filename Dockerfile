FROM eclipse-temurin:17-jdk

WORKDIR /app

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} /app/supplyx.jar

ENTRYPOINT ["java", "-jar", "/app/supplyx.jar"]
