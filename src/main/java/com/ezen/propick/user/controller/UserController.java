package com.ezen.propick.user.controller;

import com.ezen.propick.auth.model.AuthDetails;
import com.ezen.propick.user.dto.*;
import com.ezen.propick.user.entity.User;
import com.ezen.propick.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Profile("user")
@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    @Autowired
    private final UserService userService;
    //로그인페이지 반환

    //메인페이지
    @GetMapping
    public String index(Model model) {
        return "main/main";
    }

    //마이페이지
    @GetMapping("/mypage")
    public String mypage() {
        return "main/mypage";
    }

    @GetMapping("/login")
    public String login(){
        return "/main/login";
    }


    //     회원가입 페이지를 보여주는 메서드
    @GetMapping("/join")
    public String join(Model model) {
        model.addAttribute("MemberDTO", new MemberDTO()); // Model에 memberDTO 추가
        return "/main/join";  // join.html 뷰를 반환
    }
    //회원가입 처리
    @PostMapping("/join")
    public String join(@ModelAttribute MemberDTO memberDTO, BindingResult bindingResult) {
        userService.createMember(memberDTO);
        return "redirect:/user/login";
    }

    //아이디찾기 페이지
    @GetMapping("/findid")
    public String findId() {
        return "/main/id_find";
    }

    //아이디 찾기 처리
    @PostMapping("/findid")
    public String findUsername(@RequestParam String name, @RequestParam String phone, Model model) {
        try {
            // 이름과 전화번호로 아이디 찾기
            FindIdDTO findIdDTO = userService.inquiryId(name, phone);

            // 아이디가 존재하면 모델에 아이디를 추가하여 반환
            model.addAttribute("userId", findIdDTO.getUserId()); // findIdDTO의 userId를 모델에 추가
            return "/main/id_find"; // 동일한 페이지를 반환하여 아이디를 출력
        } catch (RuntimeException e) {
            // 사용자 정보를 찾을 수 없는 경우 오류 메시지 추가
            model.addAttribute("error", e.getMessage());
            return "/main/id_find"; // 동일한 페이지로 오류 메시지를 표시
        }
    }

    //비밀번호 보여주는 페이지
    @GetMapping("/findpwd")
    public String findPwd() {
        return "/main/password_find";
    }

    @PostMapping("/findpwd")
    public String resetPwd(@ModelAttribute NewPwdDTO newPwdDTO, Model model) {
        try {
            // 사용자 정보 확인
            PwdUserInfoDTO userInfo = userService.inquiryMyInfo(newPwdDTO.getUserId(), newPwdDTO.getUserPhone());

            // 비밀번호 변경
            boolean isPasswordChanged = userService.changePassword(newPwdDTO.getUserId(), newPwdDTO.getUserPwd());

            if (isPasswordChanged) {
                model.addAttribute("success", "비밀번호가 변경되었습니다.");
            } else {
                model.addAttribute("error", "비밀번호 변경에 실패했습니다.");
            }
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "/main/password_find"; // 비밀번호 찾기 페이지로 리턴
    }

    @GetMapping("/modifyMyInfo")
    public String modifyMyPage(Model model, @AuthenticationPrincipal AuthDetails userDetails) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        System.out.println("🔍 authentication.getName() 값: " + authentication.getName());
        String userId = userDetails.getUsername();

        User userInfo = userService.findUserByUserId(userId);
        model.addAttribute("user", userInfo);

        return "/main/user_modify";
    }


    @PostMapping("/modifyMyInfo")
    public String modifyMyInfo(@ModelAttribute MyInfoDTO myInfoDTO, @AuthenticationPrincipal AuthDetails userDetails) {
        String userId = userDetails.getUsername();

        // userService를 통해 사용자 정보 수정
        userService.updateUserInfo(userId, myInfoDTO);

        return "redirect:/"; // 수정 완료 후 마이페이지로 리다이렉트
    }

    //회원 탈퇴
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteByUserId(@PathVariable String userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok("회원 탈퇴 완료");
    }

    @DeleteMapping("/me")
    public ResponseEntity<String> deleteCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        userService.deleteUser(userDetails.getUsername());  // 현재 로그인된 유저 탈퇴
        return ResponseEntity.ok("회원 탈퇴 완료");
    }

    @RequestMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("로그아웃 완료");
    }

    //회원가입 아이디 중복 체크
    @GetMapping("/search")
    public List<User> searchUsers(@RequestParam String keyword) {
        return userService.searchUsers(keyword);
    }
}


