package com.ezen.propick.board.controller;

import com.ezen.propick.board.entity.QnaBoard;
import com.ezen.propick.board.entity.UserPostBoard;
import com.ezen.propick.board.repository.QnaBoardRepository;
import com.ezen.propick.board.service.QnaBoardService;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequiredArgsConstructor
public class QnaController {
    @Autowired
    private final QnaBoardService qnaBoardService;

    @Autowired
    private final QnaBoardRepository qnaBoardRepository;

    //@GetMapping("주소부분 쓰기")
    @GetMapping("/main/q&a_write")
    public String qnaWriteForm() {
        return "/main/q&a_write";
    }

    //문의사항 삭제
    @GetMapping("main/qna_board_delete")
    public String boardDelete(Integer id){
        qnaBoardService.qnaboardDelete(id);
        return "redirect:q&a_board";
    }
    //문의사항 작성
    @PostMapping("/main/q&a_write")
    public String qnaWrite(QnaBoard qnaBoard, Model model, MultipartFile file) throws Exception{
        qnaBoardService.write(qnaBoard, file);
        System.out.println(qnaBoard.getTitle());
        System.out.println(qnaBoard.getContents());
        model.addAttribute("message","작성 완료!");
        model.addAttribute("searchUrl", "/main/q&a_board");
        return "/main/postmessage";
    }


    //작성한 게시글 리스트 출력
    @GetMapping("main/q&a_board")
    public String qnaBoardList(Model model,
                                    @PageableDefault(page=0, size=10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                                    String searchKeyword,
                                    @RequestParam(value = "page", defaultValue = "0") int page) { //url 에서 ?page=1, ?page=0등의 값 받아옴

        Page<QnaBoard> list = null;
        //searchKeyword 가 넣이면 원래 페이지 리스트 보여줌
        if(searchKeyword == null) {
            list = qnaBoardService.qnaBoardList(pageable);

            //searchKeyword 가 널이 아니면 작성한 검색 메서드 실행 후 페이지 리스트 보여줌
        } else {
            list = qnaBoardService.boardSearchList(searchKeyword, pageable);
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

        return "main/q&a_board";
    }

    //상세 조회
    @GetMapping("/main/q&a_detailpage")
    @Transactional
    public String boardView(Model model, Integer id){
        model.addAttribute("qnaBoard",qnaBoardService.boardView(id));

        QnaBoard qnaBoard = qnaBoardRepository.findById(id).get();
        return "/main/q&a_detailpage";
    }


}

