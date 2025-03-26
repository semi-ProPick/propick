package com.ezen.propick;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.ezen.propick")
@EnableJpaAuditing //JPA Auditing 활성화
public class PropickApplication {

    public static void main(String[] args) {
        SpringApplication.run(PropickApplication.class, args);
    }

}
