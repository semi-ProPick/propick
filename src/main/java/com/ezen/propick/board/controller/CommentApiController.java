package com.ezen.propick.board.controller;

import com.ezen.propick.board.dto.AddCommentRequestDTO;
import com.ezen.propick.board.entity.Comment;
import com.ezen.propick.board.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentApiController {
    private final CommentService commentService;

    //생성
    @PostMapping("/main/Free_boardcm/{id}/comments")
    public ResponseEntity<Comment> save(@PathVariable Integer id, @RequestBody AddCommentRequestDTO requestDTO, Principal principal) {
        Comment savedComment = commentService.save(id,requestDTO,principal.getName());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedComment);
    }

    //조회
    @GetMapping("main/Free_boardcm/{id}/comments")
    public List<Comment> read(@PathVariable Integer id) {
        return commentService.findAll(id);
    }

    //삭제
    @DeleteMapping({"/main/Free_boardcm/comments/{id}"})
    public ResponseEntity<Integer> delete(@PathVariable Integer id) {
        commentService.delete(id);
        return ResponseEntity.ok(id);
    }
}
