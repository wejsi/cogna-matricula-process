FROM maven:3.8.8-eclipse-temurin-17 AS build
WORKDIR /workspace

# copy pom and download dependencies first for better cache
COPY pom.xml ./
RUN mvn -B -f pom.xml -DskipTests dependency:go-offline

# copy source and build
COPY src ./src
RUN mvn -B -f pom.xml -DskipTests package

FROM eclipse-temurin:17-jdk-alpine
COPY --from=build /workspace/target/*.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
