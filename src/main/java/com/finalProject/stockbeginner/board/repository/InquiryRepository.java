package com.finalProject.stockbeginner.board.repository;

import com.finalProject.stockbeginner.board.entity.Inquiry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InquiryRepository extends JpaRepository<Inquiry, String> {

    Page<Inquiry> findAllByOrderByDateDesc(Pageable pageable);
}
