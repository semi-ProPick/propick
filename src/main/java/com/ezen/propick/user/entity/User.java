package com.ezen.propick.user.entity;

import com.ezen.propick.user.enumpackage.Gender;
import com.ezen.propick.user.enumpackage.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_no", updatable = false, nullable = false)
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

    public User(String userId, String userPwd, String userName, String userPhone, Gender userGender, Date userBirth) {
        this.userId = userId;
        this.userPwd = userPwd;
        this.userName = userName;
        this.userPhone = userPhone;
        this.userGender = userGender;
        this.userBirth = userBirth;
    }

    public User(Integer userNo, String userId, String userPwd, String userName, String userPhone, Gender userGender, Date userBirth, Role userRole) {
        this.userNo = userNo;
        this.userId = userId;
        this.userPwd = userPwd;
        this.userName = userName;
        this.userPhone = userPhone;
        this.userGender = userGender;
        this.userBirth = userBirth;
        this.userRole = userRole;
    }
}