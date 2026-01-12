# WindFall - 네덜란드식 경매 기반 전국 중고 거래 플랫폼
<img width="1256" height="446" alt="image" src="https://github.com/user-attachments/assets/b151f22b-e990-4fa9-b157-502f7833fb52" />

협상(네고) 중심의 중고 거래를 룰 기반 가격 결정(하락형 경매)으로 전환해, 거래를 “피로”가 아닌 “경험”으로 재구성합니다.

<br>

## 💡 기획 의도 & 차별점
**WindFall**은  네덜란드식 경매를 적용하여 판매자는 네고 없이도 합리적 가격에 판매할 수 있고, 

구매자는 “지금 살까 더 기다릴까”의 선택 속에서 운 좋게 싸게 사는 경험(Windfall)과 눈치 게임의 몰입감을 얻습니다.

즉, 중고 거래의 핵심을 ‘협상’에서 ‘선택’ 으로 바꾸는 것이 목표입니다.

<br>

> 1. 국내에서 생소한 네덜란드 경매 도입: 중고거래를 ‘가격 경쟁’이 아닌 ‘결정의 긴장감’으로 설계
> 2. 판매자 친화 구조: 협상 부담을 제거하고, 최저 방어선(Stop Loss)로 판매 리스크를 통제

<br>

---

## 👨‍💻 개발 기간 & 팀원 소개

### **개발 기간**

> 2025.12.03 ~ 2026.01.07
> 

### **팀원**

| <a href="https://github.com/hodakrer.png"><img src="https://github.com/hodakrer.png" width="100"/></a> | <a href="https://github.com/dbghwns123"><img src="https://github.com/dbghwns123.png" width="100"/></a> | <a href="https://github.com/myoungjinseo"><img src="https://github.com/myoungjinseo.png" width="100"/></a> | <a href="https://github.com/dpwl0974"><img src="https://github.com/dpwl0974.png" width="100"/></a> | <a href="https://github.com/DEV-Cheeze"><img src="https://github.com/DEV-Cheeze.png" width="100"/></a> | <a href="https://github.com/zoooooz2"><img src="https://github.com/zoooooz2.png" width="100"/></a>
| :---: | :---: | :---: | :---: | :---: | :---: |
| **최지혁** | **유호준** | **서명진** | **윤예지** | **이창중** | **주정윤** 
| PO | BE 팀장 | 팀원 | 팀원 | 팀원 | 팀원 |

<br> 

---

## 📌 주요 기능

1. Oauth 기반 소셜 로그인 (네이버, 구글, 카카오)
2. Redis ZSet 기반 실시간 인기 랭킹
3. WebSocket 기반 실시간 사용자 집계 및 판매자 감정 표현 (이모지)
4. WebSocket 기반 자동 가격 하락 및 경매 상태 변경
5. 경매 찜 및 태그 검색
6. 경매 검색 (제목 + 내용)
7. Toss Payments 기반 결제
8. WebSocket 기반 채팅
9. 마이페이지
10. SSE 기반 알림

---

## 🔗 ERD

<img width="1099" height="630" alt="image" src="https://github.com/user-attachments/assets/5e5226f8-18b1-454e-810e-5d4683aaf129" />


---

## 🖼️ System Architecture

<img width="3208" height="2301" alt="Image" src="https://github.com/user-attachments/assets/387d0a7f-5569-4fc8-8947-2229496e7611" />

---

## 🛠️ Tech Stack

💻 Backend

![Java](https://img.shields.io/badge/Java-21-007396?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.8-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring%20Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)
![JPA](https://img.shields.io/badge/JPA-59666C?style=for-the-badge)
![QueryDSL](https://img.shields.io/badge/QueryDSL-5E97D0?style=for-the-badge)


![Gradle](https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white)
![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=black)
![WebSocket](https://img.shields.io/badge/WebSocket(STOMP)-000000?style=for-the-badge&logo=websocket&logoColor=white)
![SSE](https://img.shields.io/badge/SSE(Server--Sent%20Events)-000000?style=for-the-badge)


🗄️ Database

![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white)
![H2](https://img.shields.io/badge/H2%20Database-0078D7?style=for-the-badge)

☁️ Infra

![AWS EC2](https://img.shields.io/badge/AWS%20EC2-FF9900?style=for-the-badge&logo=amazonaws&logoColor=white)
![AWS S3](https://img.shields.io/badge/AWS%20S3-569A31?style=for-the-badge&logo=amazons3&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Nginx](https://img.shields.io/badge/Nginx-009639?style=for-the-badge&logo=nginx&logoColor=white)



🚀 CI/CD & Version Control

![GitHub Actions](https://img.shields.io/badge/GitHub%20Actions-2088FF?style=for-the-badge&logo=githubactions&logoColor=white)
![CodeDeploy](https://img.shields.io/badge/CodeDeploy-232F3E?style=for-the-badge&logo=amazonaws&logoColor=white)


📈 Monitoring / Performance

![K6](https://img.shields.io/badge/K6-7D64FF?style=for-the-badge&logo=k6&logoColor=white)


💳 External Services

![Toss Payments](https://img.shields.io/badge/Toss%20Payments-0064FF?style=for-the-badge&logo=toss&logoColor=white)
![Kakao OAuth](https://img.shields.io/badge/Kakao%20OAuth-FFCD00?style=for-the-badge&logo=kakao&logoColor=black)
![Naver OAuth](https://img.shields.io/badge/Naver%20OAuth-03C75A?style=for-the-badge&logo=naver&logoColor=white)
![Google OAuth](https://img.shields.io/badge/Google%20OAuth-4285F4?style=for-the-badge&logo=google&logoColor=white)


--- 

## 🎞️ 시연 영상
[![Video Label](http://img.youtube.com/vi/UX1E70MlFtU/0.jpg)](https://youtu.be/UX1E70MlFtU)

--- 

## 📄 개발 컨벤션

### 🚀 GitHub Flow
- **main**
    - 초기 상태 백업용 브랜치
- **develop**
    - 새로운 기능 개발이 통합되는 기준 브랜치
    - 브랜치 보호 규칙 적용 (PR + 리뷰 후 머지)
- **feature/ & fix/ & refactor/**
    - 개별 기능 개발, 버그 수정, 코드 리팩토링용 브랜치
    - 이슈 단위로 생성하여 작업
    - 작업 완료 후 PR을 통해 devleop에 머지

---


### 🔄 작업 순서

1. **이슈 생성** → 작업 단위 정의
2. **브랜치 생성** → develop 브랜치에서 이슈별 작업 브랜치 생성
3. **Commit & Push**
4. **PR 생성 & 코드 리뷰** → 최소 2명 승인 필요
5. **Merge & 브랜치 정리**
    - 리뷰 완료 후 develop 브랜치로 Merge
    - Merge 후 이슈별 작업 브랜치 삭제

---

### ⚙️ 네이밍 & 작성 규칙

1. **이슈**
    - 제목 규칙 : `[타입] 작업내용`
    - 예시 : `[Feature] 로그인 기능 추가`
    - 본문은 템플릿에 맞춰서 작성
2. **PR**
    - 제목 규칙 : `[타입] 작업내용`
    - 예시 : `[Feature] 로그인 기능 추가`
    - 본문은 템플릿에 맞춰서 작성
3. **브랜치**
    - 생성 기준 : `develop` 브랜치에서 생성
    - 명명 규칙 : `타입/작업 내용`
    - 예시: `feature/조회 기능 개발`
    - `main`과 `develop` 브랜치는 브랜치 보호 규칙이 적용되어, 반드시 PR을 통해 최소 2명의 팀원 리뷰 승인 후에만 머지할 수 있다.
4. **Commit Message 규칙**


    | 타입 | 의미 |
    | --- | --- |
    | **feat** | 새로운 기능 추가 |
    | **fix** | 버그 수정 |
    | **docs** | 문서 수정 (README, 주석 등) |
    | **style** | 코드 스타일 변경 (포맷팅, 세미콜론 등. 기능 변화 없음) |
    | **refactor** | 코드 리팩토링 (동작 변화 없음) |
    | **test** | 테스트 코드 추가/수정 |
    | **chore** | 빌드, 패키지 매니저, 설정 파일 등 유지보수 작업(환경 설정) |
    | **remove** | 파일, 폴더 삭제 |
    | **rename** | 파일, 폴더명 수정 |
    - `타입 : 작업내용`
    - 예시: `feat : 로그인 기능 추가`

---

### 💬 코드 컨벤션

**Google Java Convention**
- 기본 원칙
    - 가독성 최우선
    - 특별한 이유가 없는 경우 IntelliJ IDEA 자동 서식 준수
