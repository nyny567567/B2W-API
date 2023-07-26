package com.finalProject.stockbeginner.board.repository;

import com.finalProject.stockbeginner.board.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    Page<Notice> findAllByOrderByDateDesc(Pageable pageable);


}
