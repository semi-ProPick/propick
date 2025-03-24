package com.ezen.propick.user.repository;

import com.ezen.propick.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}

