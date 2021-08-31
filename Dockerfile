FROM openjdk:11-jdk-slim
ARG JAR_FILE=build/libs/*-all.jar
ADD ${JAR_FILE} app.jar
EXPOSE 8080
ENV APP_NAME keymanager-rest
ENTRYPOINT [ "java", "-jar", "/app.jar" ]