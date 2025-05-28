# 🚀 Kafka Connect + MariaDB 연동 프로젝트
## 📌 개요
이 프로젝트는 **Kafka Connect JDBC Sink Connector**를 활용하여 **Kafka 메시지를 MariaDB에 자동 저장**하는 환경을 구축하는 방법을 다룹니다.

✔ **Kafka 메시지를 DB에 자동 삽입**  
✔ **Kafka Connect를 Docker 기반으로 설정**  
✔ **MariaDB와 연동하여 CRUD 처리 가능**

---

## 📖 주요 개념 설명

### 🔹 **Zookeeper**
**Zookeeper**는 Kafka 클러스터를 관리하고 조정하는 **분산 코디네이터** 역할을 합니다.  
Kafka의 브로커들이 상태를 공유하고, 리더 선출 및 메타데이터 저장 기능을 수행합니다.  
✅ **브로커의 메타데이터 관리**  
✅ **리더 선출 및 클러스터 조정**

---
### 🔹 **Kafka**
**Kafka**는 **분산 메시징 시스템**으로, **대량의 데이터 스트림을 처리하고 저장**하는 역할을 합니다.  
Kafka는 **Producer(생산자)**와 **Consumer(소비자)** 모델을 기반으로 동작하며, 메시지는 **토픽(Topic)** 단위로 관리됩니다.

✔ **Producer:** 데이터를 Kafka로 전송  
✔ **Broker:** 메시지를 저장하고 전달  
✔ **Consumer:** Kafka의 데이터를 읽어 사용

---

### 🔹 **Kafka Connect**
Kafka Connect는 **Kafka와 외부 시스템(DB, 파일, 클라우드 등)을 연결하는 확장 가능한 프레임워크**입니다.  
Kafka 메시지를 자동으로 **DB, Elasticsearch, Hadoop 등 다양한 저장소로 전송**할 수 있습니다.

✔ **Source Connector:** 외부 데이터 소스를 Kafka로 가져오는 역할  
✔ **Sink Connector:** Kafka 데이터를 외부 저장소에 저장하는 역할

---

### 🔹 **Init 컨테이너**
**Init 컨테이너**는 Kafka 및 Kafka Connect가 실행될 때 필요한 설정을 자동으로 수행하는 역할을 합니다.  
이 프로젝트에서 Init 컨테이너는 **Kafka 토픽을 자동으로 생성하고, Kafka Connect에 JDBC Sink Connector를 자동 등록**하는 기능을 수행합니다.

---

## Kafka 설치 및 실행 방법

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

### ** kafka connect 플로그인 확인
```shell
curl -X GET http://localhost:8083/connector-plugins
```

### ** kafka connect 테스트
```shell

-- connect 설정확인
curl -X GET http://localhost:8083/connectors
curl -X GET http://localhost:8083/connectors/jdbc-sink-orders/status

-- 토픽생성 확인
docker exec kafka kafka-topics.sh --bootstrap-server kafka:9092 --list

-- 데이터 입력 테스트
docker exec -it kafka /bin/sh
kafka-console-producer.sh --broker-list kafka:9092 --topic t_sc_orders
{"schema":{"type":"struct","fields":[{"field":"created_at","type":"string"},{"field":"order_id","type":"string"},{"field":"product_id","type":"string"},{"field":"qty","type":"int32"},{"field":"total_price","type":"double"},{"field":"unit_price","type":"double"},{"field":"user_id","type":"string"}],"optional":false,"name":"Order"},"payload":{"created_at":"2025-05-26T10:30:00","order_id":"ORD1234","product_id":"P001","qty":3,"total_price":450.00,"unit_price":150.00,"user_id":"user01"}}

```