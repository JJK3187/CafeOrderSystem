package com.cafeorder.domain.users.repository;

import com.cafeorder.domain.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}

