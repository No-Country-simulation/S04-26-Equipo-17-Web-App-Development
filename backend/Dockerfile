FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

RUN ./mvnw dependency:go-offline -B

COPY src ./src

RUN ./mvnw clean package -DskipTests -B

FROM eclipse-temurin:21-jre

RUN useradd -m -u 1001 user

USER user

WORKDIR /home/user/app

COPY --from=build --chown=user /app/target/*.jar app.jar

EXPOSE 7860

ENTRYPOINT ["java", "-jar", "/home/user/app/app.jar"]