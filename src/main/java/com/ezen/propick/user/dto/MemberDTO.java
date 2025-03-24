package com.ezen.propick.user.dto;

import com.ezen.propick.user.entity.User;
import com.ezen.propick.user.enumpackage.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {
    User user = new User();

    @NotBlank(message = "아이디는 필수 입력 값입니다.") //@NotNull은 null이 아닌 공백("")만 적어도 상관 없기 때문에 의도와 다를 수 있음
    @Pattern(regexp = "^[a-zA-Z0-9]{5,15}$", message = "아이디는 영어와 숫자만 가능합니다.")
    private String userId = user.getUserId();

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$", message = "비다밀번호는 8자 이상, 20자 이하, 대문자, 소문자, 숫자, 특수문자를 포함시켜야 합니.")
    private String userPwd = user.getUserPwd();

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    @Pattern(regexp = "^[가-힣]+$", message = "이름은 한글만 입력 가능합니다.")
    private String userName = user.getUserName();

    @NotBlank(message = "연락처는 필수 입력 값입니다.")
    @Pattern(regexp = "^[0-9]+$", message = "연락처는 숫자만 입력 가능합니다.")
    private String userPhone = user.getUserPhone();

    @NotBlank(message = "성별은 필수 입력 값입니다.")
    private Gender userGender;

    @NotBlank(message = "생년월일은 필수 입력 값입니다.")
    @Pattern(regexp = "^[0-9]+$", message = "연락처는 숫자만 입력 가능합니다.")
    private Date userBirth;
}
