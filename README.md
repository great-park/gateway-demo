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

## 📁 프로젝트 구조

- **gwtest**: Spring Cloud Gateway (API Gateway)
- **webtestK**: 백엔드 웹 서비스 (테스트용)
- **user-service**: 사용자 관리 서비스 (JPA + H2 DB)
- **product-service**: 상품 관리 서비스 (JPA + H2 DB + Swagger)

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

#### 3. 상품 서비스 실행
```bash
cd product-service
./gradlew bootRun
```

#### 4. API Gateway 실행
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
- `GET /product-service/health` - 상품 서비스 헬스체크

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

### 상품 관리 서비스 API (인증 필요)
- `GET /product-service/api/products` - 상품 목록
- `GET /product-service/api/products/{id}` - 상품 상세 조회
- `POST /product-service/api/products` - 상품 생성
- `PUT /product-service/api/products/{id}` - 상품 수정
- `DELETE /product-service/api/products/{id}` - 상품 삭제
- `PUT /product-service/api/products/{id}/stock` - 재고 수정
- `GET /product-service/api/products/search` - 상품 검색
- `GET /product-service/api/products/category/{category}` - 카테고리별 상품
- `GET /product-service/api/products/search/name` - 상품명으로 검색
- `GET /product-service/api/products/price-range` - 가격 범위로 검색
- `GET /product-service/api/products/low-stock` - 재고 부족 상품
- `GET /product-service/api/products/active` - 활성 상품

### Swagger UI (문서화)
- `http://localhost:8083/swagger-ui.html` - 상품 서비스 API 문서

### 인증 방법
```bash
# Authorization 헤더 추가
curl -H "Authorization: Bearer chanho123" http://localhost:8080/secure/hello

# 사용자 서비스 API 호출
curl -H "Authorization: Bearer chanho123" http://localhost:8080/user-service/api/users

# 상품 서비스 API 호출
curl -H "Authorization: Bearer chanho123" http://localhost:8080/product-service/api/products
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

### Product Service (product-service) ✨ NEW!
- **JPA 엔티티**: 상품 모델 (가격, 재고, 카테고리 등)
- **데이터베이스**: H2 인메모리 DB
- **CRUD 작업**: 상품 생성/조회/수정/삭제
- **고급 검색**: 이름, 카테고리, 가격 범위 검색
- **재고 관리**: 재고 수정 및 부족 상품 조회
- **Swagger/OpenAPI**: 자동 API 문서화
- **Actuator**: 헬스체크 및 메트릭스
- **기본 데이터**: 시작 시 샘플 상품 데이터 생성

## 📊 모니터링

- Gateway 로그: `org.springframework.cloud.gateway: DEBUG`
- Web 로그: `org.springframework.web.reactive: DEBUG`
- User Service 로그: `shb.gpark.userservice: DEBUG`
- Product Service 로그: `shb.gpark.productservice: DEBUG`

## 🛠️ 개발 환경

- **Java**: 21
- **Kotlin**: 1.9.25
- **Spring Boot**: 3.5.x
- **Spring Cloud**: 2025.0.0
- **데이터베이스**: H2 (인메모리)
- **컨테이너**: Docker
- **API 문서화**: Swagger/OpenAPI 3.0

## 🔒 보안 기능

### Rate Limiting
- **공개 API**: 60 requests/minute
- **보안 API**: 30 requests/minute
- **사용자 API**: 20 requests/minute
- **사용자 서비스**: 15 requests/minute
- **상품 서비스**: 25 requests/minute ✨ NEW!

### 인증
- **토큰**: `Bearer chanho123`
- **보안 경로**: `/secure/**`, `/api/users/**`, `/user-service/**`, `/product-service/**`

## 📈 향후 계획

- [x] Docker 컨테이너화
- [x] Rate Limiting 구현
- [x] 새로운 마이크로서비스 추가
- [x] Product Service 구현 ✨ NEW!
- [x] Swagger/OpenAPI 문서화 ✨ NEW!
- [x] Actuator 모니터링 ✨ NEW!
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

# 상품 서비스 테스트 ✨ NEW!
curl -H "Authorization: Bearer chanho123" http://localhost:8080/product-service/api/products
```

### Rate Limiting 테스트
```bash
# 빠른 연속 요청으로 Rate Limiting 테스트
for i in {1..70}; do curl http://localhost:8080/app/hello; done
```

### 상품 서비스 고급 테스트 ✨ NEW!
```bash
# 상품 생성
curl -X POST -H "Authorization: Bearer chanho123" \
  -H "Content-Type: application/json" \
  -d '{"name":"테스트 상품","description":"테스트 설명","price":10000,"stock":50,"category":"테스트"}' \
  http://localhost:8080/product-service/api/products

# 상품 검색
curl -H "Authorization: Bearer chanho123" \
  "http://localhost:8080/product-service/api/products/search?category=스마트폰&minPrice=1000000"

# 재고 부족 상품 조회
curl -H "Authorization: Bearer chanho123" \
  "http://localhost:8080/product-service/api/products/low-stock?threshold=10"
```

## 📚 API 문서

### Swagger UI
- **상품 서비스**: http://localhost:8083/swagger-ui.html
- **API 문서**: http://localhost:8083/api-docs

### Actuator 엔드포인트
- **헬스체크**: http://localhost:8083/actuator/health
- **메트릭스**: http://localhost:8083/actuator/metrics
- **프로메테우스**: http://localhost:8083/actuator/prometheus

## 🎯 주요 개선사항 (v2.0)

### ✨ 새로운 기능
1. **Product Service 추가**
   - 완전한 CRUD 기능
   - 고급 검색 및 필터링
   - 재고 관리 시스템
   - 카테고리별 분류

2. **API 문서화**
   - Swagger/OpenAPI 3.0 통합
   - 자동 API 문서 생성
   - 인터랙티브 API 테스트

3. **모니터링 강화**
   - Actuator 엔드포인트 추가
   - 헬스체크 및 메트릭스
   - 프로메테우스 지원

4. **개발자 경험 개선**
   - 포괄적인 .gitignore
   - 자동 기본 데이터 생성
   - 상세한 로깅 설정

### 🔧 기술적 개선
- **검증 강화**: Bean Validation 적용
- **에러 핸들링**: 통합된 응답 형식
- **성능 최적화**: 읽기 전용 트랜잭션
- **코드 품질**: Kotlin 최신 기능 활용
