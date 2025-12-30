package vibe.bizplan.bizplan_be_inclass.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

/**
 * 이메일 발송 서비스
 * 이메일 인증 및 비밀번호 재설정 이메일을 발송합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from:noreply@send.makersround.world}")
    private String fromEmail;

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    /**
     * 이메일 인증 메일 발송
     * 
     * @param to 수신자 이메일
     * @param token 인증 토큰
     * @param userName 사용자 이름
     */
    public void sendVerificationEmail(String to, String token, String userName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String verificationUrl = frontendUrl + "/verify-email?token=" + token;

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("[BizPlan] 이메일 인증을 완료해주세요");

            // HTML 이메일 본문
            String htmlContent = buildVerificationEmailHtml(userName, verificationUrl);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("이메일 인증 메일 발송 완료: {}", to);
        } catch (MessagingException e) {
            log.error("이메일 인증 메일 발송 실패: {}", to, e);
            throw new RuntimeException("이메일 발송에 실패했습니다", e);
        }
    }

    /**
     * 비밀번호 재설정 메일 발송
     * 
     * @param to 수신자 이메일
     * @param token 재설정 토큰
     * @param userName 사용자 이름
     */
    public void sendPasswordResetEmail(String to, String token, String userName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String resetUrl = frontendUrl + "/reset-password?token=" + token;

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("[BizPlan] 비밀번호 재설정 안내");

            // HTML 이메일 본문
            String htmlContent = buildPasswordResetEmailHtml(userName, resetUrl);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("비밀번호 재설정 메일 발송 완료: {}", to);
        } catch (MessagingException e) {
            log.error("비밀번호 재설정 메일 발송 실패: {}", to, e);
            throw new RuntimeException("이메일 발송에 실패했습니다", e);
        }
    }

    /**
     * 이메일 인증 HTML 템플릿 생성
     */
    private String buildVerificationEmailHtml(String userName, String verificationUrl) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #4F46E5; color: white; padding: 20px; text-align: center; border-radius: 5px 5px 0 0; }
                    .content { background-color: #f9f9f9; padding: 30px; border-radius: 0 0 5px 5px; }
                    .button { display: inline-block; padding: 12px 30px; background-color: #4F46E5; color: white; text-decoration: none; border-radius: 5px; margin: 20px 0; }
                    .footer { text-align: center; margin-top: 20px; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>BizPlan 이메일 인증</h1>
                    </div>
                    <div class="content">
                        <p>안녕하세요, %s님!</p>
                        <p>BizPlan에 가입해주셔서 감사합니다. 아래 버튼을 클릭하여 이메일 인증을 완료해주세요.</p>
                        <p style="text-align: center;">
                            <a href="%s" class="button">이메일 인증하기</a>
                        </p>
                        <p>버튼이 작동하지 않는 경우, 아래 링크를 복사하여 브라우저에 붙여넣으세요:</p>
                        <p style="word-break: break-all; color: #4F46E5;">%s</p>
                        <p style="color: #666; font-size: 12px;">이 링크는 24시간 동안 유효합니다.</p>
                    </div>
                    <div class="footer">
                        <p>이 메일은 자동으로 발송된 메일입니다.</p>
                        <p>&copy; 2025 BizPlan. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(userName, verificationUrl, verificationUrl);
    }

    /**
     * 비밀번호 재설정 HTML 템플릿 생성
     */
    private String buildPasswordResetEmailHtml(String userName, String resetUrl) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #DC2626; color: white; padding: 20px; text-align: center; border-radius: 5px 5px 0 0; }
                    .content { background-color: #f9f9f9; padding: 30px; border-radius: 0 0 5px 5px; }
                    .button { display: inline-block; padding: 12px 30px; background-color: #DC2626; color: white; text-decoration: none; border-radius: 5px; margin: 20px 0; }
                    .footer { text-align: center; margin-top: 20px; color: #666; font-size: 12px; }
                    .warning { background-color: #FEF2F2; border-left: 4px solid #DC2626; padding: 15px; margin: 20px 0; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>비밀번호 재설정 안내</h1>
                    </div>
                    <div class="content">
                        <p>안녕하세요, %s님!</p>
                        <p>비밀번호 재설정을 요청하셨습니다. 아래 버튼을 클릭하여 새 비밀번호를 설정해주세요.</p>
                        <div class="warning">
                            <strong>주의:</strong> 본인이 요청하지 않은 경우, 이 메일을 무시하셔도 됩니다.
                        </div>
                        <p style="text-align: center;">
                            <a href="%s" class="button">비밀번호 재설정하기</a>
                        </p>
                        <p>버튼이 작동하지 않는 경우, 아래 링크를 복사하여 브라우저에 붙여넣으세요:</p>
                        <p style="word-break: break-all; color: #DC2626;">%s</p>
                        <p style="color: #666; font-size: 12px;">이 링크는 1시간 동안 유효합니다.</p>
                    </div>
                    <div class="footer">
                        <p>이 메일은 자동으로 발송된 메일입니다.</p>
                        <p>&copy; 2025 BizPlan. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(userName, resetUrl, resetUrl);
    }
}

