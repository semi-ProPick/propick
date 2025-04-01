package com.ezen.propick.board.dto;

import com.ezen.propick.board.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AddCommentRequestDTO {
    private Integer id;
    private String contents;
    private LocalDateTime created_at;
    private String userId;
    private int userPostBoard;


    public Comment toEntity() {
        return Comment.builder()
                .contents(contents)
                .created_at(created_at)

                .build();
    }
}
