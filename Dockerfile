## Docker image for Springboot application gradle
FROM  amazoncorretto:17-alpine-jdk
ARG JAR_FILE=build/libs/*-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
#Expose the authorization server port and the db port
EXPOSE 9000 5432
ENTRYPOINT ["java","-jar","app.jar"]

