package com.finalProject.stockbeginner.board.service;

import com.finalProject.stockbeginner.board.dto.requestDTO.BoardRegisterRequestDTO;
import com.finalProject.stockbeginner.board.dto.requestDTO.BoardUpdateRequestDTO;
import com.finalProject.stockbeginner.board.dto.responseDTO.InquiryResponseDTO;
import com.finalProject.stockbeginner.board.dto.responseDTO.NoticeResponseDTO;
import com.finalProject.stockbeginner.board.entity.Inquiry;
import com.finalProject.stockbeginner.board.entity.Notice;
import com.finalProject.stockbeginner.board.repository.InquiryRepository;
import com.finalProject.stockbeginner.board.repository.NoticeRepository;
import com.finalProject.stockbeginner.user.entity.User;
import com.finalProject.stockbeginner.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class InquiryBoardService {

    private final UserRepository userRepository;
    private final InquiryRepository inquiryRepository;
    private final PasswordEncoder encoder;

    //문의 등록
    public void inquiryResister(BoardRegisterRequestDTO requestDTO){
        User user = userRepository.findByEmail(requestDTO.getEmail()).orElseThrow();
        inquiryRepository.save(new Inquiry(requestDTO,user));
    }

    //문의 전체 조회
    public Page<InquiryResponseDTO> findAll(Pageable pageable){
        return inquiryRepository.findAllByOrderByDateDesc(pageable)
                .map(InquiryResponseDTO::new);
    }

    //문의 삭제
    public void delete(String id){
        inquiryRepository.deleteById(id);
    }

    //문의 단일 조회
    public InquiryResponseDTO findOne(String id){
        Inquiry inquiry = inquiryRepository.findById(id).orElseThrow();
        return new InquiryResponseDTO(inquiry);
    }

    //문의 수정
    public void update(BoardUpdateRequestDTO updateRequestDTO){
        User user = userRepository.findByEmail(updateRequestDTO.getEmail()).orElseThrow();
        Inquiry inquiry = new Inquiry(updateRequestDTO, user);
        inquiryRepository.save(inquiry);
    }

    public Boolean checkWriter(String id, String pw){
        Inquiry inquiry = inquiryRepository.findById(id).orElseThrow();
        return inquiry.getUser().getPassword().equals(encoder.encode(pw));
    }
}
