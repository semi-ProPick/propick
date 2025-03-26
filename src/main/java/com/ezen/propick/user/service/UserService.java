//package com.ezen.propick.user.service;
//
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//@Service
//public class UserService {
//
//    private final PasswordEncoder passwordEncoder;
//
//    public UserService(PasswordEncoder passwordEncoder) {
//        this.passwordEncoder = passwordEncoder;
//    }
//
//    // 예시 메서드
//    public void registerUser(String username, String rawPassword) {
//        String encodedPassword = passwordEncoder.encode(rawPassword);
//        // DB에 저장하는 로직 추가
//        System.out.println("Encoded password: " + encodedPassword);
//    }
//}