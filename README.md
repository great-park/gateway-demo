# Gateway Demo Project

Spring Cloud Gateway를 활용한 마이크로서비스 아키텍처 데모 프로젝트입니다.

## 🏗️ 아키텍처

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Client        │    │   API Gateway   │    │   Backend       │
│                 │    │   (gwtest)      │    │   (webtestK)    │
│                 │───▶│   Port: 8080    │───▶│   Port: 8081    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │
                                ▼
                       ┌─────────────────┐
                       │   User Service  │
                       │   (user-service)│
                       │   Port: 8082    │
                       └─────────────────┘
```

## 📁 프로젝트 구조

- **gwtest**: Spring Cloud Gateway (API Gateway)
- **webtestK**: 백엔드 웹 서비스 (테스트용)
- **user-service**: 사용자 관리 서비스 (JPA + H2 DB)

## 🚀 실행 방법

### 방법 1: 개별 실행

#### 1. 백엔드 서비스 실행
```bash
cd webtestK
./gradlew bootRun
```

#### 2. 사용자 서비스 실행
```bash
cd user-service
./gradlew bootRun
```

#### 3. API Gateway 실행
```bash
cd gwtest
./gradlew bootRun
```

### 방법 2: Docker Compose로 전체 실행
```bash
# 전체 시스템 실행
docker-compose up --build

# 백그라운드 실행
docker-compose up -d --build

# 중지
docker-compose down
```

## 🔗 API 엔드포인트

### 공개 API (인증 불필요)
- `GET /app/hello` - 헬스체크
- `GET /api/status` - 시스템 상태
- `GET /user-service/health` - 사용자 서비스 헬스체크

### 보안 API (인증 필요)
- `GET /secure/hello` - 보안 헬스체크
- `GET /api/users` - 사용자 목록 (webtestK)
- `GET /api/users/{id}` - 사용자 조회 (webtestK)
- `POST /api/users` - 사용자 생성 (webtestK)

### 사용자 관리 서비스 API (인증 필요)
- `GET /user-service/api/users` - 사용자 목록
- `GET /user-service/api/users/{id}` - 사용자 조회
- `GET /user-service/api/users/email/{email}` - 이메일로 사용자 조회
- `POST /user-service/api/users` - 사용자 생성
- `PUT /user-service/api/users/{id}` - 사용자 수정
- `DELETE /user-service/api/users/{id}` - 사용자 삭제

### 인증 방법
```bash
# Authorization 헤더 추가
curl -H "Authorization: Bearer chanho123" http://localhost:8080/secure/hello

# 사용자 서비스 API 호출
curl -H "Authorization: Bearer chanho123" http://localhost:8080/user-service/api/users
```

## 🔧 주요 기능

### Gateway (gwtest)
- **로깅 필터**: 모든 요청/응답 로깅
- **인증 필터**: 토큰 기반 인증
- **Rate Limiting**: IP별 요청 제한
- **라우팅**: 경로별 요청 라우팅
- **Path Rewriting**: URL 경로 변환
- **CORS 설정**: 크로스 오리진 요청 허용

### Backend (webtestK)
- **헬스체크**: 기본 상태 확인 API
- **REST API**: 테스트용 엔드포인트
- **사용자 API**: 간단한 사용자 관리

### User Service (user-service)
- **JPA 엔티티**: 사용자 모델
- **데이터베이스**: H2 인메모리 DB
- **CRUD 작업**: 사용자 생성/조회/수정/삭제
- **검증**: 입력 데이터 검증
- **기본 데이터**: 시작 시 기본 사용자 생성

## 📊 모니터링

- Gateway 로그: `org.springframework.cloud.gateway: DEBUG`
- Web 로그: `org.springframework.web.reactive: DEBUG`
- User Service 로그: `shb.gpark.userservice: DEBUG`

## 🛠️ 개발 환경

- **Java**: 21
- **Kotlin**: 1.9.25
- **Spring Boot**: 3.5.x
- **Spring Cloud**: 2025.0.0
- **데이터베이스**: H2 (인메모리)
- **컨테이너**: Docker

## 🔒 보안 기능

### Rate Limiting
- **공개 API**: 60 requests/minute
- **보안 API**: 30 requests/minute
- **사용자 API**: 20 requests/minute
- **사용자 서비스**: 15 requests/minute

### 인증
- **토큰**: `Bearer chanho123`
- **보안 경로**: `/secure/**`, `/api/users/**`, `/user-service/**`

## 📈 향후 계획

- [x] Docker 컨테이너화
- [x] Rate Limiting 구현
- [x] 새로운 마이크로서비스 추가
- [ ] JWT 인증 구현
- [ ] PostgreSQL 연동
- [ ] 모니터링 시스템 구축 (Prometheus + Grafana)
- [ ] CI/CD 파이프라인
- [ ] 서비스 디스커버리 (Eureka)
- [ ] Circuit Breaker 패턴
- [ ] 분산 추적 (Jaeger/Zipkin)

## 🧪 테스트

### 기본 테스트
```bash
# 공개 API 테스트
curl http://localhost:8080/app/hello

# 보안 API 테스트
curl -H "Authorization: Bearer chanho123" http://localhost:8080/secure/hello

# 사용자 서비스 테스트
curl -H "Authorization: Bearer chanho123" http://localhost:8080/user-service/api/users
```

### Rate Limiting 테스트
```bash
# 빠른 연속 요청으로 Rate Limiting 테스트
for i in {1..70}; do curl http://localhost:8080/app/hello; done
```
