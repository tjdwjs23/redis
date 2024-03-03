#!/bin/bash
IMAGE_NAME=public.ecr.aws/k4g0n1m3/redis # Docker Hub에서 사용하는 이미지 이름 -> ECR에서 사용하는 이미지 이름으로 변경
CONTAINER_NAME=my-container # 컨테이너 이름

echo "> 현재 실행중인 Docker 컨테이너 id 확인"
CURRENT_ID=$(docker ps -q -f "name=$CONTAINER_NAME")

if [ -n "$CURRENT_ID" ]
then
  echo "> 현재 구동중인 Docker 컨테이너 $CURRENT_ID 종료"
  docker rm -f $CURRENT_ID
fi

echo "> Docker 이미지 $IMAGE_NAME 업데이트"
docker pull $IMAGE_NAME

echo "> Docker 이미지 $IMAGE_NAME 실행"
docker run -d --name $CONTAINER_NAME -p 8080:8080 $IMAGE_NAME
