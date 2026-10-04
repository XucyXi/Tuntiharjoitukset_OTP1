
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B package -DskipTests

FROM eclipse-temurin:21-jre
RUN apt-get update && apt-get install -y --no-install-recommends \
    libgtk-3-0 libxtst6 libxxf86vm1 libgl1 libasound2t64 fontconfig fonts-dejavu \
    && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY --from=build /app/target/temperature-converter.jar app.jar

# XMing pyörii Windowsissa; Docker Desktop tarjoaa host.docker.internal-osoitteen
ENV DISPLAY=host.docker.internal:0.0
CMD ["java", "-jar", "app.jar"]
