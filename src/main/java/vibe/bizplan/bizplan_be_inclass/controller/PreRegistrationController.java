package vibe.bizplan.bizplan_be_inclass.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vibe.bizplan.bizplan_be_inclass.dto.ApiResponse;
import vibe.bizplan.bizplan_be_inclass.dto.preregistration.*;
import vibe.bizplan.bizplan_be_inclass.service.PreRegistrationService;

/**
 * 사전 등록 프로모션 API Controller
 * 
 * Rule 304: REST API Design Standards
 * Rule 305: Swagger Documentation
 * Rule 306: Controller Layer - HTTP 요청/응답 처리만 담당
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Pre-Registration", description = "사전 등록 프로모션 API")
public class PreRegistrationController {

    private final PreRegistrationService preRegistrationService;

    /**
     * 사전 등록 신청
     * 
     * @param request 사전 등록 요청 DTO
     * @return 사전 등록 응답 (할인 코드 포함)
     */
    @PostMapping("/pre-registrations")
    @Operation(
        summary = "사전 등록 신청", 
        description = "사전 등록을 신청하고 할인 코드를 발급받습니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201", 
            description = "등록 성공"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400", 
            description = "입력값 오류"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "409", 
            description = "이메일 중복"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "410", 
            description = "프로모션 종료"
        )
    })
    public ResponseEntity<ApiResponse<PreRegistrationResponse>> register(
            @Valid @RequestBody PreRegistrationRequest request) {
        
        log.info("POST /api/v1/pre-registrations - email: {}", request.getEmail());
        PreRegistrationResponse response = preRegistrationService.register(request);
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

    /**
     * 이메일 중복 체크
     * 
     * @param email 확인할 이메일 주소
     * @return 이메일 존재 여부
     */
    @GetMapping("/pre-registrations/check-email")
    @Operation(
        summary = "이메일 중복 체크", 
        description = "이메일 등록 여부를 확인합니다."
    )
    public ResponseEntity<ApiResponse<EmailCheckResponse>> checkEmail(
            @Parameter(description = "확인할 이메일 주소") 
            @RequestParam String email) {
        
        log.info("GET /api/v1/pre-registrations/check-email - email: {}", email);
        EmailCheckResponse response = preRegistrationService.checkEmail(email);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 등록 정보 조회
     * 
     * @param id 등록 ID (UUID)
     * @return 사전 등록 정보
     */
    @GetMapping("/pre-registrations/{id}")
    @Operation(
        summary = "등록 정보 조회", 
        description = "등록 ID로 사전 등록 정보를 조회합니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "조회 성공"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404", 
            description = "등록 정보 없음"
        )
    })
    public ResponseEntity<ApiResponse<PreRegistrationResponse>> getById(
            @Parameter(description = "등록 ID (UUID)") 
            @PathVariable String id) {
        
        log.info("GET /api/v1/pre-registrations/{}", id);
        PreRegistrationResponse response = preRegistrationService.getById(id);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 할인 코드로 조회
     * 
     * @param discountCode 할인 코드
     * @return 사전 등록 정보
     */
    @GetMapping("/pre-registrations/code/{discountCode}")
    @Operation(
        summary = "할인 코드로 조회", 
        description = "할인 코드로 사전 등록 정보를 조회합니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "조회 성공"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404", 
            description = "유효하지 않은 할인 코드"
        )
    })
    public ResponseEntity<ApiResponse<PreRegistrationResponse>> getByDiscountCode(
            @Parameter(description = "할인 코드") 
            @PathVariable String discountCode) {
        
        log.info("GET /api/v1/pre-registrations/code/{}", discountCode);
        PreRegistrationResponse response = preRegistrationService.getByDiscountCode(discountCode);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 현재 프로모션 정보 조회
     * 
     * @return 현재 활성화된 프로모션 정보와 할인가
     */
    @GetMapping("/promotions/current")
    @Operation(
        summary = "현재 프로모션 정보", 
        description = "현재 활성화된 프로모션 정보와 요금제별 할인가를 조회합니다."
    )
    public ResponseEntity<ApiResponse<PromotionInfoResponse>> getPromotionInfo() {
        
        log.info("GET /api/v1/promotions/current");
        PromotionInfoResponse response = preRegistrationService.getPromotionInfo();
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}

