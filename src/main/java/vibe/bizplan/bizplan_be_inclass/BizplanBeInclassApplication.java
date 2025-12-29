package vibe.bizplan.bizplan_be_inclass;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BizplanBeInclassApplication {

	public static void main(String[] args) {
		// .env 파일에서 환경 변수 로드
		// 프로젝트 루트 디렉토리의 .env 파일을 읽어서 시스템 환경 변수로 설정
		try {
			Dotenv dotenv = Dotenv.configure()
					.directory("./")  // 현재 작업 디렉토리 (프로젝트 루트에서 실행 시)
					.ignoreIfMissing()  // .env 파일이 없어도 에러 발생하지 않음
					.load();
			
			// .env 파일의 변수들을 시스템 환경 변수로 설정
			// (이미 환경 변수로 설정된 값이 있으면 덮어쓰지 않음)
			dotenv.entries().forEach(entry -> {
				String key = entry.getKey();
				String value = entry.getValue();
				// 이미 시스템 환경 변수에 설정되어 있지 않은 경우에만 설정
				if (System.getenv(key) == null && System.getProperty(key) == null) {
					System.setProperty(key, value);
				}
			});
		} catch (Exception e) {
			// .env 파일 로드 실패 시에도 애플리케이션은 계속 실행
			// 환경 변수가 이미 설정되어 있거나 다른 방법으로 주입될 수 있음
			System.err.println("Warning: Failed to load .env file: " + e.getMessage());
		}
		
		SpringApplication.run(BizplanBeInclassApplication.class, args);
	}

}
