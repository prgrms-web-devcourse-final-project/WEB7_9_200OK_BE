FROM amazoncorretto:21

# wget 설치 (amazoncorretto 버전 문제)
RUN yum -y update && yum -y install wget && yum clean all

ARG JAR_FILE=build/libs/*.jar

COPY ${JAR_FILE} app.jar

ENV TZ=Asia/Seoul

ENTRYPOINT ["java","-jar","/app.jar"]