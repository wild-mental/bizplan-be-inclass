# 🎓 Cursor AI와 Flyway로 SQLite 데이터베이스 제어

## 1. 개요
* **Flyway:** DB계의 '타임머신'. 누가 무엇을 바꿨는지 기록하고, 팀원 모두의 DB 상태를 똑같이 맞춰줍니다.
* **AI 활용:** SQLite의 기술적 한계(ALTER TABLE 제약 등)를 우리가 고민하지 않고, AI에게 "구조를 바꿔줘"라고 명령만 내려서 마이그레이션 파일을 생성합니다.

---

## 2. 사전 준비 (Setup)

### 1단계: 의존성 추가 (build.gradle)

비개발자분들도 이 부분은 복사-붙여넣기만 하면 된다고 안내해 주세요.

```gradle
dependencies {
    implementation 'org.flywaydb:flyway-core'
    // SQLite 환경에서는 아래 의존성이 추가로 필요할 수 있습니다.
    implementation 'org.flywaydb:flyway-database-sqlite'
}

```

### 2단계: 설정 (application.yml)

스프링의 자동 생성 기능(`ddl-auto`)을 끄고 Flyway를 활성화합니다.

```yaml
spring:
  datasource:
    url: jdbc:sqlite:./mydata.db
    driver-class-name: org.sqlite.JDBC
  jpa:
    hibernate:
      ddl-auto: validate # Flyway가 만든 구조와 코드가 맞는지 검사만 함
  flyway:
    enabled: true
    baseline-on-migrate: true # 기존 데이터가 있어도 시작할 수 있게 함

```

---

## 3. 실습: AI로 마이그레이션 제어하기

### [시나리오 1: 첫 테이블 생성 (V1)]

1. **AI에게 명령하기 (Cursor Composer - `Ctrl + I`):**
> "@docs (혹은 프로젝트 파일 선택) 스프링 부트 엔티티인 `User` 클래스를 보고 SQLite용 Flyway 마이그레이션 파일을 만들어줘. 파일명은 `V1__init_user_table.sql`로 해주고 위치는 `src/main/resources/db/migration`이야."


2. **검토 및 적용:** AI가 생성한 SQL을 보고 `Y`를 눌러 저장한 뒤, 애플리케이션을 실행합니다. 콘솔에 `Successfully applied 1 migration`이 뜨는 것을 확인합니다.

### [시나리오 2: SQLite의 한계 돌파 (컬럼 삭제 및 변경 - V2)]

SQLite는 `ALTER TABLE DROP COLUMN`이 잘 안 됩니다. 하지만 AI에게는 복잡하지 않습니다.

1. **AI에게 명령하기:**
> "현재 `User` 테이블에서 `age` 컬럼을 삭제하고 싶어. SQLite는 컬럼 삭제가 직접 안 되니까, **새 임시 테이블 생성 -> 데이터 복사 -> 기존 테이블 삭제 -> 이름 변경** 방식으로 `V2__remove_age_column.sql` 파일을 작성해줘."


2. **결과 확인:** AI가 SQLite 전용 우회 쿼리를 완벽하게 작성해 줍니다. 우리는 그저 실행만 하면 됩니다.

---

## 4. 데이터 확인: AI에게 물어보기 (Workbench 탈피)

별도의 DB 툴 없이 Cursor 내에서 데이터를 확인하는 법을 가르칩니다.

1. **확장 프로그램:** `SQLite Viewer` 설치 안내 (클릭 한 번으로 표 보기).
2. **AI 데이터 분석:**
* 채팅창(`Ctrl + L`)에 `@mydata.db` 파일을 첨부합니다.
* **질문:** "지금 가입한 유저들 중에서 이메일이 naver.com인 사람만 리스트업 해줘."
* **결과:** AI가 내부적으로 SQL을 실행하여 결과를 대화형으로 알려줍니다.



---

## 5. 비개발자를 위한 "AI 프롬프트" 치트시트

학생들이 실습 때 복사해서 쓸 수 있게 제공하세요.

* **테이블 추가할 때:** "새로운 `Post` 엔티티를 만들었어. 이에 맞는 Flyway 새 버전 마이그레이션 파일을 생성해줘."
* **관계 설정할 때:** "User와 Post 테이블을 1:N 관계로 연결하는 외래키 설정을 추가한 새 버전 마이그레이션 파일을 만들어줘."
* **에러 발생 시:** "[에러로그 복사] 이 에러가 났어. 마이그레이션 파일의 문법이 SQLite와 맞는지 확인하고 수정해줘."