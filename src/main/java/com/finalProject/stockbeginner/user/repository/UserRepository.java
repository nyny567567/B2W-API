package com.finalProject.stockbeginner.user.repository;

import com.finalProject.stockbeginner.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    // 이메일로 회원정보 조회
   Optional<User> findByEmail(String email);

    // 이메일 중복 체크
   boolean existsByEmail(String email);


    // 닉네임 중복 체크
    boolean existsByNick(String nick);

    //카카오유저 중복확인
    Optional<User> findByKakaoId(long kakaoId);

    boolean existsByKakaoId(long kakaoId);

    User findByPhoneNumber(String phoneNumber);
}

