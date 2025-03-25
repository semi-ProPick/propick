package com.ezen.propick.user.entity;

import com.ezen.propick.user.enumpackage.Gender;
import com.ezen.propick.user.enumpackage.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

//setter 사용 X : 의도가 분명하지 않고 객체를 언제든지 변경할 수 있는 상태가 되어서 객체의 안정성이 보장받기 힘들다. + 누가 변경했는지 추적 어려움
//AllArgsConstructor 사용 X : 클래스에 존재하는 모든 필드에 대한 생성자를 자동으로 생성하는데, 인스턴스 멤버의 선언 순서에 영향을 받기 대문에 두 변수의 순서를 바꾸면 생성자의 입력 값 순서도 바뀌게 되어 검출되지 않는 치명적인 오류를 발생시킬 수 있다.
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
public class User {
    @Id //pk설정
    @GeneratedValue(strategy = GenerationType.IDENTITY) //pk가 자동으로 늘어나는 기능
    @Column(name = "user_no")
    private Integer userNo;
    @Column(name = "user_id", nullable = false, unique = true)
    private String userId;
    @Column(name = "user_pwd", nullable = false)
    private String userPwd;
    @Column(name = "user_name", nullable = false)
    private String userName;
    @Column(name = "user_phone", nullable = false, unique = true)
    private String userPhone;
    @Column(name = "user_gender", nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender userGender;
    @Column(name = "user_birth", nullable = false)
    private Date userBirth;
    @Column(name = "user_role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role userRole = Role.User;

}