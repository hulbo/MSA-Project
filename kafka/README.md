## 🚀 Kafka 설치 및 실행 방법

### 참고
 ※ DB테이블 하나당 Topic를 하나사용함 

### ** 환경변수에 등록해야 할 정보**
```shell
OCI_MARIA_DB_URL=jdbc:mariadb://IP:3306/dbname
OCI_MARIA_DB_USER=id
OCI_MARIA_DB_PASSWORD=password
```
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

### ** 테스트시 사용명령어**
```shell

-- connect 설정확인
curl -X GET http://localhost:8083/connectors
curl -X GET http://localhost:8083/connectors/jdbc-sink/status

-- 토픽생성 확인
docker exec kafka kafka-topics.sh --bootstrap-server kafka:9092 --list
```