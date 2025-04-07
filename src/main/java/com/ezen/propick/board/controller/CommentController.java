package com.ezen.propick.board.controller;

import com.ezen.propick.board.dto.CommentRequestDTO;
import com.ezen.propick.board.entity.Comment;
import com.ezen.propick.board.repository.CommentRepository;
import com.ezen.propick.board.service.CommentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/main/Free_boardcm/{id}/comments")
    public ResponseEntity<?> commentSave(@PathVariable Integer id, @RequestBody CommentRequestDTO dto, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        Comment savedComment = commentService.commentSave(id, dto, principal.getName());

        try {
            // ✅ 서버가 반환할 JSON을 직접 변환하여 확인
            String jsonResponse = new ObjectMapper().writeValueAsString(savedComment);
            System.out.println("✅ 서버 응답 JSON: " + jsonResponse);
            return ResponseEntity.ok(savedComment);
        } catch (Exception e) {
            e.printStackTrace(); // 콘솔에 에러 출력
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("JSON 변환 오류 발생");
        }
    }





}

