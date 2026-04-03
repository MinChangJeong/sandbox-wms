# WMS API 문서 및 데이터베이스 접근 가이드

## 1. API 문서 접근 방법

### Swagger UI
- **URL**: `http://localhost:8081/swagger-ui.html`
- **기능**: 모든 REST API의 상호작용 가능한 문서
- **설명**: OpenAPI/Swagger 자동 생성 문서

### OpenAPI JSON
- **URL**: `http://localhost:8081/api-docs`
- **기능**: API 명세를 JSON 형식으로 제공
- **용도**: 외부 도구 (Postman, Insomnia 등)에서 import 가능

### Redoc
- **URL**: `http://localhost:8081/swagger-ui/redoc.html` (설정 필요)
- **기능**: 최신식 API 문서 뷰어
- **장점**: 더 나은 가독성과 네비게이션

## 2. H2 데이터베이스 접근

### H2 Web Console
- **URL**: `http://localhost:8081/h2-console`
- **JDBC URL**: `jdbc:h2:mem:testdb`
- **Username**: `sa`
- **Password**: (비워둠)

### 데이터베이스 스키마 생성 확인

```sql
-- H2 콘솔에서 실행하여 테이블 목록 확인
SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA='PUBLIC';

-- 각 테이블 구조 확인
SHOW COLUMNS FROM WAREHOUSES;
SHOW COLUMNS FROM ZONES;
SHOW COLUMNS FROM LOCATIONS;
SHOW COLUMNS FROM ITEMS;
```

## 3. REST API 엔드포인트

### 창고(Warehouse) API
```
POST   /api/v1/warehouses                  - 창고 생성
GET    /api/v1/warehouses                  - 모든 창고 조회
GET    /api/v1/warehouses/{id}             - 창고 상세 조회
GET    /api/v1/warehouses/code/{code}      - 코드로 창고 조회
PUT    /api/v1/warehouses/{id}             - 창고 정보 수정
PATCH  /api/v1/warehouses/{id}/activate    - 창고 활성화
PATCH  /api/v1/warehouses/{id}/deactivate  - 창고 비활성화
```

### 구역(Zone) API
```
POST   /api/v1/zones                       - 구역 생성
GET    /api/v1/zones/{id}                  - 구역 상세 조회
GET    /api/v1/zones/warehouse/{warehouseId} - 창고별 구역 조회
PUT    /api/v1/zones/{id}                  - 구역 정보 수정
```

### 로케이션(Location) API
```
POST   /api/v1/locations                   - 로케이션 생성
GET    /api/v1/locations/{id}              - 로케이션 상세 조회
GET    /api/v1/locations/code/{code}       - 코드로 로케이션 조회
GET    /api/v1/locations/zone/{zoneId}     - 구역별 로케이션 조회
PUT    /api/v1/locations/{id}              - 로케이션 용량 수정
```

### 품목(Item) API
```
POST   /api/v1/items                       - 품목 생성
GET    /api/v1/items                       - 품목 목록 조회 (페이징)
GET    /api/v1/items/{id}                  - 품목 상세 조회
GET    /api/v1/items/code/{code}           - 코드로 품목 조회
GET    /api/v1/items/barcode/{barcode}     - 바코드로 품목 조회
PUT    /api/v1/items/{id}                  - 품목 정보 수정
```

## 4. 요청/응답 예제

### 창고 생성 요청
```bash
curl -X POST http://localhost:8081/api/v1/warehouses \
  -H "Content-Type: application/json" \
  -H "X-User-ID: admin" \
  -d '{
    "warehouseCode": "WH001",
    "warehouseName": "서울 창고",
    "address": "서울시 강남구",
    "warehouseType": "GENERAL"
  }'
```

### 응답 예제
```json
{
  "statusCode": 201,
  "message": "창고가 생성되었습니다",
  "data": 1,
  "timestamp": "2026-04-03T12:53:00.000Z"
}
```

## 5. 로깅

### 로그 파일 위치
- **File**: `logs/wms.log`
- **Console**: 실시간 출력
- **Format**: `[timestamp] [level] [logger] [requestId] [userId] - message`

### 로그 레벨 설정 (application.yml)
```yaml
logging:
  level:
    com.wms: DEBUG              # 애플리케이션 로그
    org.hibernate.SQL: DEBUG    # SQL 쿼리 로그
```

## 6. 데이터베이스 스키마 정보

### Warehouse 테이블
```sql
CREATE TABLE warehouses (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  warehouse_code VARCHAR(10) UNIQUE NOT NULL,
  warehouse_name VARCHAR(100) NOT NULL,
  address VARCHAR(255) NOT NULL,
  warehouse_type VARCHAR(50) NOT NULL,
  is_active BOOLEAN NOT NULL,
  version BIGINT,
  created_at TIMESTAMP NOT NULL,
  created_by VARCHAR(255) NOT NULL,
  updated_at TIMESTAMP NOT NULL,
  updated_by VARCHAR(255) NOT NULL,
  is_deleted BOOLEAN NOT NULL
);
```

### Zone 테이블
```sql
CREATE TABLE zones (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  warehouse_id BIGINT NOT NULL,
  zone_code VARCHAR(20) NOT NULL,
  zone_name VARCHAR(100) NOT NULL,
  zone_type VARCHAR(50) NOT NULL,
  temperature_min INT,
  temperature_max INT,
  is_active BOOLEAN NOT NULL,
  version BIGINT,
  created_at TIMESTAMP NOT NULL,
  created_by VARCHAR(255) NOT NULL,
  updated_at TIMESTAMP NOT NULL,
  updated_by VARCHAR(255) NOT NULL,
  is_deleted BOOLEAN NOT NULL
);
```

### Location 테이블
```sql
CREATE TABLE locations (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  zone_id BIGINT NOT NULL,
  location_code VARCHAR(50) UNIQUE NOT NULL,
  row_num INT NOT NULL,
  column_num INT NOT NULL,
  level INT NOT NULL,
  location_type VARCHAR(50) NOT NULL,
  max_weight DOUBLE,
  max_volume DOUBLE,
  status VARCHAR(50) NOT NULL,
  is_active BOOLEAN NOT NULL,
  version BIGINT,
  created_at TIMESTAMP NOT NULL,
  created_by VARCHAR(255) NOT NULL,
  updated_at TIMESTAMP NOT NULL,
  updated_by VARCHAR(255) NOT NULL,
  is_deleted BOOLEAN NOT NULL
);
```

### Item 테이블
```sql
CREATE TABLE items (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  item_code VARCHAR(50) UNIQUE NOT NULL,
  item_name VARCHAR(200) NOT NULL,
  barcode VARCHAR(50) UNIQUE,
  category VARCHAR(50) NOT NULL,
  unit VARCHAR(50) NOT NULL,
  storage_type VARCHAR(50) NOT NULL,
  expiry_managed BOOLEAN NOT NULL,
  lot_managed BOOLEAN NOT NULL,
  is_active BOOLEAN NOT NULL,
  version BIGINT,
  created_at TIMESTAMP NOT NULL,
  created_by VARCHAR(255) NOT NULL,
  updated_at TIMESTAMP NOT NULL,
  updated_by VARCHAR(255) NOT NULL,
  is_deleted BOOLEAN NOT NULL
);
```

## 7. 헤더 설정

모든 요청에 다음 헤더를 포함하는 것을 권장합니다:

```
X-User-ID: admin                          # 사용자 ID (감시 추적용)
X-Correlation-ID: unique-request-id       # 요청 추적 ID (선택사항)
Content-Type: application/json            # JSON 요청인 경우 필수
```

## 8. 상태 코드 종류

- **201 Created**: 리소스 생성 성공
- **200 OK**: 요청 성공
- **400 Bad Request**: 잘못된 요청
- **409 Conflict**: 중복 코드 등 충돌
- **500 Internal Server Error**: 서버 오류

## 9. 현재 설정

- **Port**: 8081
- **Database**: H2 (in-memory)
- **JPA DDL**: create (매번 테이블 재생성)
- **SQL Logging**: Enabled (DEBUG 레벨)

## 10. 문제 해결

### 스키마가 생성되지 않는 경우
1. `application.yml`에서 `ddl-auto: create` 확인
2. 로그에서 Hibernate 초기화 메시지 확인
3. H2 콘솔에서 직접 조회

### API 응답이 없는 경우
1. Swagger UI에서 API 경로 확인
2. 올바른 헤더(X-User-ID) 전달 확인
3. 요청 본문 형식 확인 (JSON)

### 데이터베이스 연결 오류
1. `jdbc:h2:mem:testdb` URL 확인
2. 사용자명 `sa`, 비밀번호 비움 확인
3. H2 콘솔 활성화 확인 (`/h2-console`)
