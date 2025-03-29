package com.ezen.propick.user.service;

import com.ezen.propick.user.dto.LoginDTO;
import com.ezen.propick.user.dto.MemberDTO;
import com.ezen.propick.user.dto.findIdDTO;
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


    public LoginDTO findByUserId(String userId){
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        return new LoginDTO(user.getUserId(), user.getUserPwd());
    }

    public findIdDTO inquiryId(String userName, String userPhone){
        User user = userRepository.findByUserNameAndUserPhone(userName, userPhone)
                .orElseThrow(() -> new RuntimeException(" 이름과 전화번호에 맞는 사용자 정보가 없습니다."));

                return new findIdDTO(user.getUserId());
    }

}
