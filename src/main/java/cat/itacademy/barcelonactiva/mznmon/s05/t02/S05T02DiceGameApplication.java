package cat.itacademy.barcelonactiva.mznmon.s05.t02;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class S05T02DiceGameApplication {

	public static void main(String[] args) {
		SpringApplication.run(S05T02DiceGameApplication.class, args);
	}

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
				.info(new Info()
						.title("Dice Game API")
						.version("0.1")
						.description("API with Spring Boot to manage a Dice Game")
						.termsOfService("http://swagger.io/terms")
						.license(new License().name("Apache 2.0")
								.url("http://springdoc.org")
						));
	}

}
