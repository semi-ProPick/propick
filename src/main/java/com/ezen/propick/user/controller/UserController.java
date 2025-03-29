package com.ezen.propick.user.controller;

import com.ezen.propick.user.dto.MemberDTO;
import com.ezen.propick.user.dto.findIdDTO;
import com.ezen.propick.user.entity.User;
import com.ezen.propick.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    //로그인페이지 반환
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
        return "redirect:/";
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
            findIdDTO findIdDTO = userService.inquiryId(name, phone);

            // 아이디가 존재하면 모델에 아이디를 추가하여 반환
            model.addAttribute("userId", findIdDTO.getUserId()); // findIdDTO의 userId를 모델에 추가
            return "/main/id_find"; // 동일한 페이지를 반환하여 아이디를 출력
        } catch (RuntimeException e) {
            // 사용자 정보를 찾을 수 없는 경우 오류 메시지 추가
            model.addAttribute("error", e.getMessage());
            return "/main/id_find"; // 동일한 페이지로 오류 메시지를 표시
        }
    }


}
