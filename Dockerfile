# 기본 이미지로 openJDK 21을 사용
FROM openjdk:21

# 컨테이너 내부에서 애플리케이션 파일을 저장할 작업 디렉토리를 설정
WORKDIR /app

# 빌드한 애플리케이션의 JAR 파일을 컨테이너의 작업 디렉토리로 복사
COPY build/libs/*.jar app.jar

# 컨테이너가 시작될 때 애플리케이션을 실행
CMD ["java", "-jar", "app.jar"]