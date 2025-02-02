package com.finalProject.stockbeginner.user.api;

import com.finalProject.stockbeginner.exception.DuplicatedEmailException;
import com.finalProject.stockbeginner.exception.NoRegisteredArgumentsException;
import com.finalProject.stockbeginner.user.auth.KakaoOAuth;
import com.finalProject.stockbeginner.user.auth.TokenUserInfo;
import com.finalProject.stockbeginner.user.dto.UserUpdateDTO;
import com.finalProject.stockbeginner.user.dto.request.LoginRequestDTO;
import com.finalProject.stockbeginner.user.dto.request.UserRegisterRequestDTO;
import com.finalProject.stockbeginner.user.dto.response.LoginResponseDTO;
import com.finalProject.stockbeginner.user.dto.response.UserRegisterResponseDTO;
import com.finalProject.stockbeginner.user.service.OAuthService;
import com.finalProject.stockbeginner.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final OAuthService oAuthService;
    private final KakaoOAuth kakaoOAuth;




    //카카오 로그인
    @GetMapping("/kakao")
    public void getKakaoAuthUrl(HttpServletResponse response) throws IOException {
        response.sendRedirect(kakaoOAuth.responseUrl());
    }

    //로그인 요청 처리
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Validated @RequestBody LoginRequestDTO dto
    ) {
        try {
            LoginResponseDTO responseDTO
                    = userService.authenticate(dto);

            return ResponseEntity.ok().body(responseDTO);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(e.getMessage());

        }
    }


//    //카카오 로그인
//    @GetMapping("/callback/kakao")
//    public String login(@RequestParam("code") String code, HttpSession session) throws Exception {
//        String access_Token = oAuthService.getKakaoAccessToken(code);
////        userService.createKakaoUser(access_Token);
//        HashMap<String, Object> userInfo = userService.getUserInfo(access_Token);
//        System.out.println("login Controller : " + userInfo);
//        userService.giveTokenToKakao(userInfo);
//
//
////            클라이언트의 이메일이 존재할 때 세션에 해당 이메일과 토큰 등록
////        if (userInfo.get("email") != null) {
////            session.setAttribute("userId", userInfo.get("email"));
////            session.setAttribute("access_Token", access_Token);
////        }
//
//
//        return "로그인에 성공했습니다.";
//    }


    //회원 가입
    @PostMapping
    public ResponseEntity<?> register(@RequestPart("user") UserRegisterRequestDTO requestDTO,
                                      @RequestPart(value = "profileImage", required = false) MultipartFile profileImg,
                                      BindingResult result) {

        {
            log.info("/api/auth POST - {}", requestDTO);

            if (result.hasErrors()) {
                log.warn(result.toString());
                return ResponseEntity.badRequest()
                        .body(result.getFieldError());
            }

            try {

                String uploadedFilePath = null;
                if (profileImg != null) {
                    log.info("attached file name: {}", profileImg.getOriginalFilename());
                    uploadedFilePath = userService.uploadProfileImage(profileImg);
                }


                UserRegisterResponseDTO responseDTO = userService.register(requestDTO, uploadedFilePath);
                return ResponseEntity.ok()
                        .body(responseDTO);

            } catch (NoRegisteredArgumentsException e) {
                log.warn("필수 가입 정보를 전달받지 못했습니다.");
                return ResponseEntity.badRequest()
                        .body(e.getMessage());
            } catch (DuplicatedEmailException e) {
                log.warn("이메일이 중복되었습니다.");
                return ResponseEntity.badRequest()
                        .body(e.getMessage());
            } catch (Exception e) {
                log.warn("기타 예외가 발생했습니다.");
                e.printStackTrace();
                return ResponseEntity.internalServerError().build();
            }
        }
    }


    //프로필 이미지 관련
    // 프로필 사진 이미지 데이터를 클라이언트에게 응답 처리
    @GetMapping("/load-profile")
    public ResponseEntity<?> loadFile(
            @AuthenticationPrincipal TokenUserInfo userInfo
    ) {
        log.info("/api/auth/load-profile - GET!, user: {}", userInfo.getEmail());

        try {
            //클라이언트가 요청한 프로필 사진을 응답해야 함.
            //1. 프로필 사진의 경로를 얻어야 함.
            String filePath
                    = userService.findProfilePath(userInfo.getUserId());

            //2. 얻어낸 파일 경로를 통해서 실제 파일 데이터 로드하기
            File profileFile = new File(filePath);

            if (!profileFile.exists()) {
                return ResponseEntity.notFound().build();
            }

            // 해당 경로에 저장된 파일을 바이트배열로 직렬화 해서 리턴
            byte[] fileData = FileCopyUtils.copyToByteArray(profileFile);

            //3. 응답 헤더에 컨턴츠 타입을 설정.
            HttpHeaders headers = new HttpHeaders();
            MediaType contentType = findExtensionAndGetMediaType(filePath);
            if(contentType == null) {
                return ResponseEntity.internalServerError()
                        .body("발견된 파일은 이미지 파일이 아닙니다.");
            }
            headers.setContentType(contentType);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(fileData);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("파일을 찾을 수 없습니다.");
        }

    }

    private MediaType findExtensionAndGetMediaType(String filePath) {

        // 파일 경로에서 확장자 추출하기
        // C:/todo_upload/asjkldlkaslkdjc_abc.jpg
        String ext
                = filePath.substring(filePath.lastIndexOf(".") + 1);

        switch (ext.toUpperCase()) {
            case "JPG": case "JPEG":
                return MediaType.IMAGE_JPEG;
            case "PNG":
                return MediaType.IMAGE_PNG;
            case "GIF":
                return MediaType.IMAGE_GIF;
            default:
                return null;
        }

    }


    //
//        log.info("controller password, {}",requestDTO.getPassword());
//        log.info("requestDTO: " + requestDTO);
//        try {
//            UserRegisterResponseDTO responseDTO = userService.register(requestDTO);
//            return ResponseEntity.ok().body(responseDTO);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }
//
    //닉네임 중복 확인
    @GetMapping("/checknick")
    public ResponseEntity<?> checkNick(String nick) {
        if(nick.trim().equals("")) {
            return ResponseEntity.badRequest()
                    .body("닉네임이 없습니다!");
        }
        boolean resultFlag = userService.isDuplicateNick(nick);
        return ResponseEntity.ok().body(resultFlag);

    }

    //이메일 중복 확인
    @GetMapping("/check")
    public ResponseEntity<?> check(String email) {
        if(email.trim().equals("")) {
            return ResponseEntity.badRequest()
                    .body("이메일이 없습니다!");
        }
        boolean resultFlag = userService.isDuplicate(email);
        return ResponseEntity.ok().body(resultFlag);

    }


    //회원 수정
    @PatchMapping
    public ResponseEntity<?> updateInfo(@Valid @RequestBody UserUpdateDTO dto, TokenUserInfo userInfo) {
            try {
              LoginResponseDTO responseDTO = userService.updateInfo(dto, userInfo);
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }



    //회원 탈퇴
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(TokenUserInfo userInfo) {
       userService.deleteUser(userInfo);

        return ResponseEntity.noContent().build();
    }
}

