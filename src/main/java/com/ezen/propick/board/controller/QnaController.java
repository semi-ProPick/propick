package com.ezen.propick.board.controller;

import com.ezen.propick.board.entity.QnaBoard;
import com.ezen.propick.board.entity.UserPostBoard;
import com.ezen.propick.board.service.QnaBoardService;
import com.ezen.propick.board.service.UserPostBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class QnaController {
    @Autowired
    private final QnaBoardService qnaBoardService;

    //@GetMapping("주소부분 쓰기")
    @GetMapping("/main/q&a_write")
    public String qnaWriteForm() {
        return "/main/q&a_write";
    }

    //게시글 작성
    @PostMapping("/main/q&a_write")
    public String qnaWriteForm(QnaBoard qnaBoard){
        qnaBoardService.write(qnaBoard);
        System.out.println(qnaBoard.getTitle());
        System.out.println(qnaBoard.getContents());
        return "";
    }

    //작성한 게시글 리스트 출력
    @GetMapping("/main/q&a_board")
    public String qnaBoardList(Model model) {
        model.addAttribute("list",qnaBoardService.qnaBoardList());

        return "/main/q&a_board";
    }

    //상세 조회
    @GetMapping("/main/q&a_detailpage")
    public String boardView(Model model, Integer id){
        model.addAttribute("qnaBoard",qnaBoardService.boardView(id));

        return "/main/q&a_detailpage";
    }


}

