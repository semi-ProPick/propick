package com.ezen.propick.auth.model;

import com.ezen.propick.auth.AuthDetails;
import com.ezen.propick.user.dto.LoginDTO;
import com.ezen.propick.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class AuthService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        logger.debug("Loading user by userId: {}", userId);
        LoginDTO loginDTO = userService.findByUserId(userId);
        logger.debug("Found LoginDTO: {}", loginDTO);
        if (Objects.isNull(loginDTO)) {
            logger.error("User not found with userId: {}", userId);
            throw new UsernameNotFoundException("해당하는 회원 정보가 존재하지 않습니다.");
        }
        return new AuthDetails(loginDTO);
    }
}