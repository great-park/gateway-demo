#!/bin/bash

# API Gateway URL
GATEWAY_URL="http://localhost:8080"

echo "🚀 Gateway Demo API 테스트 시작"
echo "=================================="

# 1. 공개 API 테스트
echo ""
echo "1️⃣ 공개 API 테스트"
echo "-------------------"
echo "헬스체크:"
curl -s "$GATEWAY_URL/app/hello" | jq '.'

echo ""
echo "시스템 상태:"
curl -s "$GATEWAY_URL/api/status" | jq '.'

# 2. JWT 토큰 발급
echo ""
echo "2️⃣ JWT 토큰 발급"
echo "-------------------"
echo "관리자 로그인:"
ADMIN_TOKEN=$(curl -s -X POST "$GATEWAY_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' | jq -r '.token')

echo "사용자 로그인:"
USER_TOKEN=$(curl -s -X POST "$GATEWAY_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"user","password":"user123"}' | jq -r '.token')

echo "토큰 검증:"
curl -s -X POST "$GATEWAY_URL/auth/validate" \
  -H "Authorization: Bearer $ADMIN_TOKEN" | jq '.'

# 3. 보안 API 테스트
echo ""
echo "3️⃣ 보안 API 테스트"
echo "-------------------"
echo "보안 헬스체크 (관리자):"
curl -s -H "Authorization: Bearer $ADMIN_TOKEN" \
  "$GATEWAY_URL/secure/hello" | jq '.'

echo ""
echo "보안 헬스체크 (사용자):"
curl -s -H "Authorization: Bearer $USER_TOKEN" \
  "$GATEWAY_URL/secure/hello" | jq '.'

# 4. 사용자 API 테스트
echo ""
echo "4️⃣ 사용자 API 테스트"
echo "-------------------"
echo "사용자 목록:"
curl -s -H "Authorization: Bearer $USER_TOKEN" \
  "$GATEWAY_URL/api/users" | jq '.'

echo ""
echo "사용자 생성:"
curl -s -X POST "$GATEWAY_URL/api/users" \
  -H "Authorization: Bearer $USER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"테스트 사용자","email":"test@example.com"}' | jq '.'

# 5. 사용자 서비스 테스트
echo ""
echo "5️⃣ 사용자 서비스 테스트"
echo "----------------------"
echo "사용자 서비스 헬스체크:"
curl -s "$GATEWAY_URL/user-service/health" | jq '.'

echo ""
echo "사용자 서비스 사용자 목록:"
curl -s -H "Authorization: Bearer $USER_TOKEN" \
  "$GATEWAY_URL/user-service/api/users" | jq '.'

# 6. Rate Limiting 테스트
echo ""
echo "6️⃣ Rate Limiting 테스트"
echo "----------------------"
echo "빠른 연속 요청 (Rate Limiting 확인):"
for i in {1..5}; do
  echo "요청 $i:"
  curl -s "$GATEWAY_URL/app/hello" | jq '.message'
  sleep 0.1
done

echo ""
echo "✅ 모든 테스트 완료!"
echo "==================================" 