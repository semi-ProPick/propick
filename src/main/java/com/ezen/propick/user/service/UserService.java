package com.ezen.propick.user.service;

import com.ezen.propick.user.dto.LoginDTO;
import com.ezen.propick.user.dto.MemberDTO;
import com.ezen.propick.user.entity.User;
import com.ezen.propick.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    public LoginDTO findByUserId(String userId) {
        logger.debug("Finding user by userId: {}", userId);
        User user = userRepository.findByUserId(userId).orElse(null);
        if (user == null) {
            logger.warn("User not found with userId: {}", userId);
            return null;
        }
        LoginDTO loginDTO = new LoginDTO(user.getUserNo(), user.getUserId(), user.getUserPwd());
        logger.debug("Found user: {}", loginDTO);
        return loginDTO;
    }

    public void registerUser(MemberDTO memberDTO) {
        logger.info("Registering new user: {}", memberDTO.getUserId());
        User user = new User(
                memberDTO.getUserId(),
                memberDTO.getUserPwd(),
                memberDTO.getUserName(),
                memberDTO.getUserPhone(),
                memberDTO.getUserGender(),
                memberDTO.getUserBirth()
        );
        userRepository.save(user);
        logger.info("User registered successfully: {}", memberDTO.getUserId());
    }

    // createMember 메서드 추가
    public void createMember(MemberDTO memberDTO) {
        registerUser(memberDTO);
    }
}