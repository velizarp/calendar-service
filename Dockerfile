FROM eclipse-temurin:21-jdk-alpine as build
WORKDIR /workspace/app

COPY pom.xml .

# Install Maven and download dependencies first to leverage Docker cache
RUN apk add --no-cache maven
RUN mvn dependency:go-offline

# Copy source code only after dependencies are downloaded
COPY src src

# Build the application
RUN mvn package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
VOLUME /tmp
COPY --from=build /workspace/app/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]