# TRIPFY
Tripfy는 사용자 개인이 가이드가 되어 자신의 여행 코스를 패키지로 다른 사용자들에게 판매하는 사이트입니다
# 사용법
## 1. sts, lombok 설치
Tripfy는 sts, mysql를 이용해 개발했어요

## 2. 파일, 디렉토리 세팅
application.properties line:3 dataSource설정에서 사용하시려는 데이터베이스 정보를 입력해주세요
application.properties line:28 아래의 파일 경로를 본인의 환경에 맞게 설정해주세요

## 3. api 키 세팅
Tripfy를 사용하시기 전에 4종류의 api 키를 기입해주세요

### a. google map javascript
src/main/resource/temlplate/board/get.html line:138

src/main/resource/temlplate/board/modify.html line:229

src/main/resource/temlplate/package/pget.html line:173

src/main/resource/temlplate/package/pmodify.html line:118

src/main/resource/temlplate/package/timelineGet.html line:128

src/main/resource/temlplate/package/timelineWrite.html line:141

### b. google place
src/main/resource/temlplate/user/join.html line:204

### c. bootpay
src/main/resource/temlplate/package/pay.html line:241

src/main/resource/temlplate/package/pay.html line:311

### d. coolSMS
src/main/java/com/t1/tripfy/controller/user/SMSController.java line: 49

src/main/java/com/t1/tripfy/controller/user/SMSController.java line: 50

src/main/java/com/t1/tripfy/controller/user/SMSController.java line: 54

# 번외: 더미 데이터 넣기
+sts에서 프로젝트를 실행합니다

+url: localhost:8080/manager/managerAccess을 입력해 이동합니다

+더미데이터 버튼을 클릭합니다

+sts 콘솔을 통해 데이터가 삽입되는지를 확인합니다

+정상적으로 데이터 삽입이 완료되면 콘솔창에 [성공]이라고 쓰인 에러메시지가 반환됩니다
