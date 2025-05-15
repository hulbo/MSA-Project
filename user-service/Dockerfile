# ✅ 기본 Java 이미지 설정
FROM openjdk:17-jdk-slim

# ✅ 빌드 시 전달할 JAR 파일 변수 설정
ARG COMMON_JAR=/var/lib/jenkins/workspace/SC_userservice/common/target/common-start-fixed.jar
ARG USER_JAR=/var/lib/jenkins/workspace/SC_userservice/user-service/target/userservice.jar

# ✅ 공통 모듈 JAR 복사
COPY ${COMMON_JAR} /app/lib/common-starter.jar

# ✅ 유저 서비스 JAR 복사
COPY ${USER_JAR} /app/user-service.jar

WORKDIR /app

# ✅ 컨테이너 실행 시 JAR 파일 실행
ENTRYPOINT ["java", "-cp", "/app/lib/common-starter.jar:/app/user-service.jar", "org.springframework.boot.loader.JarLauncher"]
