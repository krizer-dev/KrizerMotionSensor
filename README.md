# Motion Sensor 테스트 앱
이 프로젝트는 크라이저 RK3288 및 RK3399 기반 Android 기기에서 Motion Sensor 기능을 테스트하기 위한 Android 앱 소스입니다.

## 사용 제품
- KZSensor-S1 (모션센서)

제품 관련 데이터 시트 내용은 ***KZSensor-S1_manual.pdf*** 을 참고부탁드립니다.
  

## 지원 기기
- **RK3288 Android Board**
- **RK3399 Android Board**

| 모델      | GPIO Board Value | GPIO Internal Value 
|-----------|------------------| -------------------|
| RK3288    | `GPIO A0`        | `GPIO A0`          |
| RK3399    | `GPIO1-A3`       | `GPIO A2`          |

## 주요 기능
- 동작 감지
- 동작 감지 대기
- 감지 시 UI 변경 및 로그 출력
