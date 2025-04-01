package com.ezen.propick.board.dto;

import com.ezen.propick.board.entity.Comment;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class CommentResponseDTO {
    private Integer id;
    private String comment;
    private LocalDateTime created_at;
    private Integer post_id;

    public CommentResponseDTO(Comment comment) {
        this.id=comment.getId();
        this.comment = comment.getContents();
        this.created_at = comment.getCreated_at();
        this.post_id = comment.getUserPostBoard().getId();
    }
}
