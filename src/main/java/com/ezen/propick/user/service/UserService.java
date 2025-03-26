package com.ezen.propick.user.service;

import com.ezen.propick.user.dto.MemberDTO;
import com.ezen.propick.user.entity.User;
import com.ezen.propick.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; //빈으로 등록해둔 것 주입받기

    public void createMember(MemberDTO memberDTO) {
        memberDTO.setUserId(memberDTO.getUserId());
        memberDTO.setUserPwd(passwordEncoder.encode(memberDTO.getUserPwd()));
        memberDTO.setUserName(memberDTO.getUserName());
        memberDTO.setUserPhone(memberDTO.getUserPhone());
        memberDTO.setUserGender(memberDTO.getUserGender());
        memberDTO.setUserBirth(memberDTO.getUserBirth());


        User user = new User();
        user.setUserId(memberDTO.getUserId());
        user.setUserPwd(memberDTO.getUserPwd());
        user.setUserName(memberDTO.getUserName());
        user.setUserPhone(memberDTO.getUserPhone());
        user.setUserGender(memberDTO.getUserGender());
        user.setUserBirth(memberDTO.getUserBirth());

        this.userRepository.save(user);
    }
}


//inqueryMember 단일 조회
//inqueryMemberList 다중 조회