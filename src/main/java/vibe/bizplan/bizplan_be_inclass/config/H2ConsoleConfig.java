package vibe.bizplan.bizplan_be_inclass.config;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * H2 콘솔 설정
 * 로컬 개발 환경에서만 활성화됩니다.
 * 
 * Spring Boot 4.0에서는 H2 콘솔 자동 설정이 제거되었으므로 수동으로 등록합니다.
 */
@Configuration
@Profile("local")
public class H2ConsoleConfig {

    /**
     * H2 콘솔 서블릿 등록
     * H2 2.x 버전에서는 JakartaWebServlet을 사용합니다.
     * 
     * @return H2 콘솔 서블릿 등록 빈
     */
    @Bean
    @SuppressWarnings("unchecked")
    public ServletRegistrationBean<?> h2ConsoleServletRegistration() {
        try {
            // H2 2.x 버전의 JakartaWebServlet 사용 시도
            Class<?> servletClass = Class.forName("org.h2.server.web.JakartaWebServlet");
            ServletRegistrationBean<?> registration = 
                new ServletRegistrationBean<>((jakarta.servlet.Servlet) servletClass.getDeclaredConstructor().newInstance(), "/h2-console/*");
            registration.setName("H2Console");
            registration.addInitParameter("webAllowOthers", "false");
            registration.addInitParameter("trace", "false");
            return registration;
        } catch (Exception e) {
            // H2 1.x 버전의 WebServlet 사용 시도
            try {
                Class<?> servletClass = Class.forName("org.h2.server.web.WebServlet");
                ServletRegistrationBean<?> registration = 
                    new ServletRegistrationBean<>((jakarta.servlet.Servlet) servletClass.getDeclaredConstructor().newInstance(), "/h2-console/*");
                registration.setName("H2Console");
                registration.addInitParameter("webAllowOthers", "false");
                registration.addInitParameter("trace", "false");
                return registration;
            } catch (Exception ex) {
                throw new RuntimeException("H2 콘솔 서블릿을 찾을 수 없습니다. H2 의존성을 확인하세요.", ex);
            }
        }
    }
}

