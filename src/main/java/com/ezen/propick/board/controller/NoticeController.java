package com.ezen.propick.board.controller;

import com.ezen.propick.board.entity.FnaBoard;
import com.ezen.propick.board.entity.Notice;
import com.ezen.propick.board.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class NoticeController {

    @Autowired
    private final NoticeService noticeService;

    @GetMapping("/management/post_notice")
    public String noticeWriteForm() {
        return "/management/post_notice";
    }

    //게시글 작성
    @PostMapping("/management/post_notice")
    public String noticeWriteForm(Notice notice) {
        noticeService.write(notice);
        System.out.println(notice.getTitle());
        System.out.println(notice.getContents());
        return "";
    }

    //게시글 리스트 출력
    @GetMapping("/main/announcement")
    public String noticeList(Model model) {
        model.addAttribute("list", noticeService.noticeList());

        return "/main/announcement";
    }
}
