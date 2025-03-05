
# Using podman to build instad of docker

# Build stage using mavn 
FROM maven:3.8.4-openjdk-11-slim AS build

# working directory
WORKDIR /app

# Copy the pom.xml and dependency 
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the source code and build the JAR file
COPY src /app/src
RUN mvn clean package -DskipTests

#Runtime stage
FROM openjdk:11-jre-slim

# Set environment variables for version and timestamp
ARG VERSION
ARG TIMESTAMP
ENV VERSION=${VERSION}
ENV TIMESTAMP=${TIMESTAMP}


COPY --from=build /app/target/hello-world*.jar /app/hello-world.jar

# Expose the port
EXPOSE 8080

# Command to run the Java application
CMD ["java", "-jar", "/app/hello-world.jar"]
