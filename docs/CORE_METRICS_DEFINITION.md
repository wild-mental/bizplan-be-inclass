# 메이커스라운드 핵심 지표 정의서 (NSM & AARRR & 로깅 설계 초안)

> 기준 문서: `GROWTH_HACKING_REPORT.md` + GA4 체크리스트 + `LogAnalyticsService`/`PublicAnalyticsController` + `BusinessPlanGenerationService`/`BusinessPlan` 저장 로직

---

## Table 1. 북극성 지표(NSM) 구조

> NSM v1(장기)와 NSM v2(단기)를 모두 포함. 측정 주기는 MVP 기준 제안이며, 실제 운영에서 조정 가능.

| 구분 | 지표명 | 정의(Definition) | 측정 주기 | 목표치(초기) |
|---|---|---|---|---|
| **NSM v1 (장기)** | **제출 가능 산출물 완성 프로젝트 수** | 기간 내 상태가 `ready_to_submit` 이상인 프로젝트 수. (조건: ① 사업계획서 초안 생성 1회 이상, ② 최소 1회 이상 품질 점검/심사 수행, ③ HWP/PDF/DOCX 중 1회 이상 내보내기 시도 또는 내부 상태 플래그 `ready_to_submit=true`로 저장된 프로젝트) | 주간 / 월간 | 런칭 3개월 내 월 100건, 6개월 내 월 300건 |
| Input 1 (v1) | 첫 아하 모먼트 도달 세션 수 | GA4 이벤트 기준, 세션 단위로 `writing_demo_complete` 또는 `evaluation_result_view` 이벤트가 1회 이상 발생한 세션 수. (무료 데모/평가를 끝까지 수행한 세션) | 일간 / 주간 | 전체 방문 세션 대비 25% 이상 |
| Input 2 (v1) | 수정 루프 반복 횟수 | 프로젝트 단위로 `wizard_step_complete`·`evaluation_result_view`·`ai_feedback_apply`(추가 예정) 등 “피드백 후 수정/재시도” 성격의 이벤트가 발생한 총 횟수. (한 프로젝트 안에서 반복 루프가 많이 발생할수록 품질 향상 의도가 높음) | 주간 | 제출 가능 프로젝트 1건당 평균 3회 이상 |
| Input 3 (v1) | 내보내기 시도 수 | `export_document` GA4 이벤트 + 백엔드 Export API(`/api/v1/projects/{id}/export`) 호출 성공 횟수 합. (형식: pdf/hwp/docx/markdown) | 일간 / 주간 | 제출 가능 프로젝트 1건당 평균 1.2회 이상 |
| **NSM v2 (단기)** | **주간 무료 데모 완료 수** | GA4 이벤트 `writing_demo_complete`(사업계획서 작성 데모 완료)와 `evaluation_result_view`(AI 평가 결과 화면 진입)를 합산한 주간 고유 세션 수. (동일 세션 내 두 이벤트가 모두 있을 경우 1로 계산) | 주간 | 런칭 4주 내 주 100세션, 8주 내 주 300세션 |
| Input 1 (v2) | 주간 고객 획득 실험 실행 수 | 내부 관리용 지표. 주간 기준으로 “신규 유입 채널/크리에이티브/메시지”에 대한 실험 단위 수. 예: 광고 크리에이티브 A/B 집행(2), 커뮤니티 글 5개 발행(5), 파트너 뉴스레터 2건 발송(2) → 총 9회. | 주간 | 최소 주 5회 이상 실험 실행 |
| Input 2 (v2) | 주간 퍼널 개선 실험 실행 수 | 랜딩/데모/결과 화면에서의 UI·카피·플로우 변경 실험 수. 예: 히어로 CTA 카피 A/B, 데모 첫 질문 수 축소, 결과 화면 CTA 위치 조정 등 GA4에서 별도 `experiment_id` 파라미터로 태깅. | 주간 | 최소 주 3회 이상 실험 실행 |
| Input 3 (v2) | 주간 세일즈/제휴 아웃리치 시도 수 | B2B/GTM 측면에서 창업센터·대학·액셀러레이터·소상공인 단체 대상 콜드 메일/DM/콜/미팅 요청 수. CRM/시트/Notion 등 내부 시스템에 “outbound_touch”로 기록. | 주간 | 초기 8주간 주 20건 이상 접촉 |

---

## Table 2. AARRR 단계별 핵심 행동 (Key Actions)

> 각 단계별로 “비즈니스 액션(우리가 하는 일)”과 “사용자 핵심 행동(유저가 실제로 하는 일)”을 명확히 분리했습니다.  
> Metric은 GA4 이벤트명·백엔드 로그/엔티티 기준으로 기술합니다.

| 단계 | 비즈니스 액션 (Business Action) | 사용자 핵심 행동 (User Action) | 측정 기준 (Metric) | 비고 |
|---|---|---|---|---|
| **Acquisition** | 검색·커뮤니티·제휴 채널에서 “10분·무료·AI 심사위원단” 메시지로 유입 유도 | 검색/링크/공유를 통해 랜딩 페이지(`"/"` 경로) 최초 진입 | GA4 `page_view` (path = "/") 세션 수 / 일·주간 트래픽 | LogAnalytics의 `landingPageViews`와도 교차 검증 가능 |
| Acquisition | 랜딩 내 CTA 배치 및 메시지 최적화 | 랜딩에서 “무료로 심사받기(평가 데모)” 또는 “지금 바로 작성하기(작성 데모)” 버튼 클릭 | GA4 `landing_cta_click` (params: { cta_type: "evaluation" \| "writing" }) 이벤트 수 | GA4 체크리스트에 따라 추가 구현 필요 |
| **Activation** | 무료 작성 데모/평가 데모 플로우를 최소 입력으로 구성 | `/writing-demo` 또는 `/evaluation-demo` 진입 후, 모든 필수 질문 입력을 마치고 “AI 생성/평가 시작” 버튼 클릭 | GA4 `evaluation_start`, `writing_demo_start` 이벤트 수 / `page_view` 대비 비율 | GA4 다음 단계 이벤트로 설계·구현 필요 |
| Activation | “10분 내 초안/평가 완료”를 보장하는 백엔드 성능 확보 | AI 생성/평가가 완료되어 결과 화면(초안/평가 리포트)이 렌더링됨 | GA4 `writing_demo_complete`, `evaluation_result_view` 이벤트 세션 단위 발생 여부 | NSM v2의 직접 구성 요소 |
| **Retention** | 마감 D-Day·점수 기준으로 수정 루프를 유도하는 UX(알림/배지/가이드) | 기존 프로젝트에서 2회 이상 “AI 평가/체크리스트/재작성” 버튼을 눌러 재평가를 실행 | ① GA4 `wizard_step_complete`/`evaluation_result_view`/`ai_feedback_apply`(추가) 이벤트를 프로젝트별 count ② BusinessPlan/평가 관련 엔티티에서 동일 projectId의 재생성 횟수 | “프로젝트당 수정 루프 횟수”로 집계 |
| Retention | 접수 마감 일정에 맞춘 리마인더 이메일/배너 발송 | 마감 리마인더(이메일/인앱 배너) 이후 48시간 내 프로젝트 열람 또는 데모 재실행 | GA4 `project_open_after_reminder` 이벤트 / 리마인더 발송 수, 백엔드 메일 발송 로그와 조합 | 이메일 발송 시스템과의 통합 후 측정 |
| **Revenue** | 사전등록/요금제 페이지에서 플러스/프로/프리미엄 플랜 장점 강조 | `/signup?plan=plus/pro/premium`에서 가입 완료 후 사전등록 완료 | GA4 `preregistration_complete` (params: { plan_name, discount_rate }) 이벤트 수 | 이미 GA4 체크리스트에 구현됨 |
| Revenue | “점수 리포트/재작성 루프/전문가 컨설팅” 등 고가치 기능을 유료 플랜에 배치 | 무료 사용 중 특정 기능 사용 시 결제 또는 코드 입력 플로우로 진입 | GA4 `upgrade_intent` (params: { source_feature: "score_report" 등 }) → 결제·코드 입력 완료 이벤트 대비 전환율 | 결제 시스템 연동 시 확장 예정 |
| **Referral** | 결과 카드/점수 요약을 공유 가능한 포맷으로 제공 (이미지/링크) | 평가 결과/점수 카드 화면에서 “공유하기(링크/이미지)” 버튼 클릭 | GA4 `share_result` (params: { channel: "kakao" \| "link" \| "pdf" }) 이벤트 수 | 초기에는 클릭 수 기반, 추후 UTM 기반 신규 유입과 연결 |
| Referral | 합격/투자유치 성공 사례를 UGC로 수집/노출 | 합격 후 설문/피드백 폼에서 “성공 스토리 제출” 및 “실명/익명 공개 동의” 선택 | GA4 `success_story_submit` 이벤트 + 백엔드 설문 응답 저장 수 | 랜딩의 “고객 시나리오” 섹션에 재활용 가능 |

---

## Table 3. 데이터 로깅이 필요한 후보 리스트 (Draft)

> 현재 **이미 측정 중인 로그**와 **추가 필요한 로그**를 구분해 나열합니다.  
> - GA4: `src/utils/analytics.ts` + `GA4_TEST_CHECKLIST.md` 기준  
> - 백엔드 로그: `LogAnalyticsService`(api-requests 로그), `BusinessPlanGenerationService`/`BusinessPlan` 엔티티, `gemini-usage.log`

### 3-1. 이미 측정되고 있는 로그 (현 시점)

| 화면명 / API | 사용자 행동 | 예상되는 데이터 (이미 로깅됨) |
|---|---|---|
| 전체 앱 (라우팅) | 페이지 이동 (`/`, `/signup`, `/login`, `/writing-demo`, `/evaluation-demo`, `/wizard/:step`, `/business-plan` 등) | GA4 `page_view` (path, title) — `initializeGA` + RouteTracker 기준, DebugView/체크리스트에 명시 |
| 회원가입 (`/signup`) | 이메일/비밀번호 입력 후 “가입 후 무료 데모 체험하기” 클릭 | GA4 `signup_complete` (params: { plan_name, method }) |
| 사전등록 (플랜별 `/signup?plan=plus/pro`) | 가입 완료 후 사전등록 완료 | GA4 `preregistration_complete` (params: { plan_name, discount_rate }) |
| 마법사 (`/wizard/:step`) | 모든 단계 입력 후 “AI 사업계획서 생성” 버튼 클릭 | GA4 `wizard_complete` (params: { template_type, completion_rate }) |
| 평가 데모 (`/evaluation-demo`) | 아이디어 입력 후 “AI 평가 시작” → 결과 화면 진입 | GA4 `evaluation_result_view` (params: { total_score, grade, pass_rate }) |
| 사업계획서 뷰어 (`/business-plan`) | “PDF/HWP/DOCX” 버튼 클릭 | GA4 `export_document` (params: { format, template_type }) |
| 전체 페이지/공통 | GA4 클라이언트 ID/유저 속성 설정 | GA4 `setUserId`, `setUserProperties` (user_id, user_type, subscription_plan 등) |
| 백엔드 Gemini 호출 | 사업계획서 생성 요청 시 Gemini API 호출 및 토큰/시간 로깅 | `logs/gemini-usage.log`에 `[Gemini Usage Log]` 라인 (StartTime, EndTime, Duration, Input/Output/Total tokens, Throughput) + DB `business_plans` 테이블의 `gemini_metadata_json` 필드 |
| 백엔드 API 전체 | API 호출 시 `api-requests.log`에 요청 정보 로깅 | `LogParser.ParsedLogEntry` 기반: timestamp, method, apiPath, referer, requestId 등 |
| 백엔드 Public Analytics API | `/api/v1/public/analytics/*` 호출 | `PublicAnalyticsController`에서 `log.info`로 조회 로그 기록 (targetDate, 기간) |

### 3-2. 추가로 로그 기록이 필요한 후보 지점 (NSM/AARRR 연동용)

아래 항목들은 **NSM·AARRR에 직접 연결되는 이벤트**이지만, 현재는 GA4 체크리스트상 “미구현/후속 작업”으로 명시되어 있거나, 백엔드에 명시적 구조가 없는 부분입니다.

| 화면명 | 사용자 행동 | 예상되는 데이터 (추가 로깅 설계) |
|---|---|---|
| 랜딩 히어로 섹션 (`LandingPage` HeroSection) | “지금 바로 작성하기” 버튼 클릭 | GA4 `landing_cta_click` (params: { cta_type: "writing", persona_id?: "kim" \| "park" \| ... }) |
| 랜딩 M.A.K.E.R.S 섹션 | “무료로 AI 심사 받아보기” 버튼 클릭 | GA4 `landing_cta_click` (params: { cta_type: "evaluation" }) |
| 랜딩 페르소나 카드 (`solution-steps` 섹션) | 각 페르소나 카드의 플랜별 CTA 클릭 (예: “프로 요금제 사전등록”) | GA4 `persona_cta_click` (params: { persona_id: "kim" \| "park" \| "choi" \| "han", plan_name }) |
| 작성 데모 (`/writing-demo`) | 데모 진입 시점(폼 처음 로드) | GA4 `writing_demo_start` (params: { source: "landing" \| "signup_complete" }) |
| 작성 데모/마법사 | 각 단계 완료 시 “다음 단계로” 클릭 | GA4 `wizard_step_complete` (params: { step_id, duration_seconds }) + 백엔드에서 해당 프로젝트/step 저장 시 `wizard_step_logs`(별도 테이블 또는 jsonb) |
| 평가 데모 (`/evaluation-demo`) | 평가 시작 버튼 클릭 | GA4 `evaluation_start` (params: { template_type, input_length }) |
| 평가/작성 결과 화면 | AI 피드백를 적용하기 위해 텍스트 블록을 자동 삽입 또는 “수정 적용” 버튼 클릭 | GA4 `ai_feedback_apply` (params: { source: "evaluation" \| "makers", section_id }) — 수정 루프 측정 핵심 |
| 평가/작성 결과 화면 | 2회 이상 재평가/재생성 실행 | GA4 `ai_regenerate` (params: { context: "evaluation" \| "business_plan", attempt_index }) |
| 프로젝트 리스트/뷰어 | 기존 프로젝트 다시 열람 (`/projects/:id` 또는 `/business-plan?projectId=…`) | GA4 `project_open` (params: { project_id, via: "list" \| "reminder" }) + 백엔드에서 `projects.last_opened_at` 필드 업데이트 |
| 리마인더 발송 (백엔드) | 마감 D-7/D-3/D-1 이메일/인앱 배너 발송 | 백엔드 `reminder_logs` 테이블 또는 `logs/notification-*.log`에 { user_id, project_id, reminder_type, send_time } 저장 |
| 리마인더 후 재방문 | 리마인더 발송 후 48시간 내 `project_open` 또는 데모 재실행 | GA4 `project_open_after_reminder` (params: { reminder_type, delay_hours }) — NSM Retention 분석용 |
| 가격/사전등록 섹션 (`pricing-section`) | 각 유료 플랜 카드의 CTA 클릭 | GA4 `pricing_cta_click` (params: { plan_name, phase: "A" \| "B", discount_rate }) |
| 팀/추천/공유 영역 | 결과/점수 카드 공유 버튼 클릭 | GA4 `share_result` (params: { channel, has_success_flag: true\|false }) |
| 성공 스토리 설문 (웹/외부 폼) | 합격/투자유치 후 스토리 제출 | GA4 `success_story_submit` (params: { persona_id, funding_type: "grant" \| "loan" \| "vc" }) + 백엔드 저장 구조(예: `success_stories` 테이블) |

### 3-3. NSM v1 “제출 가능 산출물” 판별을 위한 백엔드 상태/엔티티 설계 제안

- **Project 엔티티**에 다음 필드 추가 제안:
  - `status`: `draft` \| `in_progress` \| `ready_to_submit` \| `submitted`
  - `lastExportedAt`: 마지막 Export 시각
  - `lastEvaluatedAt`: 마지막 AI 평가/체크리스트 실행 시각
- **BusinessPlan / Evaluation / Export 로그**를 조합해 아래 조건을 충족하면 `ready_to_submit`로 승격:
  - `BusinessPlan` 존재 (`business_plans` 테이블에 레코드 ≥ 1)
  - `Evaluation` 또는 체크리스트 결과 존재(별도 테이블 또는 로그)
  - Export API 성공 기록(`ExportService`에서 `log.info` + 상태 업데이트)
- 이렇게 되면 NSM v1은 단순히 “`Project.status = 'ready_to_submit'` 인 레코드 수”로 손쉽게 집계할 수 있고,  
  GA4/로그는 **경로(어떤 행동을 통해 그 상태에 도달했는지)**를 분석하는 용도로 활용할 수 있습니다.

--- 

> 이 문서는 **초기 Draft**이며, 실제 구현 시 GA4 이벤트 이름/파라미터, 백엔드 엔티티 스키마는 개발팀과 합의 후 확정해야 합니다.  
> 단, 여기 정의된 “사용자 행동 단위”는 바꾸지 않는 것을 추천합니다. (그로스/분석의 기준축이 되기 때문입니다.)

