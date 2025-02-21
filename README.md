# **🐾개발바닥 (Devoot)**

## 📝 서비스 소개
**Devoot**는 개발 강의를 큐레이팅하는 SNS형 플랫폼입니다. 개발자들이 다양한 강의를 비교하고, 학습 여정을 공유하며 협업할 수 있는 서비스를 제공합니다.

## 👥 팀 구성 및 역할
팀명 : 개미

| 역할     | 이름    |
|----------|---------|
| BE/팀장  | 김본    |
| BE 리더  | 정연희  |
| BE 서기  | 권정우  |
| FE/PM    | 김나현  |
| FE 리더  | 정은아  |
| FE 서기  | 이승주  |


## 🎯 프로젝트 목표
- 개발 강의 큐레이션과 비교를 통해 개발자 학습 생태계를 지원.
- 사용자 간 협업 기능을 강화하여 학습 효율성을 증대.


## 💡 주요 기능

### 1. 개발 강의 비교 및 분석 기능
- 다양한 사이트의 개발 강의 가격 및 리뷰 비교.
- 상세 페이지에서 사용자 리뷰 작성.
- 태그와 카테고리를 이용한 강의 검색.
- 강의 가격 변동 시 알림 메일 발송.

### 2. 프로필 공유 기능
- 사용자가 찜한 강의 공유.

### 3. 개발자 친구와 투두 메이트 기능
- 개발 강의 커리큘럼과 연동된 투두 리스트 생성 및 관리.
- 맞팔 상태에서 투두 리스트 공유.


## 🔍 기술 관점

### 1. 맞춤형 크롤링 및 데이터 분류
- ChatGPT API를 활용하여 강의 데이터 크롤링 및 카테고리 분류.
- 태그 추천 기능 제공.

### 2. 크롤링 시스템 구축
- 병렬 크롤링 작업 설계로 효율성 향상.
- 해싱을 통해 데이터베이스 성능 최적화.


## 🔧 주요 기술 스택

| **분류**        | **기술명**                                                                 |
|------------------|----------------------------------------------------------------------------|
| **Backend (BE)**| Spring Boot 3.4.1, JDK 21.0.5                                               |
| **Frontend (FE)**| Node.js, Vue.js, JavaScript, HTML5, Vite                                  |
| **데이터**       | Python 3.9, GPT-4o Mini API                                               |
| **데이터베이스** | MariaDB 11.4.2, ElasticSearch                                             |
| **인프라 (INF)** | AWS EC2, Docker, DockerHub, S3, RDS                                      |
| **크롤러** | AWS ECR, AWS Lambda, AWS EventBridge, AWS CloudWatch                       |
| **CI/CD**        | Jenkins                                                                  |


## 작업 외부 링크
- [**기능 명세서**](https://enchanted-dime-6ce.notion.site/96905e8e30ab4434b2a4e4fd8ebbfdcf?v=0325d5bd05424f3890ced29741ef9285)
- [**와이어프레임**](https://www.figma.com/design/I0vsM6v7JgJXucs0tC6ysu/%EA%B3%B5%ED%86%B5-%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8?node-id=0-1&t=NVOz7wgl5qvsiLNH-1)
- [**ERD**](https://enchanted-dime-6ce.notion.site/DB-855fcdf5d86d45f980a53efd1a5a07ec)
---
**Devoot와 함께 더 나은 개발 학습 여정을 시작하세요!**
