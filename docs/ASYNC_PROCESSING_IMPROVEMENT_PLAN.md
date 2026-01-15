# 시스템 성능 및 다중 사용자 확장성을 위한 비동기 처리 개선 계획

**문서 버전**: 1.0  
**일자**: 2025-01-XX  
**상태**: 기획중  
**관련 요구사항**: REQ-NF-001-PERF-001 (성능 요구사항)

---

## 📋 비즈니스 요약 (Executive Summary)

### 현재 상황: 왜 이 작업이 필요한가?

**문제점**:
- 사용자가 사업계획서 생성 버튼을 클릭하면 **3~15초 동안 화면이 멈춤**
- 동시에 여러 사용자가 요청하면 시스템이 느려지거나 타임아웃 발생
- 이메일 발송 등 외부 서비스 호출 시에도 사용자가 대기해야 함
- 현재 시스템은 **동시에 1~5명의 사용자만 처리 가능**

**영향**:
- 사용자 경험 저하 (긴 대기 시간)
- 시스템 확장성 제한 (다중 사용자 지원 어려움)
- 서비스 안정성 문제 (타임아웃 에러 발생)

### 해결 방안: 무엇을 하려는가?

**핵심 아이디어**: "요청을 받으면 즉시 응답하고, 실제 작업은 백그라운드에서 처리"

**주요 변경사항**:
1. **즉시 응답 패턴 도입**
   - 사용자가 요청하면 즉시 "작업이 시작되었습니다" 응답 (0.1초 이내)
   - 실제 AI 생성은 백그라운드에서 진행
   - 사용자는 진행 상황을 실시간으로 확인 가능

2. **작업 큐 시스템 구축**
   - 여러 사용자의 요청을 순서대로 처리
   - 작업 상태 추적 및 재시도 메커니즘
   - 에러 발생 시 자동 복구

3. **데이터베이스 최적화**
   - 동시 처리 능력 향상 (1개 → 5개)
   - 읽기/쓰기 작업 분리로 효율성 개선

### 기대 효과: 어떤 개선이 있는가?

**사용자 경험**:
- ✅ 사업계획서 생성 요청 시 **즉시 응답** (3~15초 → 0.1초)
- ✅ 진행 상황을 실시간으로 확인 가능
- ✅ 대기 시간 없이 다른 작업 진행 가능

**시스템 성능**:
- ✅ **동시 처리 능력 10배 증가** (1~5명 → 50명 이상)
- ✅ **분당 처리량 10배 증가** (10건 → 100건 이상)
- ✅ 타임아웃 에러 대폭 감소

**비즈니스 가치**:
- ✅ 더 많은 사용자 동시 지원 가능 (서비스 확장성)
- ✅ 사용자 이탈률 감소 (빠른 응답)
- ✅ 시스템 안정성 향상 (에러 감소)

### 적용 대상: 어떤 기능들이 개선되는가?

**즉시 적용 (높은 우선순위)**:
1. **사업계획서 AI 생성** - 가장 긴 대기 시간 (3~15초)
2. **AI 평가** - 평가 처리 시간 (2~10초)
3. **문서 내보내기** - 파일 생성 시간 (3~30초)

**단계적 적용 (중간 우선순위)**:
4. **회원가입/이메일 인증** - 이메일 발송 시간 (1~3초)
5. **비밀번호 재설정** - 이메일 발송 시간 (1~3초)
6. **재무 시뮬레이션** - 복잡한 계산 (0.5~2초)

### 일정 및 리소스: 언제까지 진행하는가?

**총 기간**: 8주 (약 2개월)

**단계별 일정**:
- **1~2주**: 기초 구축 (데이터베이스, 작업 큐 시스템)
- **3~4주**: 핵심 기능 구현 (사업계획서 생성 비동기화)
- **5~6주**: 추가 기능 및 최적화
- **7~8주**: 테스트 및 점진적 배포

**필요 리소스**:
- 백엔드 개발자 1~2명
- QA 테스트 1명 (6~7주차)
- 인프라 모니터링 설정

### 리스크 및 대응: 어떤 위험이 있는가?

**주요 리스크**:
1. **기존 사용자 영향** - 기존 API와의 호환성 유지 필요
   - **대응**: 기존 API 유지하며 신규 API 병행 운영 (1개월)

2. **작업 큐 과부하** - 동시 요청이 너무 많을 경우
   - **대응**: 큐 크기 제한 및 우선순위 시스템 도입

3. **작업 실패 처리** - AI API 오류 등
   - **대응**: 자동 재시도 메커니즘 및 에러 알림

**완화 전략**:
- 단계적 배포 (내부 → 베타 → 전체)
- Feature Flag를 통한 기능 활성화 제어
- 실시간 모니터링 및 알림 시스템

### 투자 대비 효과 (ROI)

**개발 비용**: 약 2개월 개발 기간

**기대 효과**:
- 사용자 경험 개선 → 이탈률 감소 → 매출 증가
- 시스템 확장성 향상 → 더 많은 사용자 지원 가능
- 운영 안정성 향상 → 장애 감소 → 운영 비용 절감

**예상 성과**:
- 응답 시간 **99% 개선** (15초 → 0.1초)
- 동시 처리 능력 **10배 증가**
- 타임아웃 에러 **90% 이상 감소**

---

## 개요

이 문서는 AI API 호출과 데이터베이스 작업에 비동기 처리를 도입하여 시스템 성능을 개선하고 다중 사용자 환경을 지원하는 종합적인 계획을 기술한다. 본 계획은 다음과 같은 주요 병목 문제를 해결한다.

1. **AI API 호출**: Gemini API의 동기 처리로 인한 요청 스레드 블로킹
2. **DB 작업**: 제한적인 커넥션 풀에서의 동기 JPA 저장소 호출
3. **요청-응답 패턴**: 장시간 작업이 HTTP 요청 차단

해결책은 **작업 큐 패턴**과 상태 폴링 엔드포인트 도입으로, 클라이언트가 즉각적으로 작업 수락 응답을 받고, 완료 상태를 비동기로 조회할 수 있도록 한다.

---

## 목차

1. [현행 시스템 분석](#1-현행-시스템-분석)
2. [주요 병목 요인 식별](#2-주요-병목-요인-식별)
3. [제안 아키텍처](#3-제안-아키텍처)
4. [단계별 구현 계획](#4-단계별-구현-계획)
5. [데이터베이스 스키마 변경](#5-데이터베이스-스키마-변경)
6. [API 설계](#6-api-설계)
7. [커넥션 풀 관리](#7-커넥션-풀-관리)
8. [테스트 전략](#8-테스트-전략)
9. [마이그레이션 전략](#9-마이그레이션-전략)
10. [성능 목표](#10-성능-목표)

---

## 1. 현행 시스템 분석

### 1.1 AI API 호출 지점

#### 1.1.1 Java Spring Boot 서비스
**위치**: `BusinessPlanGenerationService.java`

```java
// 현재 동기식 구현
ChatResponse chatResponse = chatModel.call(prompt);
```
**특징**:
- 동기 블로킹 함수 호출
- 평균 응답시간: 3~10초
- HTTP 요청 스레드 전체 차단
- 장시간 요청에 대한 타임아웃 미구현


### 1.2 데이터베이스 작업 지점

#### 1.2.1 저장소 작업
**위치**:
- `BusinessPlanRepository` - CRUD
- `BusinessPlanGenerationRepository` - 사용로그
- `ProjectRepository` - 프로젝트 정보
- `UserRepository` - 사용자 정보
- 기타 엔티티별 저장소

**현행 설정**:
```properties
# SQLite 커넥션 풀 (단일 쓰기 락)
spring.datasource.hikari.maximum-pool-size=1
spring.datasource.hikari.minimum-idle=1
```
**특징**:
- 모두 동기 처리
- SQLite 단일 writer lock으로 동시성 제한
- 커넥션 풀 1개로 병목 발생
- Read replica 불가

### 1.3 기존 비동기 패턴

**도입된 예**
- `ExportService.processExport()` - `CompletableFuture.runAsync()` 활용
- `EvaluationService.processEvaluation()` - `CompletableFuture.runAsync()`

**패턴 예시**
```java
CompletableFuture.runAsync(() -> processExport(exportId, request));
```

**제약**
- 작업 상태 추적 테이블 미존재
- 재시도 미구현
- 큐 관리 미구현
- 기본(무제한) 쓰레드 풀 사용

---

## 2. 주요 병목 요인 식별

### 2.1 AI API 병목

| 이슈 | 영향도 | 현재 동작 |
|------|--------|-----------|
| 동기 블로킹 호출 | 높음 | HTTP 스레드 3~15초 블로킹 |
| 타임아웃 미구현 | 중간 | 무한대기 가능 |
| 재시도 미구현 | 중간 | 1회 실패 시 전체 실패 |
| 레이트 리밋 없음 | 중간 | API 한도 고갈 위험 |
| 섹션 순차 생성 | 중간 | 전체 시간 = 섹션 합산 |

### 2.2 데이터베이스 병목

| 이슈 | 영향도 | 현재 동작 |
|------|--------|-----------|
| 커넥션 풀 1개 | 높음 | 동시 1건만 처리 |
| 동기 처리 | 높음 | 모든 DB호출이 요청 스레드 블로킹 |
| SQLite 단일 writer lock | 높음 | 쓰기 순차화 |
| 커넥션 타임아웃 없음 | 중간 | 연결 무한대기 위험 |
| 읽기/쓰기 분리 없음 | 낮음 | 모든 작업이 동일 락 사용 |

### 2.3 요청-응답 병목

| 이슈 | 영향도 | 현재 동작 |
|------|--------|-----------|
| 장기 작업 HTTP 블로킹 | 높음 | 클라이언트 3~15초 대기 |
| 진행상황 추적 없음 | 중간 | 클라이언트 가시성 부족 |
| 작업 취소 기능 없음 | 낮음 | 실행 중 작업 취소 불가 |
| 타임아웃 이슈 | 중간 | 브라우저/로드밸런서에서 타임아웃 |

---

## 3. 제안 아키텍처

### 3.1 개략도

```
┌─────────────┐
│   클라이언트 │
└──────┬──────┘
       │ 1. POST /api/v1/business-plan/generate
       │    → 즉시 응답: { jobId, status: "accepted" }
       │
       ▼
┌─────────────────────────────────────┐
│     Spring Boot Controller          │
│  - 요청 유효성 검사                  │
│  - Job 엔티티 생성                   │
│  - jobId 즉시 반환                   │
└──────┬───────────────────────────────┘
       │
       │ 2. Job DB 저장 (비동기)
       ▼
┌─────────────────────────────────────┐
│      작업 큐 (메모리, 인메모리)      │
│  - 우선순위 큐                       │
│  - 상태 추적                         │
└──────┬───────────────────────────────┘
       │
       │ 3. Worker 스레드로 처리
       ▼
┌─────────────────────────────────────┐
│   비동기 작업 처리기                │
│  - AI API 비동기 호출                │
│  - DB 작업 비동기화                  │
│  - 상태 갱신                         │
└──────┬───────────────────────────────┘
       │
       │ 4. 상태 갱신
       ▼
┌─────────────────────────────────────┐
│      데이터베이스 (SQLite)          │
│  - Job 상태 테이블                   │
│  - 결과 저장                         │
└─────────────────────────────────────┘

       │ 5. 클라이언트 상태 폴링
       │    GET /api/v1/jobs/{jobId}/status
       │
       ▼
┌─────────────────────────────────────┐
│   상태조회 엔드포인트                │
│  - 현재 Job 상태 반환                │
│  - 진행률 정보                       │
│  - 작업 완료 시 결과 반환             │
└─────────────────────────────────────┘
```

### 3.2 컴포넌트 설계 요약

#### 3.2.1 Job 엔티티
- **용도**: 비동기 작업 상태/결과 추적
- **주요 필드**:
  - `jobId`(UUID, PK)
  - `jobType`(enum: BUSINESS_PLAN_GENERATION 등)
  - `status`(enum: PENDING, PROCESSING, COMPLETED, FAILED, CANCELLED)
  - `requestData`(JSON, 요청 원본)
  - `resultData`(JSON, 결과값)
  - `errorMessage`(실패시 오류 메시지)
  - `progress`(0~100)
  - `createdAt`, `startedAt`, `completedAt`
  - `userId`, `projectId` (필터링)

#### 3.2.2 작업 큐 서비스
- **용도**: 큐/Worker 관리
- **기능**:
  - 우선순위(FIFO+Priority) 큐
  - Worker Thread Pool (확장 가능)
  - 상태 갱신
  - 실패시 재시도
  - API 호출 레이트 리밋

#### 3.2.3 비동기 작업 처리기
- **용도**: 비동기 로직 실행 담당
- **기능**:
  - AI API 비동기 호출
  - DB 비동기 작업
  - 진행상황 추적
  - 오류-재시도

#### 3.2.4 커넥션 풀 매니저
- **용도**: DB 커넥션 풀 관리 최적화
- **기능**:
  - 동적 풀 사이징
  - 타임아웃 처리
  - 읽기/쓰기 분리
  - 커넥션 상태 모니터링

---

## 4. 단계별 구현 계획

### 4.1 1단계: 기초 구축 (1~2주)

#### 4.1.1 DB 스키마
- [ ] `async_jobs` 테이블 생성
- [ ] 상태/사용자 질의용 인덱스 생성
- [ ] 마이그레이션 스크립트 작성

#### 4.1.2 엔티티/저장소
- [ ] `AsyncJob` 엔티티 생성
- [ ] `AsyncJobRepository` 생성
- [ ] 상태 폴링용 쿼리 함수 구현

#### 4.1.3 상태/유형 Enum
- [ ] JobStatus Enum 정의
- [ ] JobType Enum 정의

### 4.2 2단계: 작업 큐 서비스 (2~3주)

#### 4.2.1 작업 큐
- [ ] `JobQueueService` 생성
- [ ] 우선순위 큐/ThreadPool 구현
- [ ] 큐 등록 메소드 구현

#### 4.2.2 ThreadPool 설정
- [ ] `ThreadPoolTaskExecutor` 설정
- [ ] Pool 사이즈 튜닝
- [ ] 모니터링/로깅 추가

### 4.3 3단계: 비동기 작업 처리 (3~4주)

#### 4.3.1 사업계획서 생성 비동기화
- [ ] `BusinessPlanGenerationService` 비동기 리팩터링
- [ ] Gemini API 비동기 호출 적용
- [ ] 진행률 추적
- [ ] 오류처리/재시도

#### 4.3.2 DB 작업 비동기화
- [ ] 저장소 호출 비동기화
- [ ] 비중요 작업에 @Async 적용
- [ ] 커넥션 풀 최적화

### 4.4 4단계: API 엔드포인트 (4~5주)

#### 4.4.1 Job 등록 엔드포인트
- [ ] `POST /api/v1/business-plan/generate` 수정
- [ ] jobId 즉시 반환
- [ ] 큐에 작업 등록

#### 4.4.2 상태 폴링 엔드포인트
- [ ] `GET /api/v1/jobs/{jobId}/status` 생성
- [ ] 현황/진행률 반환
- [ ] 완료 결과 반환

#### 4.4.3 작업 취소 엔드포인트
- [ ] `DELETE /api/v1/jobs/{jobId}` 생성
- [ ] 진행 중 Job 취소/상태 갱신

### 4.5 5단계: 커넥션 풀 최적화 (5~6주)

#### 4.5.1 HikariCP 설정
- [ ] 풀 사이즈 증가(제한 내)
- [ ] 타임아웃 등 안정성 옵션 적용
- [ ] 커넥션 상태 점검 구현

#### 4.5.2 읽기/쓰기 분리
- [ ] 읽기 전용 작업 식별
- [ ] `@Transactional(readOnly = true)` 적용
- [ ] 쿼리 패턴 최적화

### 4.6 6단계: 테스트/모니터링 (6~7주)

#### 4.6.1 단위 테스트
- [ ] 작업 큐 서비스 테스트
- [ ] 비동기 작업 처리기 테스트
- [ ] 상태 엔드포인트 테스트

#### 4.6.2 통합 테스트
- [ ] E2E 흐름 테스트
- [ ] 동시 요청 테스트
- [ ] 에러 상황 테스트

#### 4.6.3 성능 테스트
- [ ] 다중 동시 요청 부하테스트
- [ ] 응답/처리 속도 측정
- [ ] 자원 사용량 모니터링

### 4.7 7단계: 마이그레이션/배포 (7~8주)

#### 4.7.1 하위호환성
- [ ] 기존 엔드포인트 유지(Deprecated)
- [ ] 비동기 모드 Feature Flag 적용
- [ ] 점진적 배포계획 수립

#### 4.7.2 문서화
- [ ] API 문서 업데이트
- [ ] 클라이언트 통합 가이드
- [ ] 배포 가이드 작성

---

## 5. 데이터베이스 스키마 변경

### 5.1 신규 테이블: `async_jobs`

```sql
CREATE TABLE async_jobs (
    id TEXT PRIMARY KEY,  -- UUID
    job_type TEXT NOT NULL,  -- BUSINESS_PLAN_GENERATION, SECTION_GENERATION 등
    status TEXT NOT NULL,  -- PENDING, PROCESSING, COMPLETED, FAILED, CANCELLED
    request_data TEXT,  -- 요청 원본(JSON)
    result_data TEXT,  -- 결과(JSON)
    error_message TEXT,  -- 에러 메시지
    progress INTEGER DEFAULT 0,  -- 0~100
    user_id TEXT,  -- 작업 등록자
    project_id TEXT,  -- 연계 프로젝트
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    retry_count INTEGER DEFAULT 0,
    max_retries INTEGER DEFAULT 3
);

-- 효율적 조회용 인덱스
CREATE INDEX idx_async_jobs_status ON async_jobs(status);
CREATE INDEX idx_async_jobs_user_id ON async_jobs(user_id);
CREATE INDEX idx_async_jobs_project_id ON async_jobs(project_id);
CREATE INDEX idx_async_jobs_created_at ON async_jobs(created_at);
CREATE INDEX idx_async_jobs_status_created ON async_jobs(status, created_at);
```

### 5.2 마이그레이션 스크립트

**파일**: `src/main/resources/db/migration/sqlite/V999__create_async_jobs_table.sql`

```sql
-- 비동기 작업 처리를 위한 async_jobs 테이블 생성
-- Version: 999
-- Date: 2025-01-XX

CREATE TABLE IF NOT EXISTS async_jobs (
    id TEXT PRIMARY KEY,
    job_type TEXT NOT NULL,
    status TEXT NOT NULL,
    request_data TEXT,
    result_data TEXT,
    error_message TEXT,
    progress INTEGER DEFAULT 0,
    user_id TEXT,
    project_id TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    retry_count INTEGER DEFAULT 0,
    max_retries INTEGER DEFAULT 3
);

CREATE INDEX IF NOT EXISTS idx_async_jobs_status ON async_jobs(status);
CREATE INDEX IF NOT EXISTS idx_async_jobs_user_id ON async_jobs(user_id);
CREATE INDEX IF NOT EXISTS idx_async_jobs_project_id ON async_jobs(project_id);
CREATE INDEX IF NOT EXISTS idx_async_jobs_created_at ON async_jobs(created_at);
CREATE INDEX IF NOT EXISTS idx_async_jobs_status_created ON async_jobs(status, created_at);
```

---

## 6. API 설계

### 6.1 Job 등록 엔드포인트

**Endpoint**: `POST /api/v1/business-plan/generate`

**Request**: (기존과 동일)

**Response** (변경 후):
```json
{
  "success": true,
  "data": {
    "jobId": "550e8400-e29b-41d4-a716-446655440000",
    "status": "accepted",
    "estimatedTime": 10,
    "statusUrl": "/api/v1/jobs/550e8400-e29b-41d4-a716-446655440000/status"
  },
  "error": null
}
```

**HTTP Status**: `202 Accepted` (기존 200에서 변경)

### 6.2 상태 폴링 엔드포인트

**Endpoint**: `GET /api/v1/jobs/{jobId}/status`

**진행중일 때**:
```json
{
  "success": true,
  "data": {
    "jobId": "550e8400-e29b-41d4-a716-446655440000",
    "status": "processing",
    "progress": 45,
    "estimatedRemainingTime": 5,
    "message": "Generating business plan sections..."
  },
  "error": null
}
```

**완료시**:
```json
{
  "success": true,
  "data": {
    "jobId": "550e8400-e29b-41d4-a716-446655440000",
    "status": "completed",
    "progress": 100,
    "result": {
      "businessPlanId": "bp-2025-12-17-550e8400",
      "projectId": "project-uuid-here",
      "generatedAt": "2025-12-17T12:35:00.000Z",
      "sections": [...],
      "metadata": {...}
    }
  },
  "error": null
}
```

**실패시**:
```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "JOB_FAILED",
    "message": "AI generation failed",
    "detail": "Gemini API timeout after 30 seconds"
  }
}
```

**HTTP 코드**:
- `200 OK`: Job 존재(상태무관)
- `404 Not Found`: Job ID 없음
- `410 Gone`: Job 만료(클린업 정책시)

### 6.3 작업 취소 엔드포인트

**Endpoint**: `DELETE /api/v1/jobs/{jobId}`

**Response**:
```json
{
  "success": true,
  "data": {
    "jobId": "550e8400-e29b-41d4-a716-446655440000",
    "status": "cancelled",
    "message": "Job cancelled successfully"
  },
  "error": null
}
```

**HTTP 코드**:
- `200 OK` : 취소 성공
- `400 Bad Request` : 이미 완료/실패로 취소 불가
- `404 Not Found` : ID 없음

### 6.4 작업 목록 엔드포인트 (선택)

**Endpoint**: `GET /api/v1/jobs?userId={userId}&status={status}&limit={limit}`

**Response**:
```json
{
  "success": true,
  "data": {
    "jobs": [
      {
        "jobId": "...",
        "jobType": "BUSINESS_PLAN_GENERATION",
        "status": "completed",
        "createdAt": "2025-12-17T12:30:00.000Z",
        "completedAt": "2025-12-17T12:35:00.000Z"
      }
    ],
    "total": 10,
    "page": 1,
    "limit": 20
  },
  "error": null
}
```

---

## 7. 커넥션 풀 관리

### 7.1 현행 제약

**SQLite 제약**:
- 단일 쓰기 락: 1회 1건만 쓰기 허용
- 파일기반 DB: 파일IO 성능 영향
- 네트워크 오버헤드 없음

**설정 현황**:
```properties
spring.datasource.hikari.maximum-pool-size=1
spring.datasource.hikari.minimum-idle=1
```

### 7.2 최적화 예시

**비동기 운영에 적합한 SQLite 설정**:
```properties
# SQLite 커넥션 풀
# 다중 읽기 지원, 쓰기는 1건
spring.datasource.hikari.maximum-pool-size=5
spring.datasource.hikari.minimum-idle=2
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000
spring.datasource.hikari.leak-detection-threshold=60000

# SQLite 성능최적화
spring.datasource.hikari.data-source-properties.journal_mode=WAL
spring.datasource.hikari.data-source-properties.synchronous=NORMAL
spring.datasource.hikari.data-source-properties.cache_size=10000
spring.datasource.hikari.data-source-properties.temp_store=MEMORY
```

**설명**:
- **maximum-pool-size=5** : 읽기 동시 5건 허용
- **WAL 모드** : 동시 읽기/쓰기 효율
- **synchronous=NORMAL** : 안전성과 성능 균형
- **cache_size** : 인메모리 캐시 증설
- **temp_store=MEMORY** : 임시 테이블 메모리화

### 7.3 읽기/쓰기 분리 전략

1. **읽기 작업**: `@Transactional(readOnly = true)`
   - 동시처리 가능, 락 필요 없음
   - 별도 커넥션 풀 활용 가능

2. **쓰기 작업**: `@Transactional`
   - SQLite 특성상 순차처리 권장
   - 트랜잭션 최소화/배치 쓰기 활용

3. **비동기 쓰기**:
   - 작업 큐에 쓰기 요청 적재
   - 백그라운드로 순차 처리
   - 상태 비동기 갱신

### 7.4 커넥션 풀 모니터링

**추적 항목**:
- 활성 커넥션 수
- 유휴 커넥션 수
- 커넥션 대기 시간
- 커넥션 타임아웃 발생률
- 커넥션 누수 감지

**구현 방안**:
- HikariCP 내장 지표 사용
- Actuator 엔드포인트로 노출
- 풀 고갈시 알림 설정

---

## 8. 테스트 전략

### 8.1 단위 테스트

#### 8.1.1 작업 큐 서비스
```java
@Test
void testJobSubmission() {
    // 작업이 큐에 등록된다
    // 작업 상태가 PENDING이다
}

@Test
void testJobProcessing() {
    // 워커가 작업을 집어간다
    // 상태가 PROCESSING으로 변경
}

@Test
void testJobCompletion() {
    // 작업이 성공적으로 완료
    // 결과값 저장 확인
}
```

#### 8.1.2 비동기 작업 처리기
```java
@Test
void testAsyncAICall() {
    // 비동기 AI API 호출 테스트
    // 진행률 갱신 확인
}

@Test
void testErrorHandling() {
    // 재시도 로직 테스트
    // 실패 상태 갱신 확인
}
```

### 8.2 통합 테스트

#### 8.2.1 E2E 흐름 테스트
```java
@Test
void testBusinessPlanGenerationAsync() {
    // 1. 작업 등록
    // 2. jobId 즉시 반환 확인
    // 3. 상태 폴링 완료까지 반복
    // 4. 최종 결과 검증
}
```

#### 8.2.2 동시 작업 테스트
```java
@Test
void testConcurrentJobs() {
    // 10건 동시 등록
    // 모두 수락/정상 종료 검증
}
```

### 8.3 성능 테스트

#### 8.3.1 부하테스트
- **시나리오**: 100건 동시 요청
- **지표**:
  - 등록 응답속도 < 100ms
  - 최초 진행상황 응답 < 1초
  - 전체 완료 < 15초
  - CPU/메모리/커넥션 활용량

#### 8.3.2 스트레스 테스트
- **시나리오**: 큐에 1000건 적재
- **지표**:
  - 큐 처리율
  - 메모리 사용량
  - 풀 사용률
  - 에러율

---

## 9. 마이그레이션 전략

### 9.1 Feature Flag 방식

**구현 예시**:
```java
@Value("${app.feature.async-job-processing:false}")
private boolean asyncJobProcessingEnabled;

@PostMapping("/generate")
public ResponseEntity<ApiResponse<?>> generateBusinessPlan(
        @RequestBody BusinessPlanGenerateRequest request) {
    
    if (asyncJobProcessingEnabled) {
        // 비동기 플로우
        return submitJobAsync(request);
    } else {
        // 기존 동기 방식
        return generateBusinessPlanSync(request);
    }
}
```

### 9.2 단계적 배포 계획

**1단계**: 내부 사용자 테스트 (1~2주)
- 내부만 적용
- 오류 및 성능 모니터링 및 개선

**2단계**: Beta 사용자 (3~4주)
- 10% 사용자로 확대
- 피드백 및 모니터링

**3단계**: 전체 전환 (5~6주)
- 전체 적용, 구버전 엔드포인트 폐기
- 1주간 집중 모니터링

**4단계**: 클린업 (7~8주)
- 동기 코드 제거, 문서 정비, 마이그레이션 코드 아카이빙

### 9.3 하위호환성

**기존 엔드포인트 유지(Deprecated)**
- 1개월 유지
- Deprecation 헤더/로그 남김
- 마이그레이션 가이드 제공

**클라이언트 마이그레이션 가이드**
1. 새 작업 등록 엔드포인트 사용
2. 상태 폴링(1~2초 주기)
3. 신규 응답 포맷에 맞게 변경
4. 오류처리 로직 개편

---

## 10. 성능 목표

### 10.1 응답시간 목표

| 작업 | 현 상태 | 목표 | 개선점 |
|------|---------|------|-------|
| 작업 등록 | N/A | < 100ms | 즉시 응답 |
| 상태 폴링 | N/A | < 50ms | 신속 상태조회 |
| 작업 완료 | 3~15s | 3~15s | (백그라운드 처리) |
| DB 작업 | 가변 | < 200ms | 풀 최적화 |

### 10.2 처리량 목표

| 지표 | 현재 | 목표 | 개선 |
|-----|------|------|-----|
| 동시 요청 | 1-5 | 50+ | 10배 증가 |
| 분당 처리량 | ~10 | 100+ | 10배 증가 |
| DB 커넥션 | 1 | 5 | 5배 증가 |

### 10.3 자원 사용 목표

| 자원 | 현 상태 | 목표 | 비고 |
|------|---------|------|-----|
| HTTP 스레드 블로킹 | 높음 | 낮음 | 대부분 비동기 처리 |
| DB 커넥션 | 1 | 3~5 | SQLite 기준 |
| 메모리 | 기본값 | +20% | 큐 메모리 오버헤드 |
| CPU | 기본값 | +10% | 워커 스레드 |

---

## 11. 상세 구현 방법

### 11.1 Java Spring Boot

#### 11.1.1 비동기 환경 설정

**파일**: `AsyncConfig.java`
```java
@Configuration
@EnableAsync
public class AsyncConfig {
    
    @Bean(name = "jobProcessorExecutor")
    public Executor jobProcessorExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("job-processor-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
    
    @Bean(name = "databaseExecutor")
    public Executor databaseExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("db-async-");
        executor.initialize();
        return executor;
    }
}
```

#### 11.1.2 작업 큐 서비스 예제

**파일**: `JobQueueService.java`
```java
@Service
@RequiredArgsConstructor
@Slf4j
public class JobQueueService {
    
    private final AsyncJobRepository jobRepository;
    private final AsyncJobProcessor jobProcessor;
    
    @Async("jobProcessorExecutor")
    public CompletableFuture<String> submitJob(AsyncJob job) {
        // DB 저장
        job.setStatus(JobStatus.PENDING);
        job = jobRepository.save(job);
        // 비동기 처리 시작
        processJobAsync(job.getId());
        return CompletableFuture.completedFuture(job.getId());
    }
    
    @Async("jobProcessorExecutor")
    private void processJobAsync(String jobId) {
        try {
            AsyncJob job = jobRepository.findById(jobId)
                    .orElseThrow(() -> new JobNotFoundException(jobId));
            
            job.setStatus(JobStatus.PROCESSING);
            job.setStartedAt(Instant.now());
            jobRepository.save(job);
            
            // 작업 처리
            Object result = jobProcessor.process(job);
            
            job.setStatus(JobStatus.COMPLETED);
            job.setResultData(objectMapper.writeValueAsString(result));
            job.setCompletedAt(Instant.now());
            job.setProgress(100);
            jobRepository.save(job);
            
        } catch (Exception e) {
            handleJobFailure(jobId, e);
        }
    }
}
```

#### 11.1.3 비동기 작업 처리기 예제

**파일**: `BusinessPlanAsyncJobProcessor.java`
```java
@Component
@RequiredArgsConstructor
@Slf4j
public class BusinessPlanAsyncJobProcessor implements AsyncJobProcessor {
    
    private final ChatModel chatModel;
    private final BusinessPlanRepository businessPlanRepository;
    
    @Override
    public Object process(AsyncJob job) {
        BusinessPlanGenerateRequest request = parseRequest(job);
        // 비동기 AI 호출 예
        return generateBusinessPlanAsync(request, job);
    }
    
    @Async("jobProcessorExecutor")
    private CompletableFuture<BusinessPlanGenerateResponse> generateBusinessPlanAsync(
            BusinessPlanGenerateRequest request, AsyncJob job) {
        
        updateProgress(job, 10, "프롬프트 생성 중...");
        Prompt prompt = buildPrompt(request);
        
        updateProgress(job, 30, "AI API 호출 중...");
        ChatResponse response = chatModel.call(prompt);  // 동기지만 api thread에서 실행
        
        updateProgress(job, 70, "응답 처리 중...");
        BusinessPlanGenerateResponse result = processResponse(response);
        
        updateProgress(job, 90, "DB 저장 중...");
        saveBusinessPlanAsync(result);
        
        updateProgress(job, 100, "완료");
        return CompletableFuture.completedFuture(result);
    }
    
    @Async("databaseExecutor")
    private void saveBusinessPlanAsync(BusinessPlanGenerateResponse response) {
        BusinessPlan entity = mapToEntity(response);
        businessPlanRepository.save(entity);
    }
}
```

### 11.2 Python FastAPI 참고 구현

#### 11.2.1 비동기 엔드포인트

**파일**: `BusinessPlanGenerationService.java`
```java
@Async
public CompletableFuture<BusinessPlanGenerateResponse> generateBusinessPlanAsync(
        BusinessPlanGenerateRequest request, String projectId) {
    // 비동기 사업계획서 생성: job_id 즉시 반환, 백그라운드 처리
    String jobId = UUID.randomUUID().toString();
    // ... 비동기 처리 로직
    return CompletableFuture.supplyAsync(() -> {
        // Gemini API 호출 및 처리
            template_type=request.template_type
        )
        await update_job_status(job_id, "completed", 100, result=sections)
    except Exception as e:
        await update_job_status(job_id, "failed", error=str(e))

@app.get("/jobs/{job_id}/status")
async def get_job_status(job_id: str):
    """작업 상태 조회"""
    job = await get_job(job_id)
    if not job:
        raise HTTPException(status_code=404, detail="Job not found")
    
    return {
        "job_id": job_id,
        "status": job.status,
        "progress": job.progress,
        "result": job.result_data if job.status == "completed" else None,
        "error": job.error_message if job.status == "failed" else None
    }
```

---

## 12. 모니터링 및 가시성

### 12.1 주요 모니터링 지표

**작업 지표**
- 분당 Job 등록/완료/실패 건수
- 평균 처리시간, 큐 길이

**시스템 지표**
- 활성 워커 스레드 수
- DB 커넥션 풀 활용
- 메모리/CPU 사용량

**API 지표**
- 등록/상태조회 응답시간
- 에러율 등

### 12.2 로깅

**구조화 로그 예**
```java
log.info("Job submitted: jobId={}, jobType={}, userId={}", jobId, jobType, userId);
log.info("Job processing started: jobId={}, elapsed={}ms", jobId, elapsed);
log.info("Job completed: jobId={}, duration={}ms, resultSize={}", jobId, duration, resultSize);
log.error("Job failed: jobId={}, error={}", jobId, error, exception);
```

### 12.3 알림

**설정 예**
- 작업 실패율 5% 초과
- 큐 길이 100건 초과
- 평균 처리 30초 초과
- 커넥션 풀/워커 풀 고갈

---

## 13. 위험요소 및 대응

### 13.1 주요 리스크

| 위험 | 발생확률 | 영향도 | 대응전략 |
|-----|---------|-------|--------|
| 작업 큐 과부하 | 중 | 높음 | 큐 상한, 초과 시 등록 거부 |
| 커넥션 고갈 | 낮음 | 높음 | 모니터링/서킷브레이커 적용 |
| 작업 실패 증가 | 중 | 중간 | 재시도/데드레터 큐 |
| 폴링 부하 | 중 | 낮음 | 차후 WebSocket 도입 검토 |
| 데이터 정합성 | 낮음 | 높음 | 트랜잭션/idem-potency 적용 |

### 13.2 대응 전략

**큐 과부하**:
- 최대 큐 1000건 제한
- 초과 시 503, retry-after 제공

**커넥션 고갈**:
- 풀 모니터링/타임아웃/헬스체크 대응

**작업 실패**:
- 지수적 backoff 재시도
- 데드레터 큐
- 실패율 알림

---

## 14. 향후 개선방안

### 14.1 WebSocket 실시간 상태
- 폴링 대신 실시간 푸시
- 서버 부하 감소

### 14.2 작업 우선순위(프리미엄 등)
- 유료/무료 작업 우선순위
- 큐 우선처리 시스템

### 14.3 분산 큐(멀티 인스턴스)
- Redis 분산 큐,
- 다중 인스턴스 확장 지원

### 14.4 작업 예약/스케줄러
- 예약 작업, 반복 작업
- Cron 기반 스케줄 지원

---

## 15. 결론

본 계획은 AI API 호출 및 데이터베이스 작업의 비동기 처리 기반 확장안을 제시하며, 주요 효과는 다음과 같다.

1. **사용자 경험 개선**: 요청 즉시 응답 제공
2. **확장성 제고**: 다중 사용자 동시 지원
3. **자원 최적화**: DB 커넥션 효율적 사용
4. **시스템 견고성**: 에러/재시도 체계 보강

순차적 도입 방식을 통해 위험은 최소화하며, 이전 버전과의 하위호환성도 유지한다.

---

## 부록 A: 참고 문서

- [REQ-NF-001-PERF-001.md](../tasks/non-functional/REQ-NF-001-PERF-001.md) - 성능 요구사항
- [API_SPECIFICATION.md](./API_SPECIFICATION.md) - API 스펙
- [303-database-sqlite-jpa-rules.mdc](../.cursor/rules/303-database-sqlite-jpa-rules.mdc) - 데이터베이스 규칙

## 부록 B: 용어 정리

- **Job**: 비동기 처리 작업 단위
- **Job Queue**: 대기중인 Job 자료구조
- **Worker Thread**: 큐에서 Job을 처리하는 스레드
- **Status Polling**: 클라이언트가 상태를 반복 조회
- **Connection Pool**: DB 접속 커넥션 풀

---

## 부록 C: 비동기 처리가 필요한 엔드포인트 분석

### C.1 분석 개요

현재 백엔드 시스템의 모든 REST API 엔드포인트를 분석하여 비동기 처리가 필요하거나 효과적인 엔드포인트를 식별했습니다. 분석 기준은 다음과 같습니다:

1. **AI API 호출**: Gemini API 등 외부 AI 서비스 호출 (3-15초 소요)
2. **외부 서비스 호출**: 이메일 발송, 외부 API 호출 등 (네트워크 지연)
3. **복잡한 계산**: 재무 시뮬레이션, 대량 데이터 처리 등 (CPU 집약적)
4. **장시간 DB 작업**: 대량 데이터 조회/저장, 복잡한 쿼리 등
5. **파일 처리**: 문서 생성, 변환, 내보내기 등 (I/O 집약적)

### C.2 비동기 처리 우선순위별 엔드포인트 목록

#### C.2.1 높은 우선순위 (즉시 적용 권장)

| 엔드포인트 | HTTP Method | 현재 상태 | 비동기 처리 필요 이유 | 예상 처리 시간 |
|-----------|------------|----------|-------------------|--------------|
| `/api/v1/business-plan/generate` | POST | 동기 처리 | Gemini API 호출 (3-15초) | 3-15초 |
| `/api/v1/evaluations` | POST | 부분 비동기 | AI 평가 처리 (2-10초) | 2-10초 |
| `/api/v1/projects/{projectId}/export` | POST | 부분 비동기 | 문서 생성/변환 (3-30초) | 3-30초 |

**상세 분석**:

1. **`POST /api/v1/business-plan/generate`** (BusinessPlanController)
   - **현재 구현**: 동기식 Gemini API 호출
   - **문제점**: HTTP 스레드가 3-15초 동안 블로킹
   - **개선 방안**: Job Queue 패턴 적용 (본 문서의 핵심 대상)
   - **예상 효과**: 즉시 응답 (202 Accepted), 백그라운드 처리

2. **`POST /api/v1/evaluations`** (EvaluationController)
   - **현재 구현**: `CompletableFuture.runAsync()` 사용 중
   - **문제점**: Job 상태 추적 테이블 없음, 재시도 메커니즘 부재
   - **개선 방안**: 통합 Job Queue 시스템으로 전환
   - **예상 효과**: 일관된 비동기 처리 패턴, 상태 추적 개선

3. **`POST /api/v1/projects/{projectId}/export`** (ExportController)
   - **현재 구현**: `CompletableFuture.runAsync()` 사용 중
   - **문제점**: Job 상태 추적 테이블 없음, 에러 처리 미흡
   - **개선 방안**: 통합 Job Queue 시스템으로 전환
   - **예상 효과**: 일관된 비동기 처리 패턴, 에러 복구 개선

#### C.2.2 중간 우선순위 (단계적 적용 권장)

| 엔드포인트 | HTTP Method | 현재 상태 | 비동기 처리 필요 이유 | 예상 처리 시간 |
|-----------|------------|----------|-------------------|--------------|
| `/api/v1/auth/signup` | POST | 동기 처리 | 이메일 발송 (SMTP 호출) | 1-3초 |
| `/api/v1/auth/verify-email/resend` | POST | 동기 처리 | 이메일 발송 (SMTP 호출) | 1-3초 |
| `/api/v1/auth/password/reset-request` | POST | 동기 처리 | 이메일 발송 (SMTP 호출) | 1-3초 |
| `/api/v1/projects/{projectId}/financial/simulate` | POST | 동기 처리 | 복잡한 재무 계산 | 0.5-2초 |

**상세 분석**:

1. **이메일 발송 관련 엔드포인트** (AuthController)
   - **엔드포인트**: 
     - `POST /api/v1/auth/signup`
     - `POST /api/v1/auth/verify-email/resend`
     - `POST /api/v1/auth/password/reset-request`
   - **현재 구현**: `EmailService.sendVerificationEmail()` 동기 호출
   - **문제점**: SMTP 서버 응답 대기로 인한 지연 (1-3초)
   - **개선 방안**: 이메일 발송을 비동기 큐로 처리
   - **예상 효과**: 회원가입/인증 응답 속도 개선 (100ms 이하)
   - **주의사항**: 이메일 발송 실패 시 재시도 메커니즘 필요

2. **`POST /api/v1/projects/{projectId}/financial/simulate`** (FinancialController)
   - **현재 구현**: 동기식 재무 계산
   - **문제점**: 복잡한 계산 시 CPU 집약적 작업으로 인한 지연
   - **개선 방안**: 계산 작업을 비동기로 처리 (선택적)
   - **예상 효과**: 대량 데이터 처리 시 응답 속도 개선
   - **참고**: 현재는 빠른 응답이 가능하나, 향후 복잡도 증가 시 고려

#### C.2.3 낮은 우선순위 (선택적 적용)

| 엔드포인트 | HTTP Method | 현재 상태 | 비동기 처리 필요 이유 | 예상 처리 시간 |
|-----------|------------|----------|-------------------|--------------|
| `/api/v1/projects` | POST | 동기 처리 | 프로젝트 생성 (DB 쓰기) | < 100ms |
| `/api/v1/projects/{projectId}` | PUT | 동기 처리 | 프로젝트 수정 (DB 쓰기) | < 100ms |
| `/api/v1/projects/{projectId}/wizard` | PUT | 동기 처리 | 위저드 데이터 저장 (DB 쓰기) | < 100ms |

**상세 분석**:

- **CRUD 작업**: 대부분 빠른 응답 (< 100ms)이 가능하여 비동기 처리의 이점이 제한적
- **개선 방안**: 대량 데이터 처리나 복잡한 비즈니스 로직이 추가될 경우에만 고려
- **참고**: 현재는 동기 처리로 충분하나, 향후 확장성 고려 시 검토

### C.3 엔드포인트별 비동기 처리 전략

#### C.3.1 AI API 호출 엔드포인트

**대상**: `POST /api/v1/business-plan/generate`

**전략**:
- Job Queue 패턴 적용 (본 문서의 핵심)
- 즉시 응답 (202 Accepted) + Job ID 반환
- 상태 폴링 엔드포인트 제공
- 진행률 추적 (progress: 0-100)

**구현 우선순위**: 최우선

#### C.3.2 이메일 발송 엔드포인트

**대상**: 
- `POST /api/v1/auth/signup`
- `POST /api/v1/auth/verify-email/resend`
- `POST /api/v1/auth/password/reset-request`

**전략**:
- 이메일 발송을 별도 비동기 큐로 처리
- 사용자 응답은 즉시 반환 (이메일 발송 완료 대기 불필요)
- 발송 실패 시 재시도 메커니즘 (최대 3회)
- 발송 실패 로그 기록 및 모니터링

**구현 우선순위**: 중간 (사용자 경험 개선)

**구현 예시**:
```java
@PostMapping("/signup")
public ResponseEntity<ApiResponse<SignupResponse>> signup(
        @Valid @RequestBody SignupRequest request) {
    
    // 사용자 생성 (동기)
    SignupResponse response = authService.signup(request);
    
    // 이메일 발송 (비동기)
    emailService.sendVerificationEmailAsync(
        response.getUser().getEmail(), 
        response.getVerificationToken(),
        response.getUser().getName()
    );
    
    return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(response));
}
```

#### C.3.3 문서 내보내기 엔드포인트

**대상**: `POST /api/v1/projects/{projectId}/export`

**전략**:
- 현재 `CompletableFuture.runAsync()` 사용 중
- 통합 Job Queue 시스템으로 전환
- 상태 추적 및 에러 처리 개선
- 진행률 추적 (문서 생성 단계별)

**구현 우선순위**: 중간 (기존 구현 개선)

#### C.3.4 AI 평가 엔드포인트

**대상**: `POST /api/v1/evaluations`

**전략**:
- 현재 `CompletableFuture.runAsync()` 사용 중
- 통합 Job Queue 시스템으로 전환
- 6개 영역 평가의 단계별 진행률 추적
- 각 영역별 부분 결과 제공

**구현 우선순위**: 중간 (기존 구현 개선)

### C.4 비동기 처리 불필요 엔드포인트

다음 엔드포인트들은 현재 동기 처리가 적절하며, 비동기 전환의 이점이 제한적입니다:

| 엔드포인트 | 이유 |
|-----------|------|
| `GET /api/v1/projects` | 빠른 조회 (< 50ms) |
| `GET /api/v1/projects/{projectId}` | 빠른 조회 (< 50ms) |
| `GET /api/v1/projects/templates` | 정적 데이터 조회 |
| `GET /api/v1/auth/verify-email` | 단순 토큰 검증 |
| `POST /api/v1/auth/login` | 빠른 인증 처리 (< 100ms) |
| `POST /api/v1/auth/refresh` | 빠른 토큰 갱신 (< 50ms) |
| `GET /api/v1/health` | 헬스 체크 (즉시 응답 필요) |

### C.5 구현 로드맵 요약

**Phase 1 (즉시 적용)**:
1. `POST /api/v1/business-plan/generate` - Job Queue 패턴 적용
2. 통합 Job Queue 시스템 구축

**Phase 2 (단계적 적용)**:
1. 이메일 발송 비동기화 (3개 엔드포인트)
2. `POST /api/v1/evaluations` - 통합 Job Queue로 전환
3. `POST /api/v1/projects/{projectId}/export` - 통합 Job Queue로 전환

**Phase 3 (선택적 적용)**:
1. `POST /api/v1/projects/{projectId}/financial/simulate` - 필요 시 비동기화
2. 기타 대량 데이터 처리 엔드포인트

### C.6 예상 성능 개선 효과

| 엔드포인트 | 현재 응답 시간 | 개선 후 응답 시간 | 개선율 |
|-----------|--------------|-----------------|--------|
| `POST /api/v1/business-plan/generate` | 3-15초 | < 100ms | **99% 개선** |
| `POST /api/v1/auth/signup` | 1-3초 | < 100ms | **95% 개선** |
| `POST /api/v1/evaluations` | 2-10초 | < 100ms | **99% 개선** |
| `POST /api/v1/projects/{projectId}/export` | 3-30초 | < 100ms | **99% 개선** |

**전체 시스템 효과**:
- **동시 처리 능력**: 1-5건 → 50+건 (10배 증가)
- **사용자 경험**: 즉시 응답으로 대기 시간 제거
- **시스템 안정성**: 타임아웃 에러 감소, 에러 복구 메커니즘 강화

---

**문서 상태**: 초안  
**다음 검토일**: 미정  
**승인자**: 미정
