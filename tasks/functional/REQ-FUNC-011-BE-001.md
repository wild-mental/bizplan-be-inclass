# REQ-FUNC-011-BE-001: HWP/PDF 내보내기 서비스 구현

## 1. 개요
- **목표**: 저장된 사업계획서 데이터를 템플릿 파일(.hwp, .docx)에 병합(Merge)하여 다운로드 가능한 파일을 생성한다.
- **범위**:
  - HWP 라이브러리(kr.dogfoot.hwplib 등) 또는 변환 솔루션 연동
  - PDF 변환 (LibreOffice, wkhtmltopdf 등 활용)
  - `GET /projects/{id}/export` API
- **Out of Scope**: 완벽한 스타일링(초안 수준 포맷팅), 표/이미지 삽입(텍스트 위주).

## 2. 상세 요구사항
- **HWP 필드 매핑**: 미리 준비된 HWP 템플릿의 누름틀(Field)에 데이터를 삽입한다.
- **PDF 생성**: HTML 템플릿 렌더링 후 PDF로 변환하거나, HWP를 PDF로 변환한다.

---

```yaml
task_id: "REQ-FUNC-011-BE-001"
title: "사업계획서 HWP/PDF 내보내기 기능 구현"
summary: >
  데이터와 HWP 템플릿(누름틀)을 병합하여 다운로드 가능한 파일을 생성한다.
type: "functional"
epic: "EPIC_1_PASS_THE_TEST"
req_ids: ["REQ-FUNC-011"]
component: ["backend.core"]
agent_profile: ["backend"]

inputs:
  description: "문서 데이터 및 포맷 요청"
  fields:
    - name: "document_data"
      type: "object"
    - name: "format"
      type: "enum" # hwp, pdf

outputs:
  description: "바이너리 파일 스트림"
  success:
    content_type: "application/octet-stream"

steps_hint:
  - "HWP 템플릿 파일 준비 및 누름틀 정의"
  - "Java HWP 라이브러리(hwplib) 의존성 추가"
  - "ExportService: 데이터-누름틀 매핑 및 파일 생성"
  - "Controller: 파일 다운로드 응답 처리"

preconditions:
  - "서버에 폰트 및 템플릿 파일이 배포되어 있어야 한다."

postconditions:
  - "생성된 HWP 파일을 열었을 때 텍스트가 올바른 위치에 들어가 있어야 한다."

dependencies: ["REQ-FUNC-003-BE-001"]
```

