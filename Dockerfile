FROM openjdk:21-jdk-slim

ARG JAR_FILE=build/libs/*.jar

COPY ${JAR_FILE} app.jar

ENV TZ=Asia/Seoul

ENTRYPOINT ["java","-jar","/app.jar"]