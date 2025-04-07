package com.ezen.propick.admin.controller;

import com.ezen.propick.board.entity.FnaBoard;
import com.ezen.propick.board.service.FnaBoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
@Profile("admin")
@Controller
public class FnaController {
    @Autowired
    private final FnaBoardService fnaBoardService;

    public FnaController(FnaBoardService fnaBoardService) {
        this.fnaBoardService = fnaBoardService;
    }

    @GetMapping("/fna/write")
    public String noticeWriteForm() {

        return "/management/post_fna_write";
    }

    @PostMapping("/fna/writedo")
    public String fnaWritePro(FnaBoard fnaBoard) {
        fnaBoardService.fnawrite(fnaBoard);
        return "redirect:/fna/list";

    }

    @GetMapping("/fna/list")
    public String fnalist(Model model,
                             @PageableDefault(page=0, size=10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                             String searchKeyword,
                             @RequestParam(value = "page", defaultValue = "0") int page) { //url 에서 ?page=1, ?page=0등의 값 받아옴

        Page<FnaBoard> list = null;
        //searchKeyword 가 넣이면 원래 페이지 리스트 보여줌
        if(searchKeyword == null) {
            list = fnaBoardService.fnaBoardList(pageable);

            //searchKeyword 가 널이 아니면 작성한 검색 메서드 실행 후 페이지 리스트 보여줌
        } else {
            list = fnaBoardService.fnaSearchList(searchKeyword, pageable);
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
        return "/management/post_fna";
    }

    @GetMapping("/fna/view")   //localhost:8081/notice/view?id=1
    public String fnaView(Model model, Integer id){
        model.addAttribute("fnaBoard", fnaBoardService.fnaView(id));
        return "/management/post_fna_view";
    }

    @GetMapping("fna/delete")
    public String fnaDelete(Integer id){
        fnaBoardService.fnaDelete(id);
        return "redirect:list";
    }

    //수정 @PathVariable 사용
    @GetMapping("fna/modify/{id}")
    public String fnaModify(@PathVariable("id") Integer id, Model model){

        model.addAttribute("fnaBoard", fnaBoardService.fnaView(id));
        return "/management/post_fna_modify";
    }

    //수정
    @PostMapping("/fna/update/{id}")
    public String fnaUpdate(@PathVariable("id") Integer id, FnaBoard fnaBoard) {

        //기존 내용 가져옴
        FnaBoard boardTemp = fnaBoardService.fnaView(id);
        //위의 인수로 받은 새로운 내용 가져오기-> 덮어씌우기
        boardTemp.setTitle(fnaBoard.getTitle());
        boardTemp.setAnswer(fnaBoard.getAnswer());

        fnaBoardService.fnawrite(boardTemp);

        return "redirect:/fna/list";
    }


//    //게시글 리스트 출력
//    @GetMapping("/main/f&q_board")
//    public String fnaBoardList(Model model) {
//        model.addAttribute("list", fnaBoardService.fnaBoardList());
//
//        return "/main/f&q_board";
//    }



}

