## 🚀 Kafka 설치 및 실행 방법

### ** Docker Compose를 사용하여 Kafka 실행**
```shell
docker-compose up -d
```

### ** Docker Compose를 사용하여 Kafka 종료**
```shell
docker-compose down
```

### ** Docker Compose를 사용하여 Kafka 송신테스트**
```shell
docker exec -it kafka kafka-console-producer.sh --bootstrap-server kafka:9092 --topic user-events
```

### ** Docker Compose를 사용하여 Kafka 수신테스트**
```shell
docker exec -it kafka kafka-console-consumer.sh --bootstrap-server kafka:9092 --topic user-events --group new-consumer-group --from-beginning
```
