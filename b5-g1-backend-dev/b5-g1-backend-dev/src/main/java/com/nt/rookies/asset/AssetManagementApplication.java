package com.nt.rookies.asset;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
//		(exclude = {
//		HibernateJpaAutoConfiguration.class})
public class AssetManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(AssetManagementApplication.class, args);
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
						.allowedMethods(HttpMethod.OPTIONS.name(),
								HttpMethod.DELETE.name(),
								HttpMethod.PUT.name(),
								HttpMethod.PATCH.name(),
								HttpMethod.GET.name(),
								HttpMethod.POST.name())
						.maxAge(31536000)
						.allowCredentials(false)
						.allowedOrigins("*")
						.allowedHeaders("*");
			}
		};
	}
}
