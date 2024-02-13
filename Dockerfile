## Docker image for Springboot application gradle
FROM  amazoncorretto:21-alpine-jdk
ARG JAR_FILE=build/libs/*-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
#Expose the authorization server port and the db port
EXPOSE 9000
ENTRYPOINT ["java","-jar","app.jar"]

