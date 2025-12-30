package vibe.bizplan.bizplan_be_inclass.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * 비밀번호 재설정 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordResetRequest {

    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "유효한 이메일을 입력해주세요")
    @Size(max = 100, message = "이메일은 100자 이내여야 합니다")
    private String email;
}

