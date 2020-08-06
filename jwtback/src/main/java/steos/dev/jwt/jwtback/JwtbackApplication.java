package steos.dev.jwt.jwtback;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class JwtbackApplication {

	public static void main(String[] args) {
		SpringApplication.run(JwtbackApplication.class, args);
	}

}
