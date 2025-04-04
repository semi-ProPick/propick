package com.ezen.propick;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.ezen.propick")
public class PropickApplication {

    public static void main(String[] args) {
        SpringApplication.run(PropickApplication.class, args);
    }

}
