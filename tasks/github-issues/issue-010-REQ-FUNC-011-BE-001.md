# [#010] 사업계획서 HWP/PDF 내보내기 기능 구현

## Labels
`epic:EPIC_1_PASS_THE_TEST`, `type:backend`, `component:core`, `priority:Must`, `effort:L`

## Description
저장된 사업계획서 데이터를 템플릿 파일(.hwp, .docx)에 병합(Merge)하여 다운로드 가능한 파일을 생성합니다.

## Scope
- HWP 라이브러리(kr.dogfoot.hwplib 등) 또는 변환 솔루션 연동
- PDF 변환 (LibreOffice, wkhtmltopdf 등 활용)
- `GET /projects/{id}/export` API

### Out of Scope
- 완벽한 스타일링(초안 수준 포맷팅)
- 표/이미지 삽입(텍스트 위주)

## Requirements
- **HWP 필드 매핑**: 미리 준비된 HWP 템플릿의 누름틀(Field)에 데이터 삽입
- **PDF 생성**: HTML 템플릿 렌더링 후 PDF로 변환하거나, HWP를 PDF로 변환

## Technical Stack
- Java 21 + Spring Boot 4.0.0
- kr.dogfoot.hwplib (HWP 라이브러리)
- Apache PDFBox 또는 wkhtmltopdf

## API Specification

### GET /projects/{id}/export?format=hwp
**Response (200 OK):**
```
Content-Type: application/octet-stream
Content-Disposition: attachment; filename="business-plan.hwp"

[Binary File Stream]
```

### GET /projects/{id}/export?format=pdf
**Response (200 OK):**
```
Content-Type: application/pdf
Content-Disposition: attachment; filename="business-plan.pdf"

[Binary File Stream]
```

## Implementation Steps
1. HWP 템플릿 파일 준비 및 누름틀 정의
2. Java HWP 라이브러리(hwplib) 의존성 추가
3. ExportService: 데이터-누름틀 매핑 및 파일 생성
4. Controller: 파일 다운로드 응답 처리
5. PDF 변환 로직 구현

## Acceptance Criteria
- [ ] 서버에 폰트 및 템플릿 파일이 배포되어 있음
- [ ] 생성된 HWP 파일을 열었을 때 텍스트가 올바른 위치에 들어가 있음
- [ ] PDF 파일이 정상적으로 생성되고 다운로드됨

## Dependencies
- #009 (REQ-FUNC-003-BE-001)

## Related Requirements
REQ-FUNC-011

