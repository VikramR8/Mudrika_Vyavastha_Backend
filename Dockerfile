FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/mudrikavyavastha-0.0.1-SNAPSHOT.jar mudrikavyavastha-v1.0.jar
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "mudrikavyavastha-v1.0.jar"]