package com.ezen.propick.auth.model.service;

import com.ezen.propick.auth.model.AuthDetails;
import com.ezen.propick.user.dto.LoginDTO;
import com.ezen.propick.user.dto.MemberDTO;
import com.ezen.propick.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class AuthService implements UserDetailsService {
    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException{
        System.out.println("userId : " + userId);
        MemberDTO memberDTO = userService.findByUserId(userId);
        System.out.println("memberDTO : " + memberDTO);
        if(Objects.isNull(memberDTO)){
            throw new UsernameNotFoundException("해당하는 회원 정보가 존재하지 않습니다.");
        }
        return new AuthDetails(memberDTO);
    }
}
