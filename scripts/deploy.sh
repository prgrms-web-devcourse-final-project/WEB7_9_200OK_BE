#!/bin/bash

# 1. 작업 디렉토리 이동 (appspec.yml에서 지정한 destination과 일치)
cd /home/ubuntu/windfall-app

# 2. 환경변수 로드 (.env 파일이 있으면 적용)
if [ -f .env ]; then
  export $(cat .env | xargs)
fi

# 3. 현재 실행 중인 서비스 확인 (Blue인지 Green인지)
# docker-compose ps 명령어로 'wind-fall-green' 서비스가 'Up' 상태인지 확인
IS_GREEN=$(docker-compose ps | grep wind-fall-green | grep Up)

# 4. 배포 대상(Target) 및 중단 대상(Stop) 설정
if [ -z "$IS_GREEN" ]; then
  echo "### BLUE => GREEN 배포 시작 ###"
  TARGET_SERVICE="wind-fall-green"
  TARGET_CONTAINER="wind-fall-green"
  STOP_SERVICE="wind-fall-blue"
else
  echo "### GREEN => BLUE 배포 시작 ###"
  TARGET_SERVICE="wind-fall-blue"
  TARGET_CONTAINER="wind-fall-blue"
  STOP_SERVICE="wind-fall-green"
fi

echo "Target Service: $TARGET_SERVICE"

# 5. 이미지 가져오기 (Redis, MySQL, Nginx 등 최신 이미지도 같이 확인)
docker-compose pull

# 6. 새 서비스 실행
# --no-deps: 연결된 다른 컨테이너(Redis, MySQL 등)를 재시작하지 않음
echo "$TARGET_SERVICE 컨테이너를 실행합니다..."
docker-compose up -d --no-deps $TARGET_SERVICE

# 7. Health Check (Docker Native 방식)
echo "Health Check 시작..."

for i in {1..20}; do
  # docker inspect로 컨테이너의 상태(healthy, starting, unhealthy) 확인
  # docker-compose.yml에 healthcheck 설정이 되어 있어야 함
  HEALTH_STATUS=$(docker inspect --format='{{.State.Health.Status}}' $TARGET_CONTAINER)

  echo "   Checking... ($i/20) - Status: $HEALTH_STATUS"

  if [ "$HEALTH_STATUS" = "healthy" ]; then
    echo "Health Check 통과! 서버가 정상적으로 실행되었습니다."
    break
  fi

  if [ $i -eq 20 ]; then
    echo "Health Check 실패. 배포를 중단하고 롤백합니다."
    docker-compose stop $TARGET_SERVICE
    exit 1
  fi

  sleep 10
done

# 8. Nginx 포트 스위칭 (도커 내부 Nginx 제어)
echo "Nginx 설정을 변경합니다..."

# service-url.inc 파일 수정 (호스트 경로 ./nginx/conf.d/service-url.inc)
# 주의: Nginx는 도커 내부망을 사용하므로 'localhost'가 아닌 '컨테이너 이름'으로 연결
echo "set \$service_url http://$TARGET_CONTAINER:8080;" | sudo tee ./nginx/conf.d/service-url.inc

# Nginx 컨테이너에게 설정 리로드 명령 전송
echo "Nginx Reloading..."
docker-compose exec -T nginx nginx -s reload

# 9. 구버전 서비스 중단 및 정리
echo "이전 버전($STOP_SERVICE)을 중단합니다..."
docker-compose stop $STOP_SERVICE

echo "미사용 이미지를 정리합니다..."
docker image prune -f

echo "배포가 성공적으로 완료되었습니다!"