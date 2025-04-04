package com.ezen.propick.board.dto;

import com.ezen.propick.board.entity.Comment;
import com.ezen.propick.board.entity.UserPostBoard;
import com.ezen.propick.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentRequestDTO {

    private Integer id;
    private String contents;
    private LocalDateTime created_at;
    private User user;
    private UserPostBoard userPostBoard;

    /* DTO -> Entity*/
    public Comment toEntity() {
        Comment comments = Comment.builder()
                .id(id)
                .contents(contents)
                .created_at(created_at)
                .user(user)
                .userPostBoard(userPostBoard)
                .build();

        return comments;
    }

}
