package vibe.bizplan.bizplan_be_inclass.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 템플릿 서비스
 * 하드코딩된 템플릿 목록을 관리합니다.
 * 
 * @see <a href="https://github.com/wild-mental/bizplan-be-inclass/issues/2">GitHub Issue #2</a>
 */
@Service
public class TemplateService {

    /**
     * 지원되는 템플릿 목록 (하드코딩)
     * MVP 단계에서는 DB 대신 메모리에서 관리합니다.
     * 
     * @see PRE-SUB-FUNC-002.md Section 4.1 - 프로젝트 생성
     */
    private static final List<Template> TEMPLATES = List.of(
        new Template("pre-startup", "예비창업패키지", "2단계 자금 구조 (1단계 2천만 + 2단계 4천만)"),
        new Template("early-startup", "초기창업패키지", "매칭펀드 (정부 70% + 자부담 30%)"),
        new Template("policy-fund", "정책자금지원", "대출형 정책자금"),
        // 기존 호환성을 위한 템플릿
        new Template("KSTARTUP_2025", "예비창업패키지", "중소벤처기업부 예비창업패키지 양식"),
        new Template("BANK_LOAN_2025", "은행 대출용 사업계획서", "시중은행 창업대출 심사용 양식"),
        new Template("IR_PITCH_2025", "투자유치용 IR 자료", "시드/시리즈 A 투자유치용 양식")
    );

    /**
     * 전체 템플릿 목록 조회
     * 
     * @return 지원되는 모든 템플릿 목록
     */
    public List<Template> getAllTemplates() {
        return TEMPLATES;
    }

    /**
     * 템플릿 코드 유효성 검증
     * 
     * @param code 검증할 템플릿 코드
     * @return 유효한 템플릿 코드이면 true
     */
    public boolean isValidTemplate(String code) {
        return TEMPLATES.stream()
                .anyMatch(t -> t.getCode().equals(code));
    }

    /**
     * 코드로 템플릿 조회
     * 
     * @param code 조회할 템플릿 코드
     * @return 템플릿 정보 (Optional)
     */
    public Optional<Template> getTemplate(String code) {
        return TEMPLATES.stream()
                .filter(t -> t.getCode().equals(code))
                .findFirst();
    }

    /**
     * 템플릿 정보를 담는 내부 클래스
     */
    @Getter
    @RequiredArgsConstructor
    public static class Template {
        private final String code;
        private final String name;
        private final String description;
    }
}

