package com.ezen.propick;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;


@SpringBootApplication
@EntityScan(basePackages = "com.ezen.propick") // 엔티티 스캔 경로 지정
//@EnableJpaRepositories(basePackages = "com.ezen.propick") // 리포지토리 스캔 경로 지정
public class PropickApplication {

    public static void main(String[] args) {
        SpringApplication.run(PropickApplication.class, args);
    }

}
