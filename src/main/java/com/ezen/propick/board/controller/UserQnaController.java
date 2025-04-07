package com.ezen.propick.board.controller;

import com.ezen.propick.board.entity.Notice;
import com.ezen.propick.board.entity.QnaAnswer;
import com.ezen.propick.board.entity.QnaBoard;
import com.ezen.propick.board.entity.UserPostBoard;
import com.ezen.propick.board.repository.QnaAnswerRepository;
import com.ezen.propick.board.repository.QnaBoardRepository;
import com.ezen.propick.board.service.NoticeService;
import com.ezen.propick.board.service.QnaBoardService;
import com.ezen.propick.user.entity.User;
import com.ezen.propick.user.repository.UserRepository;
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

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
public class UserQnaController {

    @Autowired
    private final QnaBoardService qnaBoardService;
    @Autowired
    private QnaBoardRepository qnaBoardRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QnaAnswerRepository qnaAnswerRepository;

    @GetMapping("main/q&a_write")
    public String qnaWriteForm() {
        return "main/q&a_write";
    }

    //문의사항 작성
    @PostMapping("main/q&a_write")
    public String qnaWrite(QnaBoard qnaBoard, Model model, MultipartFile file, Principal principal) throws Exception {
        String username = principal.getName(); // 현재 로그인한 아이디
        Optional<User> user = userRepository.findByUserId(username); // 아이디로 유저 찾기

        qnaBoardService.write(qnaBoard, file, user); // ← user 넘김

        model.addAttribute("message","작성 완료!");
        model.addAttribute("searchUrl","/main/q&a_board");
        return "/main/postmessage";
    }


    @GetMapping("main/q&a_board")
    public String qnalist(Model model,
                             @PageableDefault(page=0, size=10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                             String searchKeyword,
                             @RequestParam(value = "page", defaultValue = "0") int page) { //url 에서 ?page=1, ?page=0등의 값 받아옴

        Page<QnaBoard> list = null;
        //searchKeyword 가 넣이면 원래 페이지 리스트 보여줌
        if(searchKeyword == null) {
            list = qnaBoardService.qnaBoardList(pageable);

            //searchKeyword 가 널이 아니면 작성한 검색 메서드 실행 후 페이지 리스트 보여줌
        } else {
            list = qnaBoardService.qnaSearchList(searchKeyword, pageable);
        }
        // 현재 페이지를 기반으로 pageable 생성
        pageable = PageRequest.of(page, pageable.getPageSize(), pageable.getSort());


        //현재 페이지 가져오기  페이지는 0에서 시작하기때문에 1 더해줌
        int nowPage = list.getPageable().getPageNumber() + 1;
        //페이지 수가 음수가 나올 경우 1 반환
        int startPage = Math.max(nowPage -4 , 1);

        int endPage = Math.min(nowPage + 5, list.getTotalPages());

        model.addAttribute("list",list);
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        return "/main/q&a_board";
    }

    //내가쓴 글 조회
    @GetMapping("main/my_q&a_board")
    public String qnalist1(Model model,
                          @PageableDefault(page=0, size=10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                          String searchKeyword,
                          @RequestParam(value = "page", defaultValue = "0") int page) { //url 에서 ?page=1, ?page=0등의 값 받아옴

        Page<QnaBoard> list = null;
        //searchKeyword 가 넣이면 원래 페이지 리스트 보여줌
        if(searchKeyword == null) {
            list = qnaBoardService.qnaBoardList(pageable);

            //searchKeyword 가 널이 아니면 작성한 검색 메서드 실행 후 페이지 리스트 보여줌
        } else {
            list = qnaBoardService.qnaSearchList(searchKeyword, pageable);
        }
        // 현재 페이지를 기반으로 pageable 생성
        pageable = PageRequest.of(page, pageable.getPageSize(), pageable.getSort());


        //현재 페이지 가져오기  페이지는 0에서 시작하기때문에 1 더해줌
        int nowPage = list.getPageable().getPageNumber() + 1;
        //페이지 수가 음수가 나올 경우 1 반환
        int startPage = Math.max(nowPage -4 , 1);

        int endPage = Math.min(nowPage + 5, list.getTotalPages());

        model.addAttribute("list",list);
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        return "/main/my_q&a_board";
    }

    //상세 페이지 조회
    @GetMapping("/main/q&a_detailpage")  //이 경로로 왔을때 상세 페이지 조회됨
    @Transactional
    public String boardView(Integer id, Model model){

        QnaBoard qnaBoard = qnaBoardRepository.findById(id).get();
        model.addAttribute("qnaBoard",qnaBoardService.boardView(id));

        List<QnaAnswer> answerList = qnaAnswerRepository.findAllByQnaBoard(qnaBoard);
        model.addAttribute("qnaAnswer", answerList);
        return "/main/q&a_detailpage";  //상세페이지 뷰를 담당하는 html 주소
    }

    //문의사항 삭제
    @GetMapping("main/qna_board_delete")
    public String boardDelete(Integer id){
        qnaBoardService.qnaboardDelete(id);
        return "redirect:q&a_board";
    }

    //수정 @PathVariable 사용
    @GetMapping("main/qna_modify/{id}")
    public String qnaModify(@PathVariable("id") Integer id, Model model){

        model.addAttribute("qnaBoard", qnaBoardService.boardView(id));
        return "main/q&a_modify";
    }



    //수정
    @PostMapping("/main/qna_update/{id}")
    public String qnaUpdate(@PathVariable("id") Integer id, QnaBoard qnaBoard) {

        //기존 내용 가져옴
        QnaBoard boardTemp = qnaBoardService.boardView(id);
        //위의 인수로 받은 새로운 내용 가져오기-> 덮어씌우기
        boardTemp.setTitle(qnaBoard.getTitle());
        boardTemp.setContents(qnaBoard.getContents());

        qnaBoardService.modify(boardTemp);

        return "redirect:/main/q&a_board";
    }
}
