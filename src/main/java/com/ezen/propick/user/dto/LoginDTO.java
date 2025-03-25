package com.ezen.propick.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {
    @NotBlank(message = "아이디는 필수 입력 값입니다.")
    private String userId;
    @NotBlank(message = "아이디는 필수 입력 값입니다.")
    private String userPassword;
}
