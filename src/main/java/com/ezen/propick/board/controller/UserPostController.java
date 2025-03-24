package com.ezen.propick.board.controller;

import com.ezen.propick.board.entity.UserPostBoard;
import com.ezen.propick.board.service.UserPostBoardService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@RequiredArgsConstructor
@Controller
public class


UserPostController {
    @Autowired
    private final UserPostBoardService userPostBoardService;


    //@GetMapping("주소부분 쓰기")
    @GetMapping("/main/free_write")
    public String userPostWriteForm() {
        return "/main/free_write";
    }

    //게시글 작성
    @PostMapping("/main/free_write")
    public String userPostwRite(UserPostBoard userPostBoard){
        userPostBoardService.write(userPostBoard);
        System.out.println(userPostBoard.getTitle());
        System.out.println(userPostBoard.getContents());
        return "";
    }

    //작성한 게시글 리스트 출력
    @GetMapping("/main/free_board")
    public String userPostBoardList(Model model) {
        model.addAttribute("list",userPostBoardService.userPostBoardList());

        return "/main/free_board";
    }

    //상세 페이지 조회
    @GetMapping("/main/Free_boardcm")
    public String boardView(Model model, Integer id){
        model.addAttribute("userPostBoard",userPostBoardService.boardView(id));

        return "/main/Free_boardcm";
    }

    @GetMapping("/main/board_delete")
    public String boardDelete(Integer id){
        userPostBoardService.boardDelete(id);
        return "redirect:/main/free_board";
    }


    }





//    @PostMapping("/")
//    public String save(@ModelAttribute UserPostResponseDTO userPostResponseDTO) {
//        boardService.save(userPostResponseDTO);
//        return null;

