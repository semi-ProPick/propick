package com.ezen.propick.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewPwdDTO {
    private String userId;
    private String userPwd;
    private String userPhone;
}
