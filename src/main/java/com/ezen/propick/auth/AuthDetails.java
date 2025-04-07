package com.ezen.propick.auth;

import com.ezen.propick.user.dto.LoginDTO;
import com.ezen.propick.user.dto.MemberDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthDetails implements UserDetails {

    private LoginDTO loginDTO;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 사용자의 권한 정보는 없으므로, 빈 권한 리스트를 반환
        return new ArrayList<>();
    }

    @Override
    public String getPassword() {
        System.out.println(loginDTO.getUserPwd());
        return loginDTO.getUserPwd();  // LoginDTO에서 비밀번호를 가져옴
    }

    @Override
    public String getUsername() {
        return loginDTO.getUserId();  // LoginDTO에서 아이디를 가져옴
    }

    @Override
    public boolean isAccountNonExpired() {
        System.out.println("isAccountNonExpired");
        return true;  // 계정 만료 여부를 처리
    }

    @Override
    public boolean isAccountNonLocked() {
        System.out.println("isAccountNonLocked");
        return true;  // 계정 잠금 여부를 처리
    }

    @Override
    public boolean isCredentialsNonExpired() {
        System.out.println("isCredentialsNonExpired");
        return true;  // 자격 증명 만료 여부를 처리
    }

    @Override
    public boolean isEnabled() {
        System.out.println("isEnabled");
        return true;  // 계정 활성화 여부를 처리
    }


}