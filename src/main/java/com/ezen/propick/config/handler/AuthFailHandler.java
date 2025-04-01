package com.ezen.propick.config.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import java.io.IOException;
import java.net.URLEncoder;

@Configuration
public class AuthFailHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        String errorMessage = "";

        if (exception instanceof BadCredentialsException) {
            errorMessage = "";

        } else if (exception instanceof InternalAuthenticationServiceException) {
            errorMessage = "";

        } else if (exception instanceof UsernameNotFoundException) {
            errorMessage = "";

        } else if (exception instanceof AuthenticationCredentialsNotFoundException) {
            errorMessage = "";

        } else {
            errorMessage = "";
        }

        errorMessage = URLEncoder.encode(errorMessage, "UTF-8");

        setDefaultFailureUrl("/user/login?error"+ errorMessage);

        super.onAuthenticationFailure(request, response, exception);
    }

}