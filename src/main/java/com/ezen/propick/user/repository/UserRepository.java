package com.ezen.propick.user.repository;

import com.ezen.propick.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    //로그인
    Optional<User> findByUserId(String userId);

    //아이디 찾기
    Optional<User> findByUserNameAndUserPhone(String userName, String userPhone);


    //비밀번호 찾기
}

