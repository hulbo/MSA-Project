# ✅ 기본 Java 이미지 설정
FROM openjdk:17-jdk-slim

# ✅ 빌드 시 전달할 JAR 파일 변수 설정
# ARG COMMON_JAR=target/common-start-fixed.jar
ARG USER_JAR=target/userservice.jar

# ✅ 공통 모듈 JAR 복사
# COPY ${COMMON_JAR} /app/lib/common-starter.jar

# ✅ 유저 서비스 JAR 복사
#COPY ${USER_JAR} /app/user-service.jar

COPY ${USER_JAR} app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]