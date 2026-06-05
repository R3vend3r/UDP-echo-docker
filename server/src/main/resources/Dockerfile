FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app
COPY src/main/java/UDPEchoServer.java .
RUN javac UDPEchoServer.java

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/*.class .

RUN apk add --no-cache netcat-openbsd

EXPOSE 8080/udp

CMD ["java", "UDPEchoServer"]