package com.ezen.propick.board.controller;

import com.ezen.propick.board.dto.CommentRequestDTO;
import com.ezen.propick.board.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;



    // 댓글 생성
    @PostMapping("/main/Free_boardcm/{id}/comments")
    public ResponseEntity<?> commentSave(@PathVariable Integer id, @RequestBody CommentRequestDTO dto) {
        // 로그인 없이 `guest_user`로 저장하도록 설정
        String tempUserId = "guest_user";
        return ResponseEntity.ok(commentService.commentSave(id, dto, tempUserId));
    }
}

