package com.ezen.propick.admin.controller;

import com.ezen.propick.board.entity.Notice;
import com.ezen.propick.board.service.NoticeService;
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
public class NoticeController {

    @Autowired
    private final NoticeService noticeService;

    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @GetMapping("/notice/write")
    public String noticeWriteForm() {

        return "/management/post_notice_write";
    }

    @PostMapping("/notice/writedo")
    public String noticeWritePro(Notice notice) {
        noticeService.noticewrite(notice);
        return "redirect:/notice/list";

    }
    @GetMapping("/notice/list")
    public String noticelist(Model model,
                             @PageableDefault(page=0, size=10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                             String searchKeyword,
                             @RequestParam(value = "page", defaultValue = "0") int page) { //url 에서 ?page=1, ?page=0등의 값 받아옴

        Page<Notice> list = null;
        //searchKeyword 가 넣이면 원래 페이지 리스트 보여줌
        if(searchKeyword == null) {
            list = noticeService.noticeList(pageable);

            //searchKeyword 가 널이 아니면 작성한 검색 메서드 실행 후 페이지 리스트 보여줌
        } else {
            list = noticeService.noticeSearchList(searchKeyword, pageable);
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
        return "/management/post_notice";
    }

    @GetMapping("/notice/view")   //localhost:8081/notice/view?id=1
    public String noticeView(Model model, Integer id){
        model.addAttribute("notice", noticeService.noticeView(id));
        return "/management/post_notice_view";
    }

    @GetMapping("notice/delete")
    public String noticeDelete(Integer id){
        noticeService.noticeDelete(id);
        return "redirect:list";
    }

    //수정 @PathVariable 사용
    @GetMapping("notice/modify/{id}")
    public String noticeModify(@PathVariable("id") Integer id, Model model){

        model.addAttribute("notice", noticeService.noticeView(id));
        return "/management/post_notice_modify";
    }

    //수정
    @PostMapping("/notice/update/{id}")
    public String noticeUpdate(@PathVariable("id") Integer id, Notice notice) {

        //기존 내용 가져옴
        Notice boardTemp = noticeService.noticeView(id);
        //위의 인수로 받은 새로운 내용 가져오기-> 덮어씌우기
        boardTemp.setTitle(notice.getTitle());
        boardTemp.setContents(notice.getContents());

        noticeService.noticewrite(boardTemp);

        return "redirect:/notice/list";
    }

}
