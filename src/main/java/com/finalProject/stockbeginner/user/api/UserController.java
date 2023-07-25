package com.finalProject.stockbeginner.user.api;

import com.finalProject.stockbeginner.exception.DuplicatedEmailException;
import com.finalProject.stockbeginner.exception.NoRegisteredArgumentsException;
import com.finalProject.stockbeginner.trade.dto.response.RankResponseDTO;
import com.finalProject.stockbeginner.user.auth.TokenUserInfo;
import com.finalProject.stockbeginner.user.dto.request.*;
import com.finalProject.stockbeginner.user.dto.response.FavoriteListResponseDTO;
import com.finalProject.stockbeginner.user.dto.response.LoginResponseDTO;
import com.finalProject.stockbeginner.user.dto.response.MyInfoResponseDTO;
import com.finalProject.stockbeginner.user.dto.response.UserRegisterResponseDTO;
import com.finalProject.stockbeginner.user.service.OAuthService;
import com.finalProject.stockbeginner.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    private final OAuthService oAuthService;

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

    //카카오 로그인
    @GetMapping("/callback/kakao")
    public ResponseEntity<?> login(@RequestParam("code") String code, HttpSession session) throws Exception {
        try {
            String access_Token = oAuthService.getKakaoAccessToken(code);
            System.out.println("login Controller : " + access_Token);
            LoginResponseDTO responseDTO = userService.kakaoLogin(access_Token);
            System.out.println("login Controller response dto : " + responseDTO);
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }

    // 아이디 찾기
    @PostMapping("/searchId")
    public String searchId(@Validated @RequestBody SearchIdRequestDTO dto) {
        try {
            log.info(dto.toString());
            String email
                    = userService.searchId(dto);
            log.info("컨트롤러 이메일 :" + email);
            return email;
        } catch (Exception e) {
            e.printStackTrace();
            return "일치하는 회원 정보가 없음";
        }
    }

    //비밀번호 변경
//    @Transactional
    @PostMapping("/sendEmail")
    public String sendEmail(@Validated @RequestBody ChangePasswordRequestDTO dto) {
        try {
            if (userService.sendEmail(dto) != null) {
                MailDTO maildto = userService.createMailAndChangePassword(dto);
                userService.mailSend(maildto);
                String answer = "이메일로 임시비밀번호가 발송되었습니다";
                return answer;
            }
            String answer = "일치하는 회원정보가 없습니다";
            return answer;
        } catch (Exception e) {
            e.printStackTrace();
            String answer = "다시 시도해주세요";
            return answer;
        }
    }


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
              String filePath
                    = userService.findProfilePath(userInfo.getUserId());
            File profileFile = new File(filePath);
            if (!profileFile.exists()) {
                return ResponseEntity.notFound().build();
            }
            byte[] fileData = FileCopyUtils.copyToByteArray(profileFile);
            HttpHeaders headers = new HttpHeaders();
            MediaType contentType = findExtensionAndGetMediaType(filePath);
            if (contentType == null) {
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
        String ext
                = filePath.substring(filePath.lastIndexOf(".") + 1);
        switch (ext.toUpperCase()) {
            case "JPG":
            case "JPEG":
                return MediaType.IMAGE_JPEG;
            case "PNG":
                return MediaType.IMAGE_PNG;
            case "GIF":
                return MediaType.IMAGE_GIF;
            default:
                return null;
        }
    }

    //닉네임 중복 확인
    @GetMapping("/checknick")
    public ResponseEntity<?> checkNick(String nick) {
        if (nick.trim().equals("")) {
            return ResponseEntity.badRequest()
                    .body("닉네임이 없습니다!");
        }
        boolean resultFlag = userService.isDuplicateNick(nick);
        return ResponseEntity.ok().body(resultFlag);
    }

    //이메일 중복 확인
    @GetMapping("/check")
    public ResponseEntity<?> check(String email) {
        if (email.trim().equals("")) {
            return ResponseEntity.badRequest()
                    .body("이메일이 없습니다!");
        }
        boolean resultFlag = userService.isDuplicate(email);
        return ResponseEntity.ok().body(resultFlag);
    }


    //즐겨찾기 추가(별 모양 클릭)
    @PostMapping("/favorite")
    public ResponseEntity<?> FavoriteToggle(@RequestBody FavoriteRequestDTO requestDTO) {
        try {
            List<FavoriteListResponseDTO> result = userService.favoriteToggle(requestDTO);
            return ResponseEntity.ok().body(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //내정보 받아오기
    @GetMapping("/myInfo/{email}")
    public ResponseEntity<?> getUserInfo(@PathVariable String email) {
        MyInfoResponseDTO myInfo = userService.getMyInfo(email);
        return ResponseEntity.ok().body(myInfo);
    }


    //즐겨찾기 리스트 불러오기
    @GetMapping("/favorite/{email}")
    public ResponseEntity<?> favoriteList(@PathVariable String email) {
        try {
            List<FavoriteListResponseDTO> favoriteStockList = userService.favoriteList(email);
            return ResponseEntity.ok().body(favoriteStockList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}
