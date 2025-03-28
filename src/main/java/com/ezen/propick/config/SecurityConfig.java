package com.ezen.propick.config;

import com.ezen.propick.config.handler.AuthFailHandler;
import com.ezen.propick.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
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
            auth.requestMatchers("/**").permitAll();

            // 그 외의 요청은 인증이 필요
            auth.anyRequest().authenticated();  // 모든 요청은 인증을 요구
        }).formLogin(login -> {
            login.loginPage("/user/login");  // 로그인 페이지 경로
            login.loginProcessingUrl("/user/doLogin");
//            login.usernameParameter("username");
//            login.passwordParameter("password");
            login.defaultSuccessUrl("/", true);  // 로그인 성공 후 메인 페이지로 이동
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