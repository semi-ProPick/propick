package com.ezen.propick.board.controller;

import com.ezen.propick.board.entity.UserPostBoard;
import com.ezen.propick.board.repository.UserPostBoardRepository;
import com.ezen.propick.board.service.UserPostBoardService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequiredArgsConstructor
@Controller
public class UserPostController {
    @Autowired
    private final UserPostBoardService userPostBoardService;
    @Autowired
    private UserPostBoardRepository userPostBoardRepository;


    //@GetMapping("주소부분 쓰기")
    @GetMapping("main/free_write")
    public String userPostWriteForm() {
        return "main/free_write";
    }

    //게시글 작성
    @PostMapping("main/free_write")
    public String userPostWrite(UserPostBoard userPostBoard, Model model, MultipartFile file) throws Exception{
        userPostBoardService.write(userPostBoard, file);
        System.out.println(userPostBoard.getTitle());
        System.out.println(userPostBoard.getContents());
        model.addAttribute("message","작성 완료!");
//        model.addAttribute("list", userPostBoardService.userPostBoardList());
        model.addAttribute("searchUrl","/main/free_board");
        return "/main/postmessage";
    }



//    @PostMapping("/main/free_write")
//    public String createPost(@ModelAttribute UserPostBoard post, RedirectAttributes redirectAttributes) {
//        userPostBoardRepository.save(post);
//        redirectAttributes.addFlashAttribute("message", "게시글이 등록되었습니다!");
//        return "redirect:/main/free_board";
//    }


    //작성한 게시글 리스트 출력
    @GetMapping("main/free_board")
    public String userPostBoardList(Model model, @PageableDefault(page=0, size=10, sort = "id",
                                    direction = Sort.Direction.DESC) Pageable pageable,
                                    @RequestParam(value = "page", defaultValue = "0") int page) { //url 에서 ?page=1, ?page=0등의 값 받아옴

        // 현재 페이지를 기반으로 pageable 생성
        pageable = PageRequest.of(page, pageable.getPageSize(), pageable.getSort());


        Page<UserPostBoard> list = userPostBoardService.userPostBoardList(pageable);

        //현재 페이지 가져오기  페이지는 0에서 시작하기때문에 1 더해줌
        int nowPage = list.getPageable().getPageNumber() + 1;
        //페이지 수가 음수가 나올 경우 1 반환
        int startPage = Math.max(nowPage -4 , 1);

        int endPage = Math.min(nowPage + 5, list.getTotalPages());

        model.addAttribute("list",list);
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "main/free_board";
    }


    //상세 페이지 조회
    @GetMapping("/main/Free_boardcm")  //이 경로로 왔을때 상세 페이지 조회됨
    @Transactional
    public String boardView(Integer id, Model model){
        model.addAttribute("userPostBoard",userPostBoardService.boardView(id));

        UserPostBoard userPostBoard = userPostBoardRepository.findById(id).get();
        userPostBoard.setCountview(userPostBoard.getCountview() + 1 );
        return "/main/Free_boardcm";  //상세페이지 뷰를 담당하는 html 주소
    }

    @GetMapping("main/board_delete")
    public String boardDelete(Integer id){
        userPostBoardService.boardDelete(id);
        return "redirect:free_board";
    }


    }





//    @PostMapping("/")
//    public String save(@ModelAttribute UserPostResponseDTO userPostResponseDTO) {
//        boardService.save(userPostResponseDTO);
//        return null;

