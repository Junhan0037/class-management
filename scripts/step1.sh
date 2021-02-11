#!/bin/bash

REPOSITORY=/home/ec2-user/app/step1
PROJECT_NAME=class-management

cd $REPOSITORY/$PROJECT_NAME/

echo "> Git Pull"

git pull

echo "> properties 넣기"

cp /home/ec2-user/app/application-real.properties /home/ec2-user/app/step1/class-management/src/main/resources/
mkdir /home/ec2-user/app/step1/class-management/src/test/resources
cp /home/ec2-user/app/application-test.properties /home/ec2-user/app/step1/class-management/src/test/resources/

echo "> 프로젝트 Build 시작"

./mvnw clean package

echo "> step1 디렉토리로 이동"

cd $REPOSITORY

echo "> Build 파일 복사"

cp $REPOSITORY/$PROJECT_NAME/target/*.jar $REPOSITORY/

echo "> 현재 구동중인 애플리케이션 pid 확인"

CURRENT_PID=$(pgrep -f ${PROJECT_NAME}*.jar)

echo "> 현재 구동중인 애플리케이션 pid: $CURRENT_PID"

if [ -z "$CURRENT_PID" ]; then
        echo "> 현재 구동중인 애플리케이션이 없으므로 종료하지 않습니다."
else
        echo "> kill -15 $CURRENT_PID"
        kill -15 $CURRENT_PID
        sleep 5
fi

echo "> 새 애플리케이션 배포"

JAR_NAME=$(ls -tr $REPOSITORY/ | grep *.jar | tail -n 1)

echo "> JAR NAME: $JAR_NAME"

nohup java -jar \
    -Dspring.config.location=classpath:/application.properties,/home/ec2-user/app/application-real.properties,/home/ec2-user/app/application-test.properties \
    -Dspring.profiles.active=real \
    $REPOSITORY/$JAR_NAME 2>&1 &