package vibe.bizplan.bizplan_be_inclass.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger/OpenAPI 설정 클래스
 * 
 * API 문서화를 위한 OpenAPI 3.0 설정을 정의합니다.
 * 
 * Swagger UI 접속: http://localhost:8080/swagger-ui.html
 * OpenAPI JSON: http://localhost:8080/v3/api-docs
 * 
 * @see <a href="https://springdoc.org/">SpringDoc OpenAPI</a>
 */
@Configuration
public class SwaggerConfig {

    /**
     * OpenAPI 설정 빈
     * 
     * @return OpenAPI 설정 객체
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local Development Server")
                ))
                .tags(List.of(
                        new Tag()
                                .name("Projects")
                                .description("프로젝트 관리 API - 프로젝트 생성 및 템플릿 조회"),
                        new Tag()
                                .name("Business Plan")
                                .description("사업계획서 API - 사업계획서 생성 및 제출 관리")
                ));
    }

    /**
     * API 기본 정보 설정
     * 
     * @return Info 객체
     */
    private Info apiInfo() {
        return new Info()
                .title("BizPlan API")
                .description("""
                        ## AI Co-Pilot for First-time Founders
                        
                        사업계획서 작성을 돕는 AI 기반 플랫폼의 Backend API 입니다.
                        
                        ### 주요 기능
                        - **프로젝트 관리**: 프로젝트 생성 및 템플릿 조회
                        - **사업계획서 생성**: AI 기반 사업계획서 자동 생성
                        - **제출 관리**: 사업계획서 제출 및 상태 조회
                        
                        ### API 버전
                        - 현재 버전: v1
                        - Base Path: `/api/v1`
                        """)
                .version("1.0.0")
                .contact(new Contact()
                        .name("BizPlan Team")
                        .email("support@bizplan.vibe")
                        .url("https://github.com/wild-mental/bizplan-be-inclass"))
                .license(new License()
                        .name("MIT License")
                        .url("https://opensource.org/licenses/MIT"));
    }
}
