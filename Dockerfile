# syntax=docker/dockerfile:experimental
FROM eclipse-temurin:21-jdk-alpine AS build
RUN pwd
COPY . /workspace/app
WORKDIR /workspace/app
RUN pwd
# print the current directory in the log
RUN chmod +x ./gradlew && ls -la
RUN --mount=type=cache,target=/root/.gradle ./gradlew clean build
RUN mkdir -p build/dependency && (cd build/dependency; jar -xf ../libs/*-SNAPSHOT.jar)

# Fetch runtime JDK for the new building stage
FROM eclipse-temurin:21-jre-alpine
# Create user and group
RUN addgroup -S spring && adduser -S spring -G spring
USER spring

# ARG is used to define build-time variables, 
# they're not available after the image has been built. DEPENDENCY variable is reused in many places, so it better to declare it once
VOLUME /tmp
ARG DEPENDENCY=/workspace/app/build/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

# Expose port
EXPOSE 9000

# Start your java application
ENTRYPOINT ["java","-cp","app:app/lib/*","com.nowhere.springauthserver.SpringAuthServerApplication"]