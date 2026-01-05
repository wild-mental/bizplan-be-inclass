# 프론트엔드-백엔드 연동 상태 리뷰

## 연동 상태 표

| 프론트엔드 페이지명 및 설명 | 백엔드 API 엔드포인트 URL 및 용도 | 호출조건 | 실제 호출 여부 | 정상 동작 여부 / 에러 내용 | API 요청 요약 | API 응답 요약 |
|---|---|---|---|---|---|---|
| **LandingPage**<br/>랜딩 페이지 - 사전등록 프로모션 표시 | `POST /api/v1/pre-registrations`<br/>사전등록 신청 및 할인코드 발급 | 유료 요금제 선택 시 사전등록 신청 버튼 클릭 | ✅ 호출됨 | ✅ 정상 동작 | `{ name, email, phone, selectedPlan, businessCategory?, agreeTerms, agreeMarketing }` | `{ id, discountCode, discountRate, selectedPlan, originalPrice, discountedPrice, registeredAt, status }` |
| | `GET /api/v1/pre-registrations/check-email?email={email}`<br/>이메일 중복 체크 | 이메일 입력 시 (실제 구현 여부 확인 필요) | ❓ 미확인 | ❓ 미확인 | `email` (query parameter) | `{ exists: boolean, discountCode?: string }` |
| | `GET /api/v1/promotions/current`<br/>현재 프로모션 정보 조회 | 프로모션 정보 표시 시 (실제 구현 여부 확인 필요) | ❓ 미확인 | ❓ 미확인 | 없음 | `{ isActive, currentPhase, discountRate, phaseAEnd, phaseBEnd, prices }` |
| **SignupPage**<br/>회원가입 페이지 | `POST /api/v1/auth/signup`<br/>회원가입 | 회원가입 폼 제출 시 | ✅ 호출됨 | ✅ 정상 동작 | `{ email, password, name, plan, termsAgreed, privacyAgreed, marketingConsent }` | `{ userId, email, name, accessToken, refreshToken }` |
| | `POST /api/v1/pre-registrations`<br/>사전등록 신청 (유료 요금제 시) | 유료 요금제 선택 + 회원가입 완료 후 | ✅ 호출됨 | ✅ 정상 동작 | `{ name, email, phone, selectedPlan, businessCategory?, agreeTerms, agreeMarketing }` | `{ id, discountCode, discountRate, selectedPlan, originalPrice, discountedPrice, registeredAt, status }` |
| | `POST /api/v1/auth/social/{provider}`<br/>소셜 로그인 (Google/Kakao/Naver) | 소셜 로그인 버튼 클릭 시 | ✅ 호출됨 (Mock 토큰 사용) | ⚠️ Mock 토큰 사용 중 | `{ accessToken, plan, termsAgreed, privacyAgreed, marketingConsent }` | `{ userId, email, name, accessToken, refreshToken, isNewUser }` |
| **LoginPage**<br/>로그인 페이지 | `POST /api/v1/auth/login`<br/>이메일/비밀번호 로그인 | 로그인 폼 제출 시 | ✅ 호출됨 | ✅ 정상 동작 | `{ email, password }` | `{ userId, email, name, accessToken, refreshToken }` |
| | `POST /api/v1/auth/social/{provider}`<br/>소셜 로그인 | 소셜 로그인 버튼 클릭 시 | ✅ 호출됨 (Mock 토큰 사용) | ⚠️ Mock 토큰 사용 중 | `{ accessToken, plan, termsAgreed, privacyAgreed, marketingConsent }` | `{ userId, email, name, accessToken, refreshToken, isNewUser }` |
| | `POST /api/v1/auth/refresh`<br/>토큰 갱신 | 액세스 토큰 만료 시 자동 호출 | ✅ 호출됨 (인터셉터) | ✅ 정상 동작 | `{ refreshToken }` | `{ accessToken, refreshToken }` |
| **ForgotPasswordPage**<br/>비밀번호 찾기 페이지 | `POST /api/v1/auth/password/reset-request`<br/>비밀번호 재설정 링크 요청 | 이메일 입력 후 제출 | ✅ 호출됨 | ✅ 정상 동작 | `{ email }` | `null` (보안상 항상 성공 응답) |
| **ResetPasswordPage**<br/>비밀번호 재설정 페이지 | `POST /api/v1/auth/password/reset`<br/>비밀번호 재설정 확인 | 새 비밀번호 입력 후 제출 | ✅ 호출됨 | ✅ 정상 동작 | `{ token, newPassword }` | `null` |
| **VerifyEmailPage**<br/>이메일 인증 페이지 | `GET /api/v1/auth/verify-email?token={token}`<br/>이메일 인증 확인 | 이메일 링크 클릭 시 | ✅ 호출됨 | ✅ 정상 동작 | `token` (query parameter) | `null` |
| | `POST /api/v1/auth/verify-email/resend?email={email}`<br/>이메일 인증 재발송 | 재발송 버튼 클릭 시 | ✅ 호출됨 | ✅ 정상 동작 | `email` (query parameter) | `null` |
| **ProjectCreate**<br/>프로젝트 생성 페이지 | `GET /api/v1/projects/templates`<br/>템플릿 목록 조회 | 페이지 로드 시 (실제 구현 여부 확인 필요) | ❓ 미확인 | ❓ 미확인 | 없음 | `[{ code, name, description }]` |
| | `POST /api/v1/projects`<br/>프로젝트 생성 | 템플릿 선택 후 제출 | ⚠️ 로컬 Store만 사용 (백엔드 미호출) | ❌ 백엔드 미연동 | `{ name, templateId, supportProgram?, description? }` | `{ id, name, templateId, templateName, status, progress, createdAt, updatedAt }` |
| **WizardStep**<br/>사업계획서 작성 마법사 (1-6단계) | `PUT /api/v1/projects/{projectId}/wizard`<br/>Wizard 데이터 저장 | 단계별 입력 시 자동 저장 (실제 구현 여부 확인 필요) | ❓ 미확인 | ❓ 미확인 | `{ currentStep, stepData, isStepComplete? }` | `{ lastSavedAt, progress }` |
| | `GET /api/v1/projects/{projectId}/wizard`<br/>Wizard 데이터 조회 | 페이지 로드 시 (실제 구현 여부 확인 필요) | ❓ 미확인 | ❓ 미확인 | 없음 | `{ projectId, templateId, currentStep, steps[], lastSavedAt }` |
| | `POST /api/v1/projects/{projectId}/wizard/budget/validate`<br/>자금 집행계획 검증 | 3단계에서 예산 계산기 사용 시 (실제 구현 여부 확인 필요) | ❓ 미확인 | ❓ 미확인 | `{ budgetData }` | `{ isValid, summary, validations[], warnings[] }` |
| | `POST /api/v1/business-plan/generate`<br/>AI 사업계획서 생성 | 6단계 완료 후 "AI 사업계획서 생성" 버튼 클릭 | ✅ 호출됨 | ✅ 정상 동작 | `{ requestInfo: { templateType, generatedAt, userId, projectId }, businessPlanData: { step1-6 }, generationOptions: { tone, targetLength, outputFormat, language, sections } }` | `{ businessPlanId, projectId, generatedAt, templateType, sections[], metadata: { totalSections, wordCount, characterCount, generationTimeMs, modelUsed, promptTokens, completionTokens, totalTokens }, exportOptions: { availableFormats[], downloadUrls{} } }` |
| **BusinessPlanViewer**<br/>생성된 사업계획서 뷰어 | `GET /api/v1/business-plan/{businessPlanId}`<br/>사업계획서 조회 | 페이지 로드 시 (실제 구현 여부 확인 필요) | ❓ 미확인 | ❓ 미확인 | 없음 | `{ businessPlanId, projectId, generatedAt, templateType, sections[], metadata, exportOptions }` |
| | `POST /api/v1/projects/{projectId}/export`<br/>문서 내보내기 요청 (HWP/PDF) | 내보내기 버튼 클릭 시 | ⚠️ Mock 처리 (실제 API 미호출) | ❌ 백엔드 미연동 | `{ format: 'hwp'|'pdf'|'docx', templateType, options: { maskPersonalInfo, includeAppendix, includeCoverPage, pageNumbering, watermark } }` | `{ exportId, status: 'pending'|'processing'|'completed'|'failed' }` |
| | `GET /api/v1/exports/{exportId}/status`<br/>내보내기 상태 조회 | 내보내기 진행 중 상태 확인 (실제 구현 여부 확인 필요) | ❓ 미확인 | ❓ 미확인 | 없음 | `{ exportId, status, format, fileName?, fileSize?, downloadUrl?, expiresAt?, completedAt?, errorMessage? }` |
| | `GET /api/v1/exports/{exportId}/download`<br/>파일 다운로드 | 내보내기 완료 후 다운로드 (실제 구현 여부 확인 필요) | ❓ 미확인 | ❓ 미확인 | 없음 | 파일 바이너리 (blob) |
| **EvaluationDemo**<br/>AI 평가 데모 페이지 | `POST /api/v1/evaluations`<br/>평가 요청 | 평가 입력 완료 후 제출 | ⚠️ Mock 처리 (실제 API 미호출) | ❌ 백엔드 미연동 | `{ projectId, evaluationType: 'demo'|'basic'|'full', inputData: { businessName, businessField, targetMarket, problemStatement, solutionSummary, differentiators[], teamExperience, fundingGoal } }` | `{ evaluationId, status: 'pending'|'processing'|'completed'|'failed' }` |
| | `GET /api/v1/evaluations/{evaluationId}/status`<br/>평가 진행 상태 조회 | 평가 진행 중 상태 확인 | ⚠️ Mock 처리 (실제 API 미호출) | ❌ 백엔드 미연동 | 없음 | `{ evaluationId, status, progress, currentStage, stages[], estimatedRemaining? }` |
| | `GET /api/v1/evaluations/{evaluationId}/result`<br/>평가 결과 조회 | 평가 완료 후 결과 조회 | ⚠️ Mock 처리 (실제 API 미호출) | ❌ 백엔드 미연동 | 없음 | `{ evaluationId, summary: { totalScore, grade, passRate, passRateMessage }, scores{}, strengths[], weaknesses[], recommendations[], accessLevel }` |
| **AdminPage**<br/>어드민 대시보드 | `GET /api/v1/admin/users`<br/>사용자 목록 조회 | 페이지 로드 시 | ✅ 호출됨 | ✅ 정상 동작 | `{ page, size, sortBy, sortDirection, planFilter?, providerFilter?, emailVerifiedFilter?, searchKeyword? }` | `{ users[], pagination: { page, size, totalElements, totalPages, hasNext, hasPrevious } }` |
| | `GET /api/v1/admin/statistics`<br/>사용자 통계 조회 | 페이지 로드 시 | ✅ 호출됨 | ✅ 정상 동작 | 없음 | `{ overall: { totalUsers, verifiedUsers, unverifiedUsers, marketingConsentUsers, paidPlanUsers, freePlanUsers }, signupByDate[], signupByWeek[], signupByMonth[], byPlan[], byProvider[], byCategory[] }` |
| **TeamPage**<br/>팀 소개 페이지 | 없음 | 정적 콘텐츠 페이지 | - | - | - | - |
| **기타 (인터셉터)** | `POST /api/v1/auth/refresh`<br/>토큰 갱신 | 액세스 토큰 만료 시 자동 호출 (apiClient 인터셉터) | ✅ 호출됨 | ✅ 정상 동작 | `{ refreshToken }` | `{ accessToken, refreshToken }` |
| | `POST /api/v1/auth/logout`<br/>로그아웃 | 로그아웃 시 (실제 구현 여부 확인 필요) | ❓ 미확인 | ❓ 미확인 | 없음 | `null` |
| | `GET /api/v1/auth/profile`<br/>사용자 프로필 조회 | 프로필 조회 시 (실제 구현 여부 확인 필요) | ❓ 미확인 | ❓ 미확인 | 없음 | `{ userId, email, name, plan, emailVerified, marketingConsent }` |

## 연동 상태 요약

### ✅ 완전 연동 완료
- **인증 관련**: 회원가입, 로그인, 소셜 로그인, 토큰 갱신, 비밀번호 재설정, 이메일 인증
- **사전등록**: 사전등록 신청
- **사업계획서 생성**: AI 사업계획서 생성 (POST /api/v1/business-plan/generate)
- **어드민**: 사용자 목록 조회, 통계 조회

### ⚠️ 부분 연동 (Mock 또는 미구현)
- **프로젝트 관리**: 프로젝트 생성은 로컬 Store만 사용, 백엔드 미연동
- **Wizard 데이터**: 백엔드 API는 구현되어 있으나 프론트엔드에서 호출하지 않음
- **문서 내보내기**: 백엔드 API는 구현되어 있으나 프론트엔드에서 Mock 처리
- **AI 평가**: 백엔드 API는 구현되어 있으나 프론트엔드에서 Mock 처리

### ❓ 미확인 (백엔드 API는 구현되어 있으나 프론트엔드 호출 여부 불명)
- 템플릿 목록 조회
- Wizard 데이터 저장/조회
- 자금 집행계획 검증
- 사업계획서 조회
- 내보내기 상태 조회/다운로드
- 평가 상태 조회/결과 조회
- 프로모션 정보 조회
- 이메일 중복 체크
- 로그아웃
- 프로필 조회

## 권장 사항

1. **프로젝트 관리 연동**: ProjectCreate 페이지에서 백엔드 API 호출하도록 수정
2. **Wizard 데이터 저장**: 단계별 입력 시 백엔드에 자동 저장 기능 추가
3. **문서 내보내기 연동**: BusinessPlanViewer에서 실제 백엔드 API 호출하도록 수정
4. **AI 평가 연동**: EvaluationDemo에서 실제 백엔드 API 호출하도록 수정
5. **미확인 API 확인**: 위의 "미확인" 항목들에 대해 실제 호출 여부 확인 및 필요시 연동
