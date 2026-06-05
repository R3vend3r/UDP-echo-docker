FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app
COPY src/main/java/UDPEchoClient.java .
RUN javac UDPEchoClient.java

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/*.class .

ENTRYPOINT ["java", "UDPEchoClient"]
CMD ["localhost", "8080"]