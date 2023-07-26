package com.finalProject.stockbeginner.board.repository;

import com.finalProject.stockbeginner.board.dto.requestDTO.BoardRegisterRequestDTO;
import com.finalProject.stockbeginner.board.entity.Inquiry;
import com.finalProject.stockbeginner.user.entity.User;
import com.finalProject.stockbeginner.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class InquiryRepositoryTest {

    @Autowired
    InquiryRepository inquiryRepository;
    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("등록")
    void resister() {
        //given
        String writer = "최민수";
        String title = "등록 테스트2";
        String content = "ㅇㅇ";
        String email = "kingminsu@naver.com";
        String type = "inquiry";
        BoardRegisterRequestDTO requestDTO = BoardRegisterRequestDTO.builder().type(type).email(email).title(title).content(content).writer(writer).build();
        User user = userRepository.findByEmail(email).orElseThrow();
        Inquiry inquiry = new Inquiry(requestDTO, user);
        inquiryRepository.save(inquiry);
        //when

        //then
    }
    
    @Test
    @DisplayName("단일 조회")
    void searchOne() {
        //given
        Inquiry inquiry = inquiryRepository.findById(Long.parseLong("3")).orElseThrow();
        //when
        System.out.println("inquiry = " + inquiry);
        //then
    }
}