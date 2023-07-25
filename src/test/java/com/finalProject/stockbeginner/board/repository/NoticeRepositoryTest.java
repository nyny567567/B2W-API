package com.finalProject.stockbeginner.board.repository;

import com.finalProject.stockbeginner.board.dto.requestDTO.BoardRegisterRequestDTO;
import com.finalProject.stockbeginner.board.entity.Notice;
import com.finalProject.stockbeginner.user.entity.User;
import com.finalProject.stockbeginner.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class NoticeRepositoryTest {

    @Autowired
    NoticeRepository noticeRepository;
    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("게시글 작성")
    void writeNotice() {
        //given
        String email = "kingminsu@naver.com";
        String title = "모의투자 시 주의사항";
        String content = "남탓 금지";
        User user = userRepository.findByEmail(email).orElseThrow();

        BoardRegisterRequestDTO requestDTO = BoardRegisterRequestDTO.builder().writer(user.getName()).content(content).title(title).build();
        Notice notice = new Notice(requestDTO, user);
        System.out.println("notice = " + notice);
        //when
        noticeRepository.save(notice);

        //then

    }
    @Test
    @DisplayName("조회")
    void searchAll() {
        //given

        //when
        List<Notice> all = noticeRepository.findAll(Sort.by(Sort.Direction.DESC, "date"));
        System.out.println("all = " + all);
        //then
    }

    @Test
    @DisplayName("삭제")
    void delete() {
        //given
        noticeRepository.deleteById("5");
        //when

        //then
    }

}