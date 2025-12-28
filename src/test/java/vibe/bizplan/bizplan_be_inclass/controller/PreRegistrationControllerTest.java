package vibe.bizplan.bizplan_be_inclass.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import vibe.bizplan.bizplan_be_inclass.dto.preregistration.*;
import vibe.bizplan.bizplan_be_inclass.dto.preregistration.PreRegistrationRequest.PlanType;
import vibe.bizplan.bizplan_be_inclass.exception.DuplicateEmailException;
import vibe.bizplan.bizplan_be_inclass.exception.GlobalExceptionHandler;
import vibe.bizplan.bizplan_be_inclass.exception.PromotionEndedException;
import vibe.bizplan.bizplan_be_inclass.exception.ResourceNotFoundException;
import vibe.bizplan.bizplan_be_inclass.service.PreRegistrationService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * PreRegistrationController 단위 테스트
 * MockMvc를 사용한 컨트롤러 레이어 테스트
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PreRegistrationController 테스트")
class PreRegistrationControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private PreRegistrationService service;

    @InjectMocks
    private PreRegistrationController controller;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();  // LocalDateTime 등 Java 8 모듈 지원
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/pre-registrations - 성공")
    void register_success() throws Exception {
        // given
        PreRegistrationRequest request = createTestRequest();
        PreRegistrationResponse response = createTestResponse();
        given(service.register(any())).willReturn(response);
        
        // when & then
        mockMvc.perform(post("/api/v1/pre-registrations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.discountCode").value("MR2026-ABC123"))
                .andExpect(jsonPath("$.data.discountRate").value(30))
                .andExpect(jsonPath("$.data.selectedPlan").value("pro"));
    }

    @Test
    @DisplayName("POST /api/v1/pre-registrations - 이메일 중복")
    void register_duplicateEmail() throws Exception {
        // given
        PreRegistrationRequest request = createTestRequest();
        given(service.register(any())).willThrow(
                new DuplicateEmailException("이미 등록된 이메일입니다: test@example.com"));
        
        // when & then
        mockMvc.perform(post("/api/v1/pre-registrations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("DUPLICATE_EMAIL"));
    }

    @Test
    @DisplayName("POST /api/v1/pre-registrations - 프로모션 종료")
    void register_promotionEnded() throws Exception {
        // given
        PreRegistrationRequest request = createTestRequest();
        given(service.register(any())).willThrow(
                new PromotionEndedException("프로모션이 종료되었습니다."));
        
        // when & then
        mockMvc.perform(post("/api/v1/pre-registrations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isGone())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("PROMOTION_ENDED"));
    }

    @Test
    @DisplayName("POST /api/v1/pre-registrations - 유효성 검사 실패 (빈 이름)")
    void register_validationFail_emptyName() throws Exception {
        // given
        PreRegistrationRequest request = PreRegistrationRequest.builder()
                .name("")  // 빈 이름
                .email("test@example.com")
                .phone("010-1234-5678")
                .selectedPlan(PlanType.pro)
                .agreeTerms(true)
                .build();
        
        // when & then
        mockMvc.perform(post("/api/v1/pre-registrations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("POST /api/v1/pre-registrations - 유효성 검사 실패 (잘못된 이메일)")
    void register_validationFail_invalidEmail() throws Exception {
        // given
        PreRegistrationRequest request = PreRegistrationRequest.builder()
                .name("테스트")
                .email("invalid-email")  // 잘못된 이메일
                .phone("010-1234-5678")
                .selectedPlan(PlanType.pro)
                .agreeTerms(true)
                .build();
        
        // when & then
        mockMvc.perform(post("/api/v1/pre-registrations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("GET /api/v1/pre-registrations/check-email - 이메일 존재")
    void checkEmail_exists() throws Exception {
        // given
        EmailCheckResponse response = EmailCheckResponse.builder()
                .exists(true)
                .discountCode("MR2026-ABC123")
                .build();
        given(service.checkEmail("test@example.com")).willReturn(response);
        
        // when & then
        mockMvc.perform(get("/api/v1/pre-registrations/check-email")
                .param("email", "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.exists").value(true))
                .andExpect(jsonPath("$.data.discountCode").value("MR2026-ABC123"));
    }

    @Test
    @DisplayName("GET /api/v1/pre-registrations/check-email - 이메일 미존재")
    void checkEmail_notExists() throws Exception {
        // given
        EmailCheckResponse response = EmailCheckResponse.builder()
                .exists(false)
                .build();
        given(service.checkEmail(anyString())).willReturn(response);
        
        // when & then
        mockMvc.perform(get("/api/v1/pre-registrations/check-email")
                .param("email", "new@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.exists").value(false));
    }

    @Test
    @DisplayName("GET /api/v1/pre-registrations/{id} - 성공")
    void getById_success() throws Exception {
        // given
        PreRegistrationResponse response = createTestResponse();
        given(service.getById("test-uuid")).willReturn(response);
        
        // when & then
        mockMvc.perform(get("/api/v1/pre-registrations/test-uuid"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.discountCode").value("MR2026-ABC123"));
    }

    @Test
    @DisplayName("GET /api/v1/pre-registrations/{id} - 실패 (없는 ID)")
    void getById_notFound() throws Exception {
        // given
        given(service.getById(anyString())).willThrow(
                new ResourceNotFoundException("등록 정보를 찾을 수 없습니다"));
        
        // when & then
        mockMvc.perform(get("/api/v1/pre-registrations/invalid-uuid"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("NOT_FOUND"));
    }

    @Test
    @DisplayName("GET /api/v1/promotions/current - 성공")
    void getPromotionInfo_success() throws Exception {
        // given
        PromotionInfoResponse response = createPromotionInfoResponse();
        given(service.getPromotionInfo()).willReturn(response);
        
        // when & then
        mockMvc.perform(get("/api/v1/promotions/current"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.isActive").value(true))
                .andExpect(jsonPath("$.data.currentPhase").value("A"))
                .andExpect(jsonPath("$.data.discountRate").value(30))
                .andExpect(jsonPath("$.data.prices.pro.original").value(799000));
    }

    // ========== Helper Methods ==========

    private PreRegistrationRequest createTestRequest() {
        return PreRegistrationRequest.builder()
                .name("테스트")
                .email("test@example.com")
                .phone("010-1234-5678")
                .selectedPlan(PlanType.pro)
                .agreeTerms(true)
                .agreeMarketing(false)
                .build();
    }

    private PreRegistrationResponse createTestResponse() {
        return PreRegistrationResponse.builder()
                .id("test-uuid")
                .discountCode("MR2026-ABC123")
                .discountRate(30)
                .selectedPlan("pro")
                .originalPrice(799000)
                .discountedPrice(559300)
                .savedAmount(239700)
                .registeredAt(LocalDateTime.now())
                .status("CONFIRMED")
                .build();
    }

    private PromotionInfoResponse createPromotionInfoResponse() {
        Map<String, PromotionInfoResponse.PriceInfo> prices = new HashMap<>();
        prices.put("plus", PromotionInfoResponse.PriceInfo.builder()
                .original(399000).discounted(279300).saved(119700).build());
        prices.put("pro", PromotionInfoResponse.PriceInfo.builder()
                .original(799000).discounted(559300).saved(239700).build());
        prices.put("premium", PromotionInfoResponse.PriceInfo.builder()
                .original(1499000).discounted(1049300).saved(449700).build());

        return PromotionInfoResponse.builder()
                .isActive(true)
                .currentPhase("A")
                .discountRate(30)
                .phaseAEnd(LocalDateTime.now().plusDays(5))
                .phaseBEnd(LocalDateTime.now().plusMonths(2))
                .prices(prices)
                .build();
    }
}
