FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY . .
RUN chmod +x mvnw || true
EXPOSE 8080
CMD ["sh", "-c", "./mvnw spring-boot:run -Dspring-boot.run.profiles=prod || mvn spring-boot:run -Dspring-boot.run.profiles=prod"]
