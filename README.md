# UDP Echo Docker

Учебный проект — контейнеризация UDP-эхо-сервера с помощью Docker и Docker Compose.

## Структура проекта

```
UDP-echo-docker/
├── server/
│   └── src/main/
│       ├── java/UDPEchoServer.java       # UDP-эхо-сервер
│       └── resources/Dockerfile          # Образ сервера (multi-stage build)
├── client/
│   └── src/main/
│       ├── java/UDPEchoClient.java        # Тестовый UDP-клиент
│       └── resources/Dockerfile          # Образ клиента (multi-stage build)
├── docker-compose.yaml                  # Для Docker Compose
├── k3s-deployment.yaml                  # Для k3s/Kubernetes
└── pom.xml                               # Maven-конфигурация
```

## Быстрый старт

### Требования

- Docker Engine 24+
- Docker Compose v2

### Запуск

```bash
# Собрать образы и запустить оба сервиса
docker compose up --build

# В фоне
docker compose up --build -d
```

Клиент автоматически подключится к серверу после прохождения его healthcheck и будет отправлять тестовые сообщения каждые 5 секунд.

### Остановка

```bash
docker compose down
```

Удалить вместе с volume:

```bash
docker compose down -v
```

## Описание сервисов

### udp-server

| Параметр        | Значение                        |
|-----------------|---------------------------------|
| Порт            | `8080/udp`                      |
| Образ           | `my-udp-server:v1`              |
| CPU limit       | 0.50 ядра                       |
| RAM limit       | 128 MB                          |
| Healthcheck     | UDP-ping на `127.0.0.1:8080`    |
| Restart policy  | `unless-stopped`                |
| Logs volume     | `vran_server_logs_volume`       |

### udp-client

| Параметр       | Значение            |
|----------------|---------------------|
| Образ          | `my-udp-client:v1`  |
| CPU limit      | 0.50 ядра           |
| RAM limit      | 128 MB              |
| Depends on     | `udp-server` (healthy) |
| Restart policy | `unless-stopped`    |

## Сеть

Оба контейнера работают в изолированной bridge-сети `vran_private_bridge`. Сервер также публикует порт `8080/udp` на хост-машину для внешнего тестирования.

```bash
# Проверить сеть
docker network inspect vran_private_bridge
```

## Полезные команды

```bash
# Статистика ресурсов в реальном времени
docker stats

# Детальная информация о контейнере
docker inspect udp_echo_server_nf

# Логи сервера
docker logs udp_echo_server_nf -f

# Логи клиента
docker logs udp_client_nf -f

# Статус healthcheck
docker inspect --format='{{.State.Health.Status}}' udp_echo_server_nf

# Ручная проверка UDP извне (требует netcat с поддержкой UDP)
echo "hello" | nc -u -w1 127.0.0.1 8080
```

## Сохранение образа в registry

```bash
# Локальный registry (если запущен на localhost:5000)
docker tag my-udp-server:v1 localhost:5000/my-udp-server:v1
docker push localhost:5000/my-udp-server:v1

# Docker Hub
docker tag my-udp-server:v1 <your-username>/my-udp-server:v1
docker push <your-username>/my-udp-server:v1
```
## Развертывание в k3s

### Установка k3s
```bash
curl -sfL https://get.k3s.io | sh -

## Архитектура

```
Host
└─ port 8080/udp ──► vran_private_bridge
├─ udp_echo_server_nf  (:8080/udp)
└─ udp_client_nf       (random port)
```

Клиент общается с сервером по имени DNS-имени сервиса `udp-server` внутри сети Docker.
