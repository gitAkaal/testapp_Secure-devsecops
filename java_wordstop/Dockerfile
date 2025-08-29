# Build stage
FROM maven:3.8-openjdk-11 as builder

WORKDIR /app
COPY java_wordstop/pom.xml .
COPY java_wordstop/src ./src

RUN mvn clean package

# Run stage
FROM openjdk:11-jre-slim

WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

RUN groupadd -r javaapp && useradd -r -g javaapp javauser \
    && chown -R javauser:javaapp /app

USER javauser
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
