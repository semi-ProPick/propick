package com.ezen.propick.board.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserPostRequestDTO {

    private Integer id;

    @NotBlank(message = "제목은 공백이 아니어야 합니다.")
    private String title;

    @NotBlank(message = "내용은 공백이 아니어야 합니다.")
    private String contents;

    private boolean pin_status;

}
