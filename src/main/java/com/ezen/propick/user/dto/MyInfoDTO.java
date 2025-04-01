package com.ezen.propick.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MyInfoDTO {
    private String userName;
    private String userPhone;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date userBirth;
}