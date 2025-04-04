package com.ezen.propick.board.dto;

import com.ezen.propick.board.entity.Comment;
import com.ezen.propick.board.entity.UserPostBoard;
import com.ezen.propick.user.entity.User;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
public class CommentResponseDTO {

    private Integer id;
    private String contents;
    private String created_at = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"));
    private String userId;
    private Integer post_id;

    /* Entity -> DTO */
    public CommentResponseDTO(Comment comment) {
        this.id = comment.getId();
        this.contents = comment.getContents();
        this.created_at = comment.getContents();
        this.userId = comment.getUser().getUserId();
        this.post_id = comment.getUserPostBoard().getId();
    }
}
