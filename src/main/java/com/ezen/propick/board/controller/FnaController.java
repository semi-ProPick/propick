package com.ezen.propick.board.controller;

import com.ezen.propick.board.entity.FnaBoard;
import com.ezen.propick.board.service.FnaBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class FnaController {
    @Autowired
    private final FnaBoardService fnaBoardService;

    @GetMapping("/management/post_qna")
    public String fnaWriteForm() {
        return "/management/post_qna";
    }

    //게시글 작성
    @PostMapping("/management/post_qna")
    public String fnaWriteForm(FnaBoard fnaBoard) {
        fnaBoardService.write(fnaBoard);
        System.out.println(fnaBoard.getTitle());
        System.out.println(fnaBoard.getAnswer());
        return "";
    }

    //게시글 리스트 출력
    @GetMapping("/main/f&q_board")
    public String fnaBoardList(Model model) {
        model.addAttribute("list", fnaBoardService.fnaBoardList());

        return "/main/f&q_board";
    }



}

