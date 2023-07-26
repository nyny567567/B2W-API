package com.finalProject.stockbeginner.board.service;

import com.finalProject.stockbeginner.board.dto.requestDTO.BoardRegisterRequestDTO;
import com.finalProject.stockbeginner.board.dto.requestDTO.BoardUpdateRequestDTO;
import com.finalProject.stockbeginner.board.dto.responseDTO.NoticeResponseDTO;
import com.finalProject.stockbeginner.board.entity.Notice;
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
public class NoticeBoardService {

    private final UserRepository userRepository;
    private final NoticeRepository noticeRepository;
    private final PasswordEncoder encoder;

    //공지 등록
    public void noticeResister(BoardRegisterRequestDTO requestDTO){
        User user = userRepository.findByEmail(requestDTO.getEmail()).orElseThrow();
        noticeRepository.save(new Notice(requestDTO,user));
    }

    //공지 전체 조회
    public Page<NoticeResponseDTO> findAll(Pageable pageable){
        return noticeRepository.findAllByOrderByDateDesc(pageable)
                .map(NoticeResponseDTO::new);
    }

    //공지 삭제
    public void delete(String id){
        noticeRepository.deleteById(Long.parseLong(id));
    }

    //공지 단일 조회
    public NoticeResponseDTO findOne(String id){
        Notice notice = noticeRepository.findById(Long.parseLong(id)).orElseThrow();
        return new NoticeResponseDTO(notice);
    }

    //공지 수정
    public void update(BoardUpdateRequestDTO updateRequestDTO){
        User user = userRepository.findByEmail(updateRequestDTO.getEmail()).orElseThrow();
        Notice notice = new Notice(updateRequestDTO, user);
        noticeRepository.save(notice);
    }

    public Boolean checkWriter(String id, String pw){
        Notice notice = noticeRepository.findById(Long.parseLong(id)).orElseThrow();
        return notice.getUser().getPassword().equals(encoder.encode(pw));
    }

}
