# Software Requirements Specification (SRS)

Document ID: SRS-001-GPT

Revision: 1.1

Date: 2025-11-18

Standard: ISO/IEC/IEEE 29148:2018

-------------------------------------------------

1. Introduction

   1.1 Purpose

   - 본 Software Requirements Specification(SRS)는 "AI Co-Pilot for First-time Founders" 시스템의 기능 및 비기능 요구사항을 정의한다.
   - 이 시스템은 예비·초기 창업자가 정부지원사업 및 은행 대출 등 외부 심사를 위한 한글 사업계획서와 재무 추정을 제출 가능 수준으로 준비하고, 제품-시장 적합도(PMF)와 실패 리스크를 사전에 진단하도록 지원하는 것을 목적으로 한다.
   - 본 SRS는 PRD "[PRD] AI Co-Pilot for First-time Founders V1.0 (통합본)"(REF-01)을 **유일한 비즈니스·기능 요구의 원천(Source of Truth)**으로 사용하며, 제품 기획, 설계, 개발, 시험, 운영 및 변경관리의 기준 문서로 활용된다.

   1.2 Scope (In-Scope / Out-of-Scope)

   1.2.1 In-Scope (이번 릴리즈)

   - F1: **정부/은행 제출용 한글 Wizard**
     - 예비창업패키지 등 실제 공고 양식을 단계별로 쉽게 작성하도록 안내하는 한국어 Wizard UI.
     - 템플릿별 필수 항목 강제 및 누락 방지.
   - F2: **템플릿 100% 호환 + HWP/PDF 내보내기**
     - 국내 주요 기관의 사업계획서 양식을 100% 지원하고, 해당 포맷(HWP/PDF 등)으로 내보내기(Export).
   - F3: **핵심 변수 기반 재무 자동화 엔진**
     - 매출, CAC, LTV 등 핵심 지표 입력 시 3년치 손익계산서 및 기본 현금흐름표 자동 생성.
   - F4: **AI 초안 생성 + 쉬운 모드/전문가 모드 편집기**
     - 각 항목별 AI 작성 예시/초안 생성과, 초보자용(질문 중심)·전문가용(문서 구조 중심) 편집 모드 제공.
   - F5: **PMF 진단 프레임워크 및 리포트**
     - TAM-SAM-SOM, 가치사슬 분석 등 표준 프레임워크 기반의 사업성·위험 진단 및 리포트 생성.
   - F6: **On-premise/Private Cloud 보안 옵션**
     - 민감 정보를 다루는 기업 고객을 위한 설치형 또는 프라이빗 클라우드 배포 옵션.  
       (※ **MVP 1차 릴리즈에서는 설계/인터페이스 수준까지 구현하고, 실제 상용 On-prem 패키지는 Post-MVP(Phase 2) 목표로 한다.**)

   1.2.2 Out-of-Scope (이번 릴리즈 제외)

   - F7: **PMF·IR 대시보드(가설↔재무 연동) UI**
     - 가설/지표/재무를 한 화면에서 연결해 보여주는 대시보드 UI (단, 일부 데이터 스키마 설계는 가능).
   - F8: **Notion/Jira 실행-재무 동기화(양방향)**
     - Notion/Jira 등 외부 도구와의 **양방향** 데이터 동기화 및 실행-재무 완전 통합 기능.
   - 정부/은행 포털로의 **직접 전자 제출** 기능 (본 릴리즈에서는 파일 다운로드까지만 제공).
   - 전용 모바일 앱(iOS/Android) 및 한국어 외 다국어 전체 UI.
   - 2인 초과 동시 편집을 포함한 고급 협업 기능.

   1.2.3 Constraints and Assumptions

   - **비즈니스/운영 제약(Constraints)**
     - 정부·은행 공고 양식은 비정기적으로 변경될 수 있으며, 시스템은 템플릿/Validator 룰셋의 버전 관리 및 롤백을 지원해야 한다. (PRD §6.3 Risk 1)
     - 재무 계산은 규칙 기반 엔진이 책임지고, LLM은 서술·구조화에만 사용하여 잘못된 수치 자동 생성으로 인한 리스크를 최소화해야 한다. (PRD §6.3 Risk 2)
     - LLM API, HWP/PDF 변환 솔루션 등 외부 상용 서비스의 가용성·가격 정책에 따라 시스템 운영 비용과 품질이 영향을 받을 수 있다. (PRD §6.4)
   - **기술 스택 제약(Technology Constraints, C-TEC)**
     - C-TEC-001: 모든 프론트엔드 서비스는 **Vite 기반 React.js**를 사용한다.
     - C-TEC-002: 모든 백엔드 코어 서비스는 **Java 17 + Spring Boot 3.x (JVM 기반)**을 사용한다.
     - C-TEC-003: 데이터베이스는 **MySQL(InnoDB, 유니코드 확장 문자 인코딩 – utf8mb4, 버전 8.x 이상)**을 사용한다.
     - C-TEC-004: 문서 자동생성 및 LLM 오케스트레이션 엔진은 **Python + FastAPI + LangChain** 기반 별도 서비스로 구현한다.
     - C-TEC-005: LLM 호출은 **사내 LLM Gateway**를 통해 이루어지며, 기본 Provider는 **Google Gemini**이고, 필요 시 OpenAI 등으로 교체 가능하도록 추상화한다.
     - C-TEC-006: 서비스 간 통신은 **REST(OpenAPI 3.x)**를 기본으로 하며, gRPC는 향후 고성능 내부 통신이 필요한 MSA 서비스에서 선택적으로 사용할 수 있다. (※ **MVP 단계에서는 REST만 사용**을 기본 가정으로 한다.)
   - **가정 및 의존성(Assumptions & Dependencies)**
     - 타깃 사용자는 사업계획서 작성 시간 단축 및 합격률 향상을 위해 월 구독료(예: 29,000원)를 지불할 의사가 있다. (PRD §6.4)
     - 예비·초기 창업자는 웹 기반 SaaS 도구 사용에 큰 거부감이 없으며, 기본적인 온라인 서비스 사용 경험을 보유하고 있다.
     - 외부 LLM 공급자(예: Google Gemini, OpenAI)와 HWP/PDF 변환 솔루션은 상용 환경에서 SLA 수준의 안정성을 제공한다.
     - 인프라는 **AWS** 상에서 운영되며, AWS 관리형 서비스(RDS for MySQL, S3, CloudWatch 등)를 활용한다.

   1.3 Definitions, Acronyms, Abbreviations

   | 용어 | 정의 |
   | :--- | :--- |
   | **PRD** | Product Requirements Document. 본 SRS의 비즈니스·기능 요구사항의 유일한 소스(REF-01). |
   | **SRS** | Software Requirements Specification. 본 문서. |
   | **AC (Acceptance Criteria)** | 기능이 "완료"로 간주되기 위해 만족해야 하는 조건. Given/When/Then 형식으로 기술. |
   | **GWT** | Given / When / Then 형식의 테스트 가능한 시나리오 서술 방식. |
   | **EPIC** | 상위 수준 사용자 목표를 나타내는 사용자 스토리 묶음. (EPIC 1: 과제 통과, EPIC 2: 실패 회피) |
   | **F1~F8** | PRD에서 정의된 주요 기능(Features). |
   | **JTBD (Jobs-to-be-Done)** | 사용자가 달성하려는 과업. 예: 정부/은행 관문 통과, 실패 요인 사전 진단. |
   | **AOS (Adjusted Opportunity Score)** | 조정된 기회 점수. JTBD/세그먼트별 우선순위 산출 지표. |
   | **DOS (Discovered Opportunity Score)** | 발견된 기회 점수. 리서치를 통해 발견된 기회 크기 지표. |
   | **TTV (Time-to-Value)** | 가입 후 첫 "제출 가능" 산출물을 얻기까지 걸리는 시간. |
   | **Activation Rate** | 가입 사용자 중 첫 세션에서 제출 가능한 산출물을 완성한 사용자 비율. |
   | **PMF (Product-Market Fit)** | 제품-시장 적합도. PMF 진단 리포트에서 단계로 표현. |
   | **CAC** | Customer Acquisition Cost. 고객 1명당 획득 비용. |
   | **LTV** | Life Time Value. 고객 생애 가치. |
   | **LTV/CAC** | 고객 생애 가치 대비 획득 비용 비율. |
   | **SLA** | Service Level Agreement. 가용성/성능 등 서비스 수준 합의. |
   | **RPO/RTO** | Recovery Point Objective / Recovery Time Objective. 데이터 손실 허용 한도 및 복구 시간 목표. |
   | **p95** | 95번째 퍼센타일 지표. 응답시간, 커버리지 등 성능·품질 측정에 사용. |
   | **On-premise** | 고객사 자체 인프라(사내 데이터센터 등)에 설치·운영하는 배포 방식. |
   | **C-TEC-001~006** | 본 SRS 1.2.3에 정의된 기술 스택 제약(Frontend, Backend, DB, 문서 엔진, LLM Gateway, 통신 방식) 식별자. |

   1.4 References (REF-XX)

   | ID | 문서명 | 위치 |
   | :--- | :--- | :--- |
   | **REF-01** | [PRD] AI Co-Pilot for First-time Founders V1.0 (통합본) | `/2_핵심예제_분석자료/9_GPT-Gemini_Merged_PRD.md` |
   | **REF-02** | 통합 가치 제안서 (Gemini-GPT Merged Value Proposition) | `/2_핵심예제_분석자료/8-3_Gemini-GPT_Merged_ValueProposition.md` |
   | **REF-03** | JTBD 가상 인터뷰 결과지 V2 | `/2_핵심예제_분석자료/8-2_SaaS형_온라인_비즈니스_컨설팅_JTBD_가상인터뷰_결과지_V2.md` |
   | **REF-04** | SaaS형 온라인 비즈니스 컨설팅 TAM-SAM-SOM 핵심 원인 분석 | (해당 마켓 리서치 문서, 필요 시 추가) |

2. Stakeholders

   | 역할(Role) | 설명 | 책임(Responsibilities) | 관심사(Interests) |
   | :--- | :--- | :--- | :--- |
   | **김예비 (예비창업자, 비전공자)** | 정부/지자체 예비창업패키지 등 과제에 처음 도전하는 비전공 창업자. | 사업 아이디어·기본 정보 입력, Wizard를 활용한 사업계획서·재무 초안 작성. | 제출 가능 수준의 문서를 빠르게 완성, 서류 미비·논리 부족으로 인한 탈락 방지. |
   | **박사장 (소상공인/영세 자영업자)** | 오프라인 매장을 운영하며 은행 정책자금 대출을 준비하는 소상공인. | 기존 매장 매출·비용·재방문 데이터 입력, 은행 양식에 맞는 재무 계획 작성. | 외부 컨설턴트 의존 없이 규격 재무 모델을 단기간에 완성, 대출 심사 통과 가능성 제고. |
   | **최민혁 (재도전/시리얼 창업가)** | 이전 창업에서 실패 경험이 있으며, 이번에는 PMF와 유닛 이코노믹스를 선제적으로 검증하려는 사용자. | 가설·지표·재무 가정 입력, PMF 진단 및 유닛 이코노믹스 뷰 활용. | 잘못된 확장·시장 수요 부재로 인한 재실패 방지, 데이터 기반 의사결정. |
   | **Product Owner / Founders Success Team** | PRD를 소유하고 제품 방향성을 결정하는 팀. | 기능 우선순위 결정, KPI 정의(TTV, Activation, 과제 통과율 등), 릴리즈 스코프 관리. | PRD에 정의된 KPI 달성, 사용자 페인 해결, 제품-시장 적합도 확보. |
   | **개발팀(Backend/Frontend/ML)** | 시스템 설계·구현·테스트를 수행하는 기술팀. | 본 SRS에 정의된 기능/비기능 요구사항 구현, 안정성·보안·성능 확보. | 명확하고 테스트 가능한 요구사항, 기술 부채 최소화, 배포 자동화. |
   | **운영·템플릿 관리자** | 정부/은행 양식, Validator 룰셋, 템플릿 버전 관리를 담당. | 신규 공고 템플릿 등록, 버전 업/롤백, 에러 모니터링 및 운영 알림 대응. | 템플릿 변경에 대한 빠른 대응(3일 이내 반영), 사용자 혼란 최소화. |
   | **보안/컴플라이언스 담당자** | 민감 데이터 보호 및 규제 준수를 책임지는 이해관계자. | 접근통제 정책 수립, On-premise/Private Cloud 보안 검토. | 데이터 유출·오남용 방지, 감사 로그 및 규제 준수. |

3. System Context and Interfaces

   3.1 External Systems

   - **외부 LLM Gateway 및 Provider (기본: Google Gemini)**  
     - 문서 초안, PMF 리포트, 정합성 검증 등 LLM 기반 기능을 위해 사용된다.
     - 내부 LLM Gateway를 통해 Google Gemini를 기본 Provider로 사용하며, 필요 시 OpenAI 등 다른 Provider로 교체 가능하도록 추상화한다.
   - **결제 게이트웨이 (예: 카드 결제/간편결제)**  
     - 구독 과금 및 결제 처리를 위한 외부 결제 시스템.
   - **문서 변환 서비스(HWP/PDF 변환기)**  
     - 내부 또는 제3자 솔루션을 통해 제출용 HWP/PDF 파일을 생성한다.
   - **모니터링 및 로깅 플랫폼 (예: AWS CloudWatch, Prometheus/Grafana, APM)**  
     - 성능(p95), 오류율, 비용, 사용 행태를 모니터링하고 알림을 발생시킨다.
   - **On-premise/Private Cloud 인프라 (Post-MVP)**  
     - 기업 고객이 자체적으로 운영하는 쿠버네티스/VM 환경.  
       (MVP에서는 SaaS(AWS) 배포를 우선 지원하며, On-prem은 Phase 2에서 패키징·자동화한다.)

   3.2 Client Applications

   - **웹 애플리케이션(Web Client)**  
     - Vite 기반 React.js SPA로 구현되며, 크롬 등 현대 브라우저에서 접속 가능한 메인 사용자 인터페이스이다.
     - Wizard, 편집기, PMF 리포트 뷰어, 유닛 이코노믹스 시각화 등을 제공한다.
   - **관리자 콘솔(Admin Console)**  
     - 템플릿/Validator 룰셋 관리, 모니터링 뷰, 운영 알림 확인용 웹 UI (내부 사용자용).
     - 동일 Vite+React 스택을 사용하되, 별도의 관리자 권한이 필요하다.

   3.3 API Overview

   - 백엔드 코어는 **Java 17 + Spring Boot 3.x** 기반 REST API 서버로 구현되며, 웹 클라이언트는 HTTPS를 통해 JSON 형식으로 통신한다.
   - LLM/문서 자동생성 기능은 별도의 **Python + FastAPI 기반 “Document Generation / PMF Engine” 서비스**로 구현되며, 코어 백엔드는 이 서비스와 REST(내부 네트워크)로 통신한다. (gRPC는 향후 고성능 필요 시 도입 가능.)
   - 주요 외부/내부 API 유형:
     - **프로젝트 관리 (Spring Boot)**:  
       `POST /projects`, `GET /projects/{id}`, `POST /projects/{id}/wizard/steps`
     - **문서 생성 (Spring Boot → FastAPI + LLM)**:  
       `POST /projects/{id}/documents/business-plan:generate`
     - **PMF 진단 (Spring Boot → FastAPI + LLM)**:  
       `POST /projects/{id}/pmf-report:generate`
     - **내보내기(Export) (Spring Boot → 변환 서비스)**:  
       `GET /projects/{id}/export?format=hwp|pdf`
   - 모든 외부 클라이언트 API 호출은 인증 토큰을 요구하며, 요청 크기·레이트 리밋·타임아웃은 NFR(REQ-NF-001 등)에 정의된 범위 내에서 동작해야 한다.

   3.4 Interaction Sequences (핵심 시퀀스 다이어그램)

   3.4.1 EPIC 1 – 과제 통과용 사업계획서 초안 생성

   ```mermaid
   sequenceDiagram
       actor Founder as 사용자(창업가)
       participant Web as Web App (Vite+React)
       participant API as Backend API (Spring Boot)
       participant LLM as LLM Gateway (Gemini)
       participant Store as Document Store (MySQL)

       Founder->>Web: 1. 로그인 및 '새 프로젝트 생성' 선택
       Web->>API: 2. POST /projects (템플릿=예비창업패키지)
       API-->>Web: 3. 프로젝트 ID 반환
       Founder->>Web: 4. Wizard에서 질문에 답변 입력
       Web->>API: 5. POST /projects/{id}/wizard/steps (답변 저장)
       API->>Store: 6. wizard_answers 저장
       Founder->>Web: 7. '초안 생성' 클릭
       Web->>API: 8. POST /projects/{id}/documents/business-plan:generate
       API->>LLM: 9. 섹션별 프롬프트 전송 (내부: FastAPI 엔진 → Gemini)
       LLM-->>API: 10. 섹션별 서술 초안 반환
       API->>Store: 11. 초안 문서 및 메타데이터 저장
       API-->>Web: 12. 초안 문서 반환 (필수 항목 커버리지 포함)
       Web-->>Founder: 13. 제출 가능 수준 초안 표시
   ```

   3.4.2 EPIC 2 – PMF 진단 및 실패 리스크 확인

   ```mermaid
   sequenceDiagram
       actor Founder as 사용자(창업가)
       participant Web as Web App
       participant API as Backend API (Spring Boot)
       participant PMF as PMF Engine (FastAPI+Gemini)

       Founder->>Web: 1. PMF 진단 질문(≥10개) 응답
       Web->>API: 2. POST /projects/{id}/pmf-report:generate
       API->>PMF: 3. 입력 데이터 전송 및 검증 요청
       PMF-->>API: 4. PMF 단계, 리스크 Top3, 개선 권고 반환
       API-->>Web: 5. PMF 리포트 반환
       Web-->>Founder: 6. 리포트 렌더링 (그래프/텍스트)
   ```

4. Specific Requirements

   4.1 Functional Requirements

   - 모든 기능 요구사항은 **REQ-FUNC-xxx** 형식의 ID를 부여하며, 각 요구사항은 단일 행동 또는 결과를 정의하는 **원자적(atomic)** 요구로 작성된다.
   - 각 요구사항은 PRD의 **EPIC/Story/Feature(F1~F6)**를 Source로 연결하며, 주요 AC는 Given/When/Then 형식으로 정리한다.

   | ID | Title | Description | Source (Story/Feature) | Acceptance Criteria (Given/When/Then) | Priority |
   | :--- | :--- | :--- | :--- | :--- | :--- |
   | **REQ-FUNC-001** | 템플릿 목록 제공 | 시스템은 정부/은행 과제용 템플릿 목록을 제공하여 사용자가 적절한 양식을 선택할 수 있어야 한다. | EPIC 1, F1, F2 | **Given** 사용자가 로그인하여 '새 프로젝트 생성' 화면에 진입했을 때 **When** 템플릿 선택 섹션을 열면 **Then** 예비창업패키지, 은행 정책자금 등 최소 2개 이상의 실제 공고 템플릿이 목록으로 표시되어야 한다. | Must |
   | **REQ-FUNC-002** | Wizard 단계 진행 | 시스템은 선택된 템플릿에 따라 단계별 Wizard UI를 제공하고, 각 단계의 필수 질문을 강제해야 한다. | EPIC 1, F1 | **Given** 사용자가 템플릿을 선택한 상태에서 **When** Wizard를 시작하면 **Then** 단계 진행률이 표시되고, 필수 질문을 채우지 않으면 다음 단계로 이동할 수 없어야 한다. | Must |
   | **REQ-FUNC-003** | 사업계획서 초안 자동 생성 | 시스템은 사용자가 템플릿과 기본 사업 정보를 입력하고 '초안 생성'을 클릭하면, 선택된 공고 양식의 필수 목차를 100% 포함하는 사업계획서 초안을 생성해야 한다. | EPIC 1 AC 1.1, F1, F2, F4 | **Given** 사용자가 '예비창업패키지' 템플릿을 선택하고 필수 기본 정보를 입력한 상태에서 **When** '초안 생성' 버튼을 클릭하면 **Then** 해당 공고의 필수 목차 누락률 0%인 초안 문서가 생성·저장되고 화면에 표시되어야 한다. | Must |
   | **REQ-FUNC-004** | 섹션별 AI 작성 보조 | 시스템은 각 섹션에서 AI 기반 초안/보완 텍스트를 생성하는 기능을 제공해야 한다. | EPIC 1, F4 | **Given** 사용자가 특정 섹션을 편집 중인 상태에서 **When** 'AI로 작성' 또는 'AI로 보완' 버튼을 클릭하면 **Then** LLM을 통해 생성된 텍스트 후보가 1개 이상 표시되고, 사용자는 이를 적용 또는 취소할 수 있어야 한다. | Must |
   | **REQ-FUNC-005** | 제출 가능성 체크리스트 | 시스템은 생성된 사업계획서 초안에 대해 최소 10개 이상의 제출 가능성 체크리스트 항목과 보완 가이드를 제공해야 한다. | EPIC 1 AC 1.2, F1, F2, F4 | **Given** 첫 사업계획서 초안이 생성된 상태에서 **When** 사용자가 '제출 가능성 점검' 기능을 실행하면 **Then** 문제 정의·시장·경쟁·재무 등 영역별 10개 이상 체크리스트에 대해 ✔/✖ 상태와 각 ✖ 항목에 대한 보완 가이드가 표시되고, 사용자가 90% 이상을 ✔로 만들면 점검 상태가 '제출 가능'으로 변경되어야 한다. | Must |
   | **REQ-FUNC-006** | 재무-서술 논리 정합성 검증 | 시스템은 재무 가정과 서술 내용 간 불일치를 자동 탐지하고 구체적인 경고와 수정 제안을 제시해야 한다. | EPIC 1 AC 1.3, F3, F5 | **Given** 사용자가 Wizard를 통해 매출·비용·성장률 등 재무 가정을 입력한 상태에서 **When** '논리 정합성 검증'을 실행하면 **Then** PMF·시장 규모·가격·재무 가정 간 불일치를 최소 3건 이상 구체적인 메시지(예: "시장 규모 대비 3년 차 매출 과다")와 함께 보여주어야 한다. | Must |
   | **REQ-FUNC-007** | 필수 입력 누락 방지 | 시스템은 필수 재무 필드가 비어 있는 상태에서 완료를 시도할 경우 경고 메시지를 표시하고 해당 필드로 포커스를 이동해야 한다. | EPIC 1 AC 1.4, F1, F3 | **Given** '월간 순이익'과 같이 필수로 지정된 입력 필드가 비어 있는 상태에서 **When** 사용자가 '완료' 또는 '다음 단계'를 클릭하면 **Then** "필수 항목이 누락되었습니다"라는 메시지를 보여주고 첫 번째 누락 필드로 포커스를 이동하며, 완료 처리 또는 다음 단계 이동을 차단해야 한다. | Must |
   | **REQ-FUNC-008** | PMF 진단 리포트 생성 | 시스템은 PMF 진단 질문에 대한 응답을 기반으로 PMF 단계, 핵심 리스크, 개선 권고를 포함한 리포트를 생성해야 한다. | EPIC 2 AC 2.1, F5 | **Given** 사용자가 사업 아이템/타깃 고객/가치 제안 등 최소 필수 10개 PMF 질문에 답변한 상태에서 **When** 'PMF 진단하기' 버튼을 클릭하면 **Then** PMF 성숙도 단계와 함께 최소 3개 이상의 핵심 리스크 및 각 리스크별 개선 권고가 포함된 PMF 리포트가 생성·저장·표시되어야 한다. | Should |
   | **REQ-FUNC-009** | 유닛 이코노믹스 뷰 제공 | 시스템은 사용자가 입력한 CAC, ARPU, 이탈률을 기반으로 LTV/CAC 및 손익분기점을 계산·시각화해야 한다. | EPIC 2 AC 2.2, F3, F5 | **Given** 사용자가 CAC, ARPU, 이탈률 등 유닛 이코노믹스 관련 가설 수치를 입력한 상태에서 **When** '유닛 이코노믹스 보기' 기능을 실행하면 **Then** LTV/CAC 비율과 손익분기점(개월 수)를 계산해 시각화하고, LTV/CAC < 1 또는 손익분기점 ≥ 36개월인 경우 적색 경고와 가정 조정 제안을 표시해야 한다. | Should |
   | **REQ-FUNC-010** | 데이터 부족 시 PMF 진단 차단 | 시스템은 PMF 진단에 필요한 최소 응답 수가 충족되지 않을 경우 진단을 차단하고 체크리스트와 예상 소요시간을 안내해야 한다. | EPIC 2 AC 2.3, F5 | **Given** 사용자가 필수 PMF 질문 5개 이하에만 답변한 상태에서 **When** PMF 진단을 요청하면 **Then** "데이터 부족으로 신뢰도 높은 진단 불가" 메시지와 함께 필수 최소 입력 항목 목록 및 예상 소요시간(예: 15분)을 표시하고, PMF 단계/리스크 결과는 생성하지 않아야 한다. | Must |
   | **REQ-FUNC-011** | HWP/PDF 내보내기 | 시스템은 제출 가능 상태의 사업계획서를 선택한 템플릿과 100% 필드 호환되는 HWP/PDF 파일로 내보내야 한다. | EPIC 1, F2 | **Given** 사업계획서 상태가 '제출 가능'인 프로젝트에서 **When** 사용자가 '내보내기' 버튼을 클릭하고 HWP 또는 PDF 포맷을 선택하면 **Then** 선택한 공고 양식과 동일한 구조·필수 항목을 가진 파일이 생성되며, 필수 필드는 모두 값이 채워지고 내부 플레이스홀더는 남지 않아야 한다. | Must |
   | **REQ-FUNC-012** | 재무 자동화 엔진 | 시스템은 핵심 재무 변수(예: 예상 고객 수, 단가, 고정비/변동비)를 입력 받아 최소 3년치 손익계산서와 기본 현금흐름표를 자동으로 생성해야 한다. | F3 | **Given** 사용자가 요구된 핵심 재무 변수들을 모두 입력한 상태에서 **When** '재무 추정 생성'을 실행하면 **Then** 3년치 손익계산서 및 최소 연 단위 현금흐름표가 자동 계산되어 테이블로 표시·저장되어야 한다. | Must |
   | **REQ-FUNC-013** | 자동 저장(Auto-save) | 시스템은 문서 편집 중 사용자의 입력 내용을 주기적으로 자동 저장해야 한다. | F1, F3 | **Given** 사용자가 문서의 어느 필드를 수정한 후 일정 시간 동안 추가 입력이 없는 상태에서 **When** 자동 저장 주기(최대 10초)가 도래하면 **Then** 최신 내용이 서버에 저장되고 마지막 저장 시각이 UI에 표시되어야 하며, 브라우저를 닫았다 다시 열어도 해당 내용이 복원되어야 한다. | Must |
   | **REQ-FUNC-014** | 쉬운 모드/전문가 모드 전환 | 시스템은 동일 프로젝트에서 쉬운 모드(질문 기반)와 전문가 모드(문서 구조 기반) 간 손실 없이 전환을 지원해야 한다. | F4 | **Given** 사용자가 하나의 프로젝트에서 문서를 편집 중일 때 **When** '쉬운 모드/전문가 모드' 토글을 변경하면 **Then** 기존 입력 데이터가 손실 없이 각각 Wizard 뷰와 전체 문서 뷰로 재구성되어 표시되어야 한다. | Must (※ 구현 우선순위는 MVP 내에서도 후순위로 둘 수 있음) |
   | **REQ-FUNC-015** | On-premise 배포 패키지 | 시스템은 On-premise/Private Cloud 환경을 위한 설치 패키지(예: Docker Compose)를 제공해야 한다. | F6 | **Given** 기업 고객이 On-premise 옵션을 계약한 상태에서 **When** 설치 패키지를 내려받아 안내서에 따라 설치하면 **Then** 외부 인터넷 연결 없이도 핵심 기능(F1~F5)을 사내 인프라에서 실행할 수 있어야 한다. (※ Target Release: Post-MVP Phase 2) | Should |
   | **REQ-FUNC-016** | 워크스페이스별 데이터 격리 설정 | 시스템은 조직 단위 워크스페이스마다 데이터 저장 위치 및 배포 옵션(Cloud/Private/On-premise)을 설정할 수 있어야 한다. | F6 | **Given** 조직 관리자 계정으로 워크스페이스를 생성하는 상황에서 **When** 배포 옵션을 선택하면 **Then** 해당 워크스페이스의 모든 데이터와 로그는 선택된 환경 내에만 저장·처리되어야 한다. (※ Target Release: Post-MVP Phase 2) | Should |

   4.2 Non-Functional Requirements

   - 모든 비기능 요구사항은 **REQ-NF-xxx** 형식의 ID를 부여하며, 성능·가용성·보안·비용·운영·비즈니스 KPI 등을 포함한다.

   | ID | Category | Requirement Description | Target / Metric | Source | Verification |
   | :--- | :--- | :--- | :--- | :--- | :--- |
   | **REQ-NF-001** | Performance | Wizard 단계 전환 서버 응답 성능 | Wizard 각 단계 전환 요청의 p95 응답시간 ≤ 800ms | PRD §4.2 성능 | 부하 테스트 및 APM 측정 |
   | **REQ-NF-002** | Performance | 문서/리포트 생성 성능 | 사업계획서 초안 생성 및 리포트 생성 요청의 p95 응답시간 ≤ 10초 | PRD §4.2 성능 | 성능 테스트 및 모니터링 대시보드 |
   | **REQ-NF-003** | Reliability / Availability | 서비스 가용성 | 월간 서비스 가용성 ≥ 99.5% (월 장애 시간 < 3.6시간) | PRD §4.2 신뢰성/가용성 | 상태 페이지 및 SLO 리포트 |
   | **REQ-NF-004** | Reliability | 주요 기능 오류율 | 문서 생성·PMF 진단·내보내기 등 주요 API의 오류율 ≤ 0.5%/월 | PRD §4.2 신뢰성/가용성 | 에러 로그 및 APM 통계 |
   | **REQ-NF-005** | Reliability | 자동 저장 주기 | 편집 중 자동 저장 주기 ≤ 10초 | PRD §4.2 신뢰성/가용성 | E2E 테스트, 로그 타임스탬프 검증 |
   | **REQ-NF-006** | Security | 저장 데이터 암호화 | 모든 사용자 데이터는 저장 시 AES-256으로 암호화되어야 한다. | PRD §4.2 보안 | 인프라 구성 점검 및 보안 감사 |
   | **REQ-NF-007** | Security | 전송 구간 암호화 | 클라이언트↔서버 및 서버↔외부 API 통신은 TLS 1.2 이상(권장: 1.3)을 사용해야 한다. | PRD §4.2 보안 | SSL/TLS 구성 검사 및 침투 테스트 |
   | **REQ-NF-008** | Security | 데이터 격리 | 고객/워크스페이스별 데이터는 논리적으로 격리되어야 하며, On-premise/Private Cloud 옵션에서는 외부로 데이터가 유출되지 않아야 한다. | PRD §4.2 보안, §6.3 Risk 4 | 데이터 아키텍처 리뷰 및 접근 제어 테스트 |
   | **REQ-NF-009** | Scalability | 동시 사용자 처리 | 최소 1,000 동시 세션에서 REQ-NF-001~002의 성능 목표를 유지해야 한다. | PRD 전반 | 부하 테스트(k6 등) 보고서 |
   | **REQ-NF-010** | Maintainability | 템플릿 추가 리드타임 | 신규 정부 사업 템플릿 추가 시, 요구사항 분석 후 3영업일 이내에 운영 환경에 반영 가능해야 한다(표준 필드는 코드 수정 없이 구성 기반). | PRD §4.2 확장성 | 변경 관리 기록 및 배포 리드타임 측정 |
   | **REQ-NF-011** | Cost | 사용자당 AI API 사용량 상한 | 사용자당 월 LLM 토큰 사용량을 100,000 토큰 이내로 제한하고 초과 시 경고 또는 업셀 플로우를 제공해야 한다. | PRD §4.2 비용 | LLM 사용량 대시보드 및 경고 로그 |
   | **REQ-NF-012** | Monitoring / Alerting | 장애 알림 기준 | 5분 단위 집계 기준 오류율 > 1% 또는 p95 응답시간 > 2초인 경우 On-call 채널(Slack/이메일)로 알림을 발송해야 한다. | PRD §4.2 모니터링 | 모니터링 시스템 알림 테스트 |
   | **REQ-NF-013** | Business KPI | Time-to-Value(TTV) | 가입 후 첫 '내보내기' 이벤트까지의 시간 중앙값 ≤ 30분 | PRD §1.3 북극성 지표 | 제품 분석 도구(코호트 분석) |
   | **REQ-NF-014** | Business KPI | Activation Rate | 가입 후 첫 세션 내 '제출 가능' 산출물 완성률 ≥ 40% | PRD §1.3 주요 결과 | 이벤트 기반 퍼널 분석 |
   | **REQ-NF-015** | Business KPI | 과제 통과율 | AI Co-Pilot 사용자의 자기신고 서류 1차 통과율 ≥ 65% | PRD §1.2, §1.3 | 인앱 설문조사 및 표본 분석 |
   | **REQ-NF-016** | Business KPI | NPS | 비전문가 사용자의 NPS ≥ 40 | PRD §1.3 | 정기 설문 및 NPS 계산 |
   | **REQ-NF-017** | Business KPI | Cross-sell Rate | 과제 수행 고객 중 학습/검증 기능(PMF 진단 등) 1회 이상 사용 비율 ≥ 10% | PRD §1.3 | 기능 사용 로그 및 코호트 분석 |
   | **REQ-NF-018** | Resilience | 백업 및 복구 목표 | RPO ≤ 1시간, RTO ≤ 4시간을 만족해야 한다. | 일반 SaaS 관행 | DR 리허설 및 복구 테스트 |

5. Traceability Matrix

   - 본 매트릭스는 PRD의 EPIC/Story/Feature와 본 SRS의 요구사항 ID 및 테스트 케이스 ID 간 추적성을 제공한다.

   | Story / Feature | Requirement ID(s) | Test Case ID(s) (예시) |
   | :--- | :--- | :--- |
   | **EPIC 1: 과제 통과 Job (To pass the test)** – AC 1.1~1.4 | REQ-FUNC-001, REQ-FUNC-002, REQ-FUNC-003, REQ-FUNC-005, REQ-FUNC-006, REQ-FUNC-007, REQ-FUNC-011, REQ-FUNC-013; REQ-NF-001, REQ-NF-002, REQ-NF-003, REQ-NF-013, REQ-NF-015 | TC-FUNC-001~003, TC-FUNC-005~007, TC-FUNC-011, TC-FUNC-013; TC-NF-001~003, TC-NF-013, TC-NF-015 |
   | **EPIC 2: 실패 회피 Job (To avoid failure)** – AC 2.1~2.3 | REQ-FUNC-008, REQ-FUNC-009, REQ-FUNC-010, REQ-FUNC-012; REQ-NF-002, REQ-NF-009, REQ-NF-017 | TC-FUNC-008~010, TC-FUNC-012; TC-NF-002, TC-NF-009, TC-NF-017 |
   | **F1: 정부/은행 제출용 한글 Wizard** | REQ-FUNC-001, REQ-FUNC-002, REQ-FUNC-003, REQ-FUNC-005, REQ-FUNC-007, REQ-FUNC-013; REQ-NF-001, REQ-NF-005 | TC-FUNC-001~003, TC-FUNC-005, TC-FUNC-007, TC-FUNC-013; TC-NF-001, TC-NF-005 |
   | **F2: 템플릿 100% 호환 + HWP/PDF 내보내기** | REQ-FUNC-003, REQ-FUNC-005, REQ-FUNC-011; REQ-NF-002, REQ-NF-010 | TC-FUNC-003, TC-FUNC-005, TC-FUNC-011; TC-NF-002, TC-NF-010 |
   | **F3: 핵심 변수 기반 재무 자동화 엔진** | REQ-FUNC-006, REQ-FUNC-007, REQ-FUNC-012, REQ-FUNC-013; REQ-NF-002 | TC-FUNC-006, TC-FUNC-007, TC-FUNC-012, TC-FUNC-013; TC-NF-002 |
   | **F4: AI 초안 생성 + 쉬운/전문가 모드** | REQ-FUNC-003, REQ-FUNC-004, REQ-FUNC-014; REQ-NF-001, REQ-NF-002 | TC-FUNC-003, TC-FUNC-004, TC-FUNC-014; TC-NF-001, TC-NF-002 |
   | **F5: PMF 진단 프레임워크 및 리포트** | REQ-FUNC-008, REQ-FUNC-009, REQ-FUNC-010; REQ-NF-002, REQ-NF-017 | TC-FUNC-008~010; TC-NF-002, TC-NF-017 |
   | **F6: On-premise/Private Cloud 옵션** | REQ-FUNC-015, REQ-FUNC-016; REQ-NF-008, REQ-NF-018 | TC-FUNC-015, TC-FUNC-016; TC-NF-008, TC-NF-018 |

6. Appendix

   6.1 API Endpoint List

   | Method | Endpoint | Description | Related Requirements |
   | :--- | :--- | :--- | :--- |
   | `POST` | `/auth/signup` | 이메일·비밀번호를 사용한 사용자 가입 | (인증/보안 요구사항, 별도 SRS 범위) |
   | `POST` | `/auth/login` | 인증 토큰 발급 | (인증/보안 요구사항, 별도 SRS 범위) |
   | `POST` | `/projects` | 새 프로젝트 생성(템플릿 선택 포함) | REQ-FUNC-001, REQ-FUNC-002 |
   | `GET` | `/projects/{id}` | 프로젝트 상세 조회 | REQ-FUNC-002, REQ-FUNC-003 |
   | `POST` | `/projects/{id}/wizard/steps` | Wizard 단계별 사용자 입력 저장 | REQ-FUNC-002, REQ-FUNC-013 |
   | `POST` | `/projects/{id}/documents/business-plan:generate` | 사업계획서 초안 생성 | REQ-FUNC-003, REQ-FUNC-004, REQ-FUNC-006 |
   | `POST` | `/projects/{id}/documents/business-plan:checklist` | 제출 가능성 체크리스트 및 보완 가이드 생성 | REQ-FUNC-005 |
   | `POST` | `/projects/{id}/documents/business-plan:consistency-check` | 재무-서술 논리 정합성 검증 | REQ-FUNC-006 |
   | `GET` | `/projects/{id}/export` | 사업계획서 HWP/PDF 파일 내보내기 | REQ-FUNC-011 |
   | `POST` | `/projects/{id}/financials:generate` | 핵심 변수 기반 재무 추정 생성 | REQ-FUNC-012 |
   | `POST` | `/projects/{id}/pmf-report:generate` | PMF 진단 리포트 생성 및 데이터 부족 처리 | REQ-FUNC-008, REQ-FUNC-010 |
   | `GET` | `/projects/{id}/unit-economics` | LTV/CAC 및 손익분기점 계산 결과 조회 | REQ-FUNC-009 |

   6.2 Entity & Data Model

   - 모든 엔터티·데이터 모델은 테이블 형태로 정의하며, 필드는 변경 가능성을 고려해 최소 필수 속성만 명시한다.

   **Entity: User**

   | Field | Type | Constraints | Description |
   | :--- | :--- | :--- | :--- |
   | `user_id` | UUID | PK, Not Null | 사용자 고유 식별자 |
   | `email` | VARCHAR(255) | Not Null, Unique | 로그인 이메일 |
   | `password_hash` | VARCHAR(255) | Not Null | 암호화된 비밀번호 |
   | `created_at` | TIMESTAMP | Not Null | 계정 생성 시각 |

   **Entity: Project**

   | Field | Type | Constraints | Description |
   | :--- | :--- | :--- | :--- |
   | `project_id` | UUID | PK, Not Null | 프로젝트 고유 식별자 |
   | `user_id` | UUID | FK(User) | 프로젝트 소유자 |
   | `template_code` | VARCHAR(100) | Not Null | 사용 템플릿 코드(예: `KSTARTUP_2025`) |
   | `status` | VARCHAR(50) | Not Null | 상태(`draft`, `ready_to_submit` 등) |
   | `wizard_answers` | JSONB | - | Wizard 단계별 사용자 응답 데이터 |
   | `created_at` | TIMESTAMP | Not Null | 생성 시각 |
   | `updated_at` | TIMESTAMP | Not Null | 마지막 수정 시각 |

   **Entity: BusinessPlanDocument**

   | Field | Type | Constraints | Description |
   | :--- | :--- | :--- | :--- |
   | `document_id` | UUID | PK, Not Null | 사업계획서 문서 식별자 |
   | `project_id` | UUID | FK(Project) | 소속 프로젝트 |
   | `sections` | JSONB | Not Null | 섹션별 내용(텍스트/메타데이터) |
   | `template_version` | VARCHAR(50) | Not Null | 사용 템플릿 버전 |
   | `mandatory_coverage` | NUMERIC(5,2) | Not Null | 필수 항목 커버리지(%) |
   | `created_at` | TIMESTAMP | Not Null | 생성 시각 |

   **Entity: FinancialModel**

   | Field | Type | Constraints | Description |
   | :--- | :--- | :--- | :--- |
   | `financial_model_id` | UUID | PK, Not Null | 재무 모델 식별자 |
   | `project_id` | UUID | FK(Project) | 소속 프로젝트 |
   | `inputs` | JSONB | Not Null | CAC, ARPU, 이탈률 등 입력 가정 |
   | `income_statement` | JSONB | Not Null | 3년치 손익계산서 데이터 |
   | `cashflow_summary` | JSONB | Not Null | 현금흐름 요약 데이터 |
   | `unit_economics` | JSONB | - | LTV, LTV/CAC, 손익분기점 등 |

   **Entity: PMFReport**

   | Field | Type | Constraints | Description |
   | :--- | :--- | :--- | :--- |
   | `pmf_report_id` | UUID | PK, Not Null | PMF 리포트 식별자 |
   | `project_id` | UUID | FK(Project) | 소속 프로젝트 |
   | `answers` | JSONB | Not Null | PMF 진단 질문 응답 데이터 |
   | `pmf_stage` | VARCHAR(50) | Not Null | PMF 단계(예: `problem_solution_fit`) |
   | `risks` | JSONB | Not Null | 핵심 리스크 목록 및 근거 |
   | `recommendations` | JSONB | Not Null | 리스크별 개선 권고 |

   **Entity: WorkspaceSecuritySetting**

   | Field | Type | Constraints | Description |
   | :--- | :--- | :--- | :--- |
   | `workspace_id` | UUID | PK, Not Null | 워크스페이스 식별자 |
   | `deployment_mode` | VARCHAR(20) | Not Null | `cloud`, `private`, `on_premise` |
   | `data_region` | VARCHAR(50) | - | 데이터 저장 리전(예: `ap-northeast-2`) |
   | `llm_endpoint` | VARCHAR(255) | - | LLM 엔드포인트 URL |
   | `logging_policy` | JSONB | - | 로그 수집·보존 정책 |

   6.3 Detailed Interaction Models (Mermaid 시퀀스 다이어그램)

   6.3.1 AC 1.1 & 1.2 – 템플릿 기반 초안 생성 및 제출 가능성 점검

   ```mermaid
   sequenceDiagram
       actor User as 사용자(김예비)
       participant Web as Web App
       participant API as Backend API
       participant LLM as LLM API
       participant CHK as Checklist Engine

       User->>Web: 템플릿(예비창업패키지) 선택
       Web->>API: POST /projects (template_code)
       API-->>Web: 프로젝트 ID 반환
       User->>Web: Wizard 질문에 답변 입력
       Web->>API: POST /projects/{id}/wizard/steps
       User->>Web: '초안 생성' 클릭
       Web->>API: POST /projects/{id}/documents/business-plan:generate
       API->>LLM: 섹션별 프롬프트 전송
       LLM-->>API: 섹션별 초안 텍스트
       API-->>Web: 초안 문서 + mandatory_coverage=100% 반환
       Web-->>User: 초안 문서 표시
       User->>Web: '제출 가능성 점검' 클릭
       Web->>API: POST /projects/{id}/documents/business-plan:checklist
       API->>CHK: 체크리스트 평가
       CHK-->>API: 항목별 ✔/✖ 및 보완 가이드
       API-->>Web: 체크리스트 및 가이드 반환
       Web-->>User: 10개 이상 항목 표시 및 90% 이상 ✔ 달성 시 '제출 가능' 상태 표시
   ```

   6.3.2 AC 1.3 & 1.4 – 재무-서술 정합성 검증 및 필수 입력 누락 처리

   ```mermaid
   sequenceDiagram
       actor User as 사용자(김예비)
       participant Web as Web App
       participant API as Backend API
       participant CONS as Consistency Engine

       User->>Web: 재무 가정 일부 입력 (월간 순이익 미입력)
       User->>Web: '완료' 클릭
       Web->>API: 완료 요청
       API-->>Web: 오류 응답("필수 항목이 누락되었습니다", missing_field="월간 순이익")
       Web-->>User: 경고 메시지 표시 및 해당 필드로 포커스 이동
       User->>Web: 모든 필수 재무 필드 입력
       User->>Web: '논리 정합성 검증' 실행
       Web->>API: POST /projects/{id}/documents-business-plan:consistency-check
       API->>CONS: 재무·시장·서술 데이터 검증
       CONS-->>API: 불일치 ≥ 3건, 각 경고 메시지·수정 제안 포함
       API-->>Web: 경고 목록 반환
       Web-->>User: 경고 및 수정 제안 표시
   ```

   6.3.3 AC 2.1~2.3 – PMF 진단 및 데이터 부족 처리

   ```mermaid
   sequenceDiagram
       actor User as 사용자(최민혁)
       participant Web as Web App
       participant API as Backend API
       participant PMF as PMF Engine

       User->>Web: PMF 질문 일부(5개 이하)만 응답
       User->>Web: 'PMF 진단하기' 클릭
       Web->>API: POST /projects/{id}/pmf-report:generate (answers n<=5)
       API->>PMF: 입력 검증
       PMF-->>API: "데이터 부족" 오류 + 필수 질문 목록 + 예상 소요시간(15분)
       API-->>Web: 오류 메시지 및 체크리스트 반환
       Web-->>User: 부족 데이터 안내 및 진단 결과 미표시

       User->>Web: 필수 질문 10개 이상 응답 완료
       User->>Web: 다시 'PMF 진단하기' 클릭
       Web->>API: POST /projects/{id}/pmf-report:generate (answers n>=10)
       API->>PMF: PMF 단계/리스크/권고 계산
       PMF-->>API: pmf_stage, risks[3+], recommendations[]
       API-->>Web: PMF 리포트 반환
       Web-->>User: 리포트 화면 렌더링
   ```