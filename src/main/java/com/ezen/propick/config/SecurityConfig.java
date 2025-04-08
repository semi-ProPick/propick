package com.ezen.propick.config;

import com.ezen.propick.config.handler.AuthFailHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private AuthFailHandler authFailHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }


    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> {
            // 로그인, 회원가입, 실패 페이지, 메인 페이지는 모두 허용
            auth.requestMatchers(
                    "/**",
                    "/survey_start",
                    "/survey",
                    "/login",
                    "/user/login",
                    "/user/join",
                    "/api/surveys/**",
                    "/api/survey-responses/**",
                    "/api/recommendations/**",
                    "/api/satisfaction/**",
                    "/survey_mypage",
                    "/bookmark/add",
                    "/bookmark/remove/**"
            ).permitAll();

            auth.anyRequest().authenticated();  // 모든 요청은 인증을 요구
        }).formLogin(login -> {
            login.loginPage("/user/login");
            login.loginProcessingUrl("/user/doLogin");


            // ✅ redirect 파라미터 처리 (핵심 부분!)
            login.successHandler((request, response, authentication) -> {
                String redirect = request.getParameter("redirect");
                if (redirect == null || redirect.isBlank()) {
                    redirect = "/";  // 기본 이동 경로
                }
                response.sendRedirect(redirect);  // 로그인 성공 후 리디렉트
            });

            login.failureHandler(authFailHandler);  // 로그인 실패 시 처리할 핸들러
        }).logout(logout -> {
            logout.logoutRequestMatcher(new AntPathRequestMatcher("/user/logout"));
            logout.deleteCookies("JSESSIONID");
            logout.invalidateHttpSession(true);
            logout.logoutSuccessUrl("/");  // 로그아웃 후 메인 페이지로 이동
        }).sessionManagement(session -> {
            session.maximumSessions(1);  // 세션 하나만 허용
            session.invalidSessionUrl("/");  // 잘못된 세션은 메인 페이지로 이동
        }).csrf(csrf -> csrf.disable());  // CSRF 비활성화

        return http.build();
    }

}