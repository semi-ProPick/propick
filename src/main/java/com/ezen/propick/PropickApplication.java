package com.ezen.propick;

import jakarta.persistence.EntityListeners;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
//@EnableJpaRepositories(basePackages = "com.ezen.propick")
//@EnableJpaAuditing //JPA Auditing 활성화
@EntityScan(basePackages = "com.ezen.propick")

public class PropickApplication {

    public static void main(String[] args) {
        SpringApplication.run(PropickApplication.class, args);
    }

}