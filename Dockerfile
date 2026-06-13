FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /build

COPY gradlew settings.gradle build.gradle ./
COPY gradle gradle

RUN ./gradlew --no-daemon dependencies 2>/dev/null || true

COPY src src
RUN ./gradlew --no-daemon bootJar

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=builder /build/build/libs/vn-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
