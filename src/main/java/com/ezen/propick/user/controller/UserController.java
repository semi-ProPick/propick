package com.ezen.propick.user.controller;

import com.ezen.propick.user.dto.MemberDTO;
import com.ezen.propick.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    @Autowired
    private final UserService userService;

    @GetMapping("/")
    public void signUp(MemberDTO memberDTO) {
        userService.createMember(memberDTO);
    }

//    @GetMapping("/signUp")
//    public String signUp1() {
//        System.out.println("아무거나");
//        return "아무거나";
//    }

}
