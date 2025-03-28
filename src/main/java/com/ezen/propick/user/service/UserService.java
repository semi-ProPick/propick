package com.ezen.propick.user.service;

import com.ezen.propick.user.dto.LoginDTO;
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
        User user = new User();
        user.setUserId(memberDTO.getUserId());
        user.setUserPwd(passwordEncoder.encode(memberDTO.getUserPwd())); // 암호화 적용
        user.setUserName(memberDTO.getUserName());
        user.setUserPhone(memberDTO.getUserPhone());
        user.setUserGender(memberDTO.getUserGender());
        user.setUserBirth(memberDTO.getUserBirth());

        this.userRepository.save(user);
    }

    public MemberDTO findByUserId(String userId){
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        String rawPwd = "1234";
        String encodedPwd = passwordEncoder.encode(rawPwd);
        System.out.println("encodePwd : " + encodedPwd);

        System.out.println("맞아????????? : " + passwordEncoder.matches("1234", encodedPwd));

        return new MemberDTO(user.getUserId(), user.getUserPwd());
    }



}
