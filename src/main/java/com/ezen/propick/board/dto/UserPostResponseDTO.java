package com.ezen.propick.board.dto;

import com.ezen.propick.board.entity.UserPostBoard;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserPostResponseDTO {

    private Integer id;

    @NotBlank(message = "제목은 공백이 아니어야 합니다.")
    private String title;

    @NotBlank(message = "내용은 공백이 아니어야 합니다.")
    private String contents;

    private boolean pin_status;

    @NotBlank(message = "생성일은 공백이 아니어야 합니다.")
    private LocalDateTime created_at;

    private LocalDateTime updated_at;

    public UserPostResponseDTO(UserPostBoard entitiy){
        this.id = entitiy.getId();
        this.title = entitiy.getTitle();
        this.contents = entitiy.getContents();
        this.created_at = entitiy.getCreated_at();
        this.updated_at = entitiy.getUpdated_at();
    }

}
