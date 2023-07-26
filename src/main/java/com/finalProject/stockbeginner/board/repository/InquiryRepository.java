package com.finalProject.stockbeginner.board.repository;

import com.finalProject.stockbeginner.board.entity.Inquiry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

    Page<Inquiry> findAllByOrderByDateDesc(Pageable pageable);

}
