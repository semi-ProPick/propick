package com.ezen.propick.board.controller;

import com.ezen.propick.board.entity.FnaBoard;
import com.ezen.propick.board.entity.Notice;
import com.ezen.propick.board.service.FnaBoardService;
import com.ezen.propick.board.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/main/f&q_board")
public class UserFnaController {
    private final FnaBoardService fnaBoardService;

        @Autowired
        public UserFnaController(FnaBoardService fnaBoardService) {
            this.fnaBoardService = fnaBoardService;
        }

        @GetMapping("/list")
        public String fnqlist(Model model,
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
            return "/main/f&q_board";
        }

//    @GetMapping("/notice/view")   //localhost:8081/notice/view?id=1
//    public String noticeView(Model model, Integer id){
//        model.addAttribute("notice", noticeService.noticeView(id));
//        return "/main/notice";
//    }
    }

