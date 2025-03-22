package com.ezen.propick.user.controller;

import com.ezen.propick.user.dto.MemberDTO;
import com.ezen.propick.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/")
public class UserController {
    @Autowired
    private final UserService userService;

    @PostMapping("/")
    public void signUp(MemberDTO memberDTO) {
        userService.createMember(memberDTO);
    }
}
