# syntax=docker/dockerfile:experimental
FROM amazoncorretto:21-alpine-jdk AS build

WORKDIR /workspace/app
COPY . /workspace/app

RUN --mount=type=cache,target=/root/.gradle ./gradlew clean build
RUN mkdir -p build/dependency && (cd build/dependency; jar -xf ../libs/*-SNAPSHOT.jar)

FROM amazoncorretto:21-alpine-jdk AS runtime

RUN addgroup -S spring && adduser -S spring -G spring
USER spring

VOLUME /tmp
ARG DEPENDENCY=/workspace/app/build/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

EXPOSE 9000

ENTRYPOINT ["java","-cp","app:app/lib/*","com.nowhere.springauthserver.SpringAuthServerApplication"]