package com.finalProject.stockbeginner.user.repository;

import com.finalProject.stockbeginner.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}
