package com.ezen.propick.auth;

import com.ezen.propick.user.dto.LoginDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthDetails implements UserDetails {

    private static final Logger logger = LoggerFactory.getLogger(AuthDetails.class);

    private LoginDTO loginDTO;

    public Integer getUserNo() {
        if (loginDTO != null) {
            return loginDTO.getUserNo();
        }
        logger.warn("loginDTO is null, returning null userNo");
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new ArrayList<>();
    }

    @Override
    public String getPassword() {
        if (loginDTO != null) {
            logger.debug("Returning password for user: {}", loginDTO.getUserId());
            return loginDTO.getUserPwd();
        }
        logger.warn("loginDTO is null, returning null password");
        return null;
    }

    @Override
    public String getUsername() {
        if (loginDTO != null) {
            return loginDTO.getUserId();
        }
        logger.warn("loginDTO is null, returning null username");
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        logger.debug("isAccountNonExpired called");
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        logger.debug("isAccountNonLocked called");
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        logger.debug("isCredentialsNonExpired called");
        return true;
    }

    @Override
    public boolean isEnabled() {
        logger.debug("isEnabled called");
        return true;
    }
}