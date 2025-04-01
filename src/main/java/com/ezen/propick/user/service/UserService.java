    package com.ezen.propick.user.service;

    import com.ezen.propick.user.dto.*;
    import com.ezen.propick.user.entity.User;
    import com.ezen.propick.user.repository.UserRepository;
    import jakarta.transaction.Transactional;
    import lombok.RequiredArgsConstructor;
    import org.springframework.data.jpa.repository.Query;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.stereotype.Service;

    import java.util.Date;
    import java.util.Optional;

    @RequiredArgsConstructor
    @Service
    public class UserService {
        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder; //빈으로 등록해둔 것 주입받기

    public void createMember(MemberDTO memberDTO) {
        memberDTO.setUserId(memberDTO.getUserId());
//        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(); 빈으로 등록하는게 더 효율적이다.
        memberDTO.setUserPwd(passwordEncoder.encode(memberDTO.getUserPwd()));
        memberDTO.setUserName(memberDTO.getUserName());
        memberDTO.setUserPhone(memberDTO.getUserPhone());
        memberDTO.setUserGender(memberDTO.getUserGender());

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

        public FindIdDTO inquiryId(String userName, String userPhone){
            User user = userRepository.findByUserNameAndUserPhone(userName, userPhone)
                    .orElseThrow(() -> new RuntimeException(" 이름과 전화번호에 맞는 사용자 정보가 없습니다."));

                    return new FindIdDTO(user.getUserId());
        }

        //비밀번호 변경할 때 정보 조회 아이디, 전화번호
        public PwdUserInfoDTO inquiryMyInfo(String userId, String userPhone){
            User user = userRepository.findByUserIdAndUserPhone(userId, userPhone)
                    .orElseThrow(() -> new RuntimeException(" 사용자를 찾을 수 없습니다."));
            return new PwdUserInfoDTO(user.getUserId(), user.getUserPhone());
        }

        //비밀번호 변경
        @Transactional
        public boolean changePassword(String userId, String userPwd) {
            Optional<User> user = userRepository.findByUserId(userId);

            if (user.isPresent()) {
                userRepository.updateByUserIdAndUserPwd(userId, passwordEncoder.encode(userPwd));
                return true;
            }
            return false;
        }

        //마이페이지 내 정보 관리 로그인 유저 정보 불러오기
        //LoginDTO에서 아이디만 받고 나머지는 유저 객체를 생성해서 받기 위해 생성
        public User findUserByUserId(String userId) {
            return userRepository.findByUserId(userId)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        }

        public User updateUserInfo(String userId, MyInfoDTO myInfoDTO) {
            User currentUser = userRepository.findByUserId(userId)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

            currentUser.setUserName(myInfoDTO.getUserName());
            currentUser.setUserPhone(myInfoDTO.getUserPhone());
            currentUser.setUserBirth(myInfoDTO.getUserBirth());

            return userRepository.save(currentUser);
        }


        //회원 탈퇴
        @Transactional
        public void deleteUser(String userId) {
            userRepository.deleteByUserId(userId);
        }

    }
