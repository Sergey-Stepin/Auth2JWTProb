package dev.steps.jwt3ssoclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class Jwt3SsoClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(Jwt3SsoClientApplication.class, args);
    }

}
