package com.finalProject.stockbeginner.board.api;

import com.finalProject.stockbeginner.board.dto.requestDTO.BoardCheckRequestDTO;
import com.finalProject.stockbeginner.board.dto.requestDTO.BoardDeleteRequestDTO;
import com.finalProject.stockbeginner.board.dto.requestDTO.BoardRegisterRequestDTO;
import com.finalProject.stockbeginner.board.dto.requestDTO.BoardUpdateRequestDTO;
import com.finalProject.stockbeginner.board.dto.responseDTO.InquiryResponseDTO;
import com.finalProject.stockbeginner.board.dto.responseDTO.NoticeResponseDTO;
import com.finalProject.stockbeginner.board.service.InquiryBoardService;
import com.finalProject.stockbeginner.board.service.NoticeBoardService;
import com.finalProject.stockbeginner.user.auth.TokenUserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class BoardController {

    private final NoticeBoardService noticeBoardService;
    private final InquiryBoardService inquiryBoardService;

    //글 등록
    @PostMapping
    public ResponseEntity<?> boardResister(@AuthenticationPrincipal TokenUserInfo userInfo, @RequestBody BoardRegisterRequestDTO requestDTO){
        try {
            if(requestDTO.getType().equals("notice")) {
                noticeBoardService.noticeResister(requestDTO);
                return ResponseEntity.ok().body("공지 등록 성공");
            }else if(requestDTO.getType().equals("inquiry")){
                inquiryBoardService.inquiryResister(requestDTO);
                return ResponseEntity.ok().body("문의 등록 성공");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("등록 실패");
        }
        return ResponseEntity.badRequest().body("잘못된 요청 타입");
    }

    //전체 조회, 페이징
    @GetMapping("/{type}")
    public ResponseEntity<?> getBoardList(@PathVariable String type,
                                           Pageable pageable){
        try {
            if(type.equals("notice")){
                Page<NoticeResponseDTO> dtoPage = noticeBoardService.findAll(pageable);
                return ResponseEntity.ok().body(dtoPage);
            }else if(type.equals("inquiry")){
                Page<InquiryResponseDTO> dtoPage = inquiryBoardService.findAll(pageable);
                return ResponseEntity.ok().body(dtoPage);
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("조회 실패");
        }
            return ResponseEntity.badRequest().body("잘못된 요청");
    }

    //글 단일 조회
    @GetMapping("/{type}/:{id}")
    public ResponseEntity<?> getBoard(@PathVariable String type, @PathVariable String id){
        try {
            if(type.equals("notice")){
                NoticeResponseDTO responseDTO = noticeBoardService.findOne(id);
                return ResponseEntity.ok().body(responseDTO);
            }else if(type.equals("inquiry")){
                InquiryResponseDTO responseDTO = inquiryBoardService.findOne(id);
                return ResponseEntity.ok().body(responseDTO);
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("조회 실패");
        }
        return ResponseEntity.badRequest().body("잘못된 요청");
    }

    //글 수정
    @PatchMapping("/{type}")
    public ResponseEntity<?> updateBoard(@PathVariable String type, @RequestBody BoardUpdateRequestDTO requestDTO){
        try {
            if(type.equals("notice")){
                noticeBoardService.update(requestDTO);
                return ResponseEntity.ok().body("공지 수정 성공");
            }else if(type.equals("inquiry")){
                inquiryBoardService.update(requestDTO);
                return ResponseEntity.ok().body("문의 수정 성공");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("수정 실패");
        }
        return ResponseEntity.badRequest().body("잘못된 요청");
    }

    //글 삭제
    @DeleteMapping("/{type}")
    public ResponseEntity<?> deleteBoard(@PathVariable String type, @RequestBody BoardDeleteRequestDTO requestDTO){
        try {
            if(type.equals("notice")){
                noticeBoardService.delete(requestDTO.getId());
                return ResponseEntity.ok().body("공지 삭제 성공");
            }else if(type.equals("inquiry")){
                inquiryBoardService.delete(requestDTO.getId());
                return ResponseEntity.ok().body("문의 삭제 성공");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("삭제 실패");
        }
        return ResponseEntity.badRequest().body("잘못된 요청");
    }

    // 수정,삭제 시 비밀번호 확인
    @PostMapping("/check/{type}")
    public ResponseEntity<?> checkWriter(@PathVariable String type,@RequestBody BoardCheckRequestDTO requestDTO){
        if(type.equals("notice")){
            Boolean result = noticeBoardService.checkWriter(requestDTO.getId(), requestDTO.getPw());
            return ResponseEntity.ok().body(result);
        }else if(type.equals("inquiry")){
            Boolean result = inquiryBoardService.checkWriter(requestDTO.getId(), requestDTO.getPw());
            return ResponseEntity.ok().body(result);
        }
        return ResponseEntity.badRequest().body("잘못된 요청");
    }
}
