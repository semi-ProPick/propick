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
            auth.requestMatchers("/", "/user/login", "/user/register", "/static/**").permitAll();
            auth.anyRequest().authenticated();
        }).formLogin(login -> {
            login.loginPage("/user/login");
            login.loginProcessingUrl("/user/doLogin");
            login.successHandler((request, response, authentication) -> {
                // ✅ 로그인 성공 시 userId를 세션에 저장
                request.getSession().setAttribute("userId", authentication.getName());
                response.sendRedirect("/"); // 로그인 성공 후 메인 페이지로 이동
            });
            login.failureHandler(authFailHandler);
        }).logout(logout -> {
            logout.logoutRequestMatcher(new AntPathRequestMatcher("/user/logout"));
            logout.invalidateHttpSession(true);
            logout.addLogoutHandler((request, response, authentication) -> {
                // ✅ 로그아웃 시 userId 세션 제거
                request.getSession().removeAttribute("userId");
            });
            logout.logoutSuccessUrl("/user/login");
        }).sessionManagement(session -> {
            session.sessionFixation().newSession();  // ✅ 로그인할 때 새로운 세션 생성
            session.maximumSessions(1).maxSessionsPreventsLogin(true);
        }).csrf(csrf -> csrf.disable());

        return http.build();
    }

}