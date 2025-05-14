# ⚙️ Config Server
중앙 설정 서버 역할을 하는 Spring Cloud Config 기반 서비스입니다.

## 📌 기능
- Git 기반 설정 관리
- 모든 서비스에 설정 제공

## ※ 환경설정 변경값 호출법
- common.yml, rabbit.yml 변경후 사용 
- [방법1] 각서비스에서 호출: /actuator/refresh -> 해당서버만 적용
- [방법2] 각서비스에서 호출: /actuator/busrefresh -> rabbitMQ 서버사용해서 연결된 전체 자동으로 적용됨

## ※ 로컬설정
- 환경변수 추가해야함: 
  - GIT_CONFIG_REPO_ID = GITHUB 아이디
  - GIT_CONFIG_REPO_PASSWORD = GITHUB 토큰 비밀번호
  - RABBITMQ_HOST = 레빗MQ 서버 주소
  - RABBITMQ_ID = 레빗MQ 서버 ID(비밀번호 동일하게 설정해놓았음)