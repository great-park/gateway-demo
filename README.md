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
                       ┌─────────────────┐    ┌─────────────────┐
                       │   User Service  │    │  Product Service│
                       │   (user-service)│    │ (product-service)│
                       │   Port: 8082    │    │   Port: 8083    │
                       └─────────────────┘    └─────────────────┘
```

## 📁 서비스 구성

| 서비스 | 포트 | 설명 | 주요 기능 |
|--------|------|------|-----------|
| **gwtest** | 8080 | API Gateway | 라우팅, 인증, Rate Limiting |
| **webtestK** | 8081 | 백엔드 서비스 | 기본 REST API |
| **user-service** | 8082 | 사용자 관리 | JWT 인증, 사용자 CRUD |
| **product-service** | 8083 | 상품 관리 | 상품 CRUD, 검색, 재고 관리 |

## 🚀 실행 방법

### Docker Compose (권장)
```bash
# 전체 시스템 실행
docker-compose up --build

# 백그라운드 실행
docker-compose up -d --build

# 중지
docker-compose down
```

### 개별 실행
```bash
# 각 서비스별 실행
cd gwtest && ./gradlew bootRun      # API Gateway
cd webtestK && ./gradlew bootRun    # Backend Service  
cd user-service && ./gradlew bootRun # User Service
cd product-service && ./gradlew bootRun # Product Service
```

## 🔗 API 엔드포인트

### 공개 API (인증 불필요)
```http
GET  /app/hello                    # 헬스체크
GET  /api/status                   # 시스템 상태
GET  /user-service/actuator/health # 사용자 서비스 헬스체크
GET  /product-service/actuator/health # 상품 서비스 헬스체크
```

### 사용자 관리 API (JWT 인증 필요)
```http
POST   /user-service/api/auth/register    # 회원가입
POST   /user-service/api/auth/login       # 로그인
POST   /user-service/api/auth/validate    # 토큰 검증
GET    /user-service/api/users            # 사용자 목록
GET    /user-service/api/users/{id}       # 사용자 조회
POST   /user-service/api/users            # 사용자 생성
PUT    /user-service/api/users/{id}       # 사용자 수정
DELETE /user-service/api/users/{id}       # 사용자 삭제
```

### 상품 관리 API (인증 필요)
```http
GET    /product-service/api/products                    # 상품 목록
GET    /product-service/api/products/{id}               # 상품 상세 조회
POST   /product-service/api/products                    # 상품 생성
PUT    /product-service/api/products/{id}               # 상품 수정
DELETE /product-service/api/products/{id}               # 상품 삭제
PUT    /product-service/api/products/{id}/stock         # 재고 수정
GET    /product-service/api/products/search             # 상품 검색
GET    /product-service/api/products/category/{category} # 카테고리별 상품
GET    /product-service/api/products/low-stock          # 재고 부족 상품
GET    /product-service/api/products/active             # 활성 상품
```

### 백엔드 API (인증 필요)
```http
GET  /api/users        # 사용자 목록
GET  /api/users/{id}   # 사용자 조회
POST /api/users        # 사용자 생성
```

### API 문서화
- **Swagger UI**: `http://localhost:8083/swagger-ui.html` (Product Service)

## 🔐 인증

### JWT 토큰 (User Service)
```bash
# 회원가입
curl -X POST http://localhost:8082/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"테스트","email":"test@example.com","password":"password123"}'

# 로그인
curl -X POST http://localhost:8082/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'
```

### Gateway 인증 (기존)
```bash
# Authorization 헤더 사용
curl -H "Authorization: Bearer chanho123" http://localhost:8080/secure/hello
```

## 🛠️ 주요 기능

### API Gateway (gwtest)
- **라우팅**: 경로별 요청 라우팅
- **인증**: 토큰 기반 인증
- **Rate Limiting**: IP별 요청 제한
- **로깅**: 모든 요청/응답 로깅
- **CORS**: 크로스 오리진 요청 허용

### User Service (user-service)
- **JWT 인증**: 회원가입, 로그인, 토큰 검증
- **사용자 관리**: CRUD 작업
- **비밀번호 암호화**: BCrypt 사용
- **데이터베이스**: H2 인메모리 DB

### Product Service (product-service)
- **상품 관리**: CRUD 작업
- **고급 검색**: 이름, 카테고리, 가격 범위 검색
- **재고 관리**: 재고 수정 및 부족 상품 조회
- **API 문서화**: Swagger/OpenAPI
- **모니터링**: Actuator 헬스체크

### Backend Service (webtestK)
- **기본 API**: 테스트용 엔드포인트
- **사용자 API**: 간단한 사용자 관리

## 📊 Rate Limiting

| 경로 | 제한 | 설명 |
|------|------|------|
| `/app/**` | 60 req/min | 공개 API |
| `/secure/**` | 30 req/min | 보안 API |
| `/user-service/**` | 15 req/min | 사용자 서비스 |
| `/product-service/**` | 25 req/min | 상품 서비스 |

## 🧪 테스트

```bash
# 각 서비스별 테스트
cd user-service && ./gradlew test
cd product-service && ./gradlew test
cd gwtest && ./gradlew test
cd webtestK && ./gradlew test
```

## 🛠️ 개발 환경

- **Java**: 21
- **Kotlin**: 1.9.25
- **Spring Boot**: 3.5.x
- **Spring Cloud**: 2025.0.0
- **데이터베이스**: H2 (인메모리)
- **컨테이너**: Docker
- **API 문서화**: Swagger/OpenAPI 3.0

## 📈 향후 계획

- [x] Docker 컨테이너화
- [x] Rate Limiting 구현
- [x] User Service JWT 인증
- [x] Product Service 구현
- [x] Swagger/OpenAPI 문서화
- [ ] PostgreSQL 연동
- [ ] 모니터링 시스템 (Prometheus + Grafana)
- [ ] CI/CD 파이프라인
- [ ] 서비스 디스커버리 (Eureka)
- [ ] Circuit Breaker 패턴
- [ ] 분산 추적 (Jaeger/Zipkin)
