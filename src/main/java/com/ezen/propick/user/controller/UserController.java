package com.ezen.propick.user.controller;

import com.ezen.propick.user.dto.LoginDTO;
import com.ezen.propick.user.dto.MemberDTO;
import com.ezen.propick.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    @Autowired
    private final UserService userService;

//     회원가입 페이지를 보여주는 메서드
    @GetMapping("/join")
    public String join(Model model) {
        model.addAttribute("MemberDTO", new MemberDTO()); // Model에 memberDTO 추가
        return "/main/join";  // join.html 뷰를 반환
    }

//    로그인 페이지를 보여주는 메서드
    @GetMapping("/login")
    public String login(){
        return"/main/login";
    }


    @PostMapping("/join")
    public String join(@ModelAttribute MemberDTO memberDTO, BindingResult bindingResult) {
        // 회원가입 처리
            userService.createMember(memberDTO);

        // 회원가입 후 로그인 페이지로 리다이렉트
        return "redirect:/";  // 로그인 페이지로 리다이렉트
    }

}
