# 1. Base Image
FROM eclipse-temurin:21-jre-alpine

# 2. 작업 디렉토리 설정
WORKDIR /app

# 3. 타임존 설정
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/Asia/Seoul /etc/localtime && \
    echo "Asia/Seoul" > /etc/timezone

# 4. 빌드된 JAR 파일 복사
COPY build/libs/*.jar app.jar

# 5. 실행 명령어
# -Dspring.profiles.active=${PROFILE} 옵션 통해 실행 시 환경 선택
# Docker 실행 시 enviroment에 PROFILE=local 또는 PROFILE=prod로 properties 조정 가능
# 기본값 prod 설정
ENV PROFILE=prod

ENTRYPOINT ["sh", "-c", "java -Dspring.profiles.active=${PROFILE} -jar app.jar"]