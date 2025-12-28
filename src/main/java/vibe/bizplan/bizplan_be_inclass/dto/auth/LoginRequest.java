package vibe.bizplan.bizplan_be_inclass.dto.auth;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * 로그인 요청 DTO
 * 
 * @see PRE-SUB-FUNC-002.md Section 2.3 - 로그인
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {

    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "유효한 이메일을 입력해주세요")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다")
    private String password;
}

