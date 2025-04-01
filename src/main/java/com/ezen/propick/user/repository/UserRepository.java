package com.ezen.propick.user.repository;

import com.ezen.propick.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    //로그인
    Optional<User> findByUserId(String userId);

    //아이디 찾기
    Optional<User> findByUserNameAndUserPhone(String userName, String userPhone);


    //비밀번호 변경에 필요한 정보 찾기
    Optional<User> findByUserIdAndUserPhone(String userId, String userPhone);

    @Query("UPDATE User u SET u.userPwd = :userPwd WHERE u.userId = :userId")
    @Transactional
    @Modifying
    void updateByUserIdAndUserPwd(@Param("userId")String userId, @Param("userPwd")String userPwd);

    //회원 탈퇴
    @Modifying
    @Query("DELETE FROM User u WHERE u.userId = :userId")
    void deleteByUserId(String userId);


}

