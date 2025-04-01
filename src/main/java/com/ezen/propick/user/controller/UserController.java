package com.ezen.propick.user.controller;

import com.ezen.propick.user.dto.MemberDTO;
import com.ezen.propick.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping
public class UserController {
    @Autowired
    private final UserService userService;

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

    @PostMapping("/join")
    public String join(@ModelAttribute MemberDTO memberDTO,
                       BindingResult bindingResult,
                       @RequestParam(value = "redirect", required = false) String redirect) {

        userService.createMember(memberDTO);

        // redirect 파라미터가 있으면 해당 경로로, 없으면 홈(/)으로 이동
        if (redirect != null && !redirect.isBlank()) {
            return "redirect:" + redirect;
        } else {
            return "redirect:/";
        }
    }


    @GetMapping("/findid")
    public String findId() {
        return "/main/id_find";
    }
}
