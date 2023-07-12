package com.finalProject.stockbeginner.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.finalProject.stockbeginner.exception.DuplicatedEmailException;
import com.finalProject.stockbeginner.exception.NoRegisteredArgumentsException;
import com.finalProject.stockbeginner.user.auth.KakaoOAuth;
import com.finalProject.stockbeginner.user.auth.KakaoOAuthTokenDTO;
import com.finalProject.stockbeginner.user.auth.TokenProvider;
import com.finalProject.stockbeginner.user.auth.TokenUserInfo;
import com.finalProject.stockbeginner.user.dto.KakaoUserInfoDTO;
import com.finalProject.stockbeginner.user.dto.UserUpdateDTO;
import com.finalProject.stockbeginner.user.dto.request.LoginRequestDTO;
import com.finalProject.stockbeginner.user.dto.request.UserRegisterRequestDTO;
import com.finalProject.stockbeginner.user.dto.response.LoginResponseDTO;
import com.finalProject.stockbeginner.user.dto.response.UserRegisterResponseDTO;
import com.finalProject.stockbeginner.user.entity.User;
import com.finalProject.stockbeginner.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final TokenProvider tokenProvider;
    private final OAuthService oAuthService;
    private final KakaoOAuth kakaoOAuth;

    @Value("${upload.path}")
    private String uploadRootPath;


    //카카오
    public LoginResponseDTO getSingleResult(User user) {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO(user.getEmail(),user.getPassword());
        return kakaoAuthenticate(loginRequestDTO);

    }

    //회원 가입
    public UserRegisterResponseDTO register(UserRegisterRequestDTO requestDTO, String uploadedFilePath)

            throws RuntimeException {

        String email = requestDTO.getEmail();

        if(isDuplicate(email)) {
            throw new DuplicatedEmailException("중복된 이메일 입니다.");
        }

        log.info("service password, {}",requestDTO.getPassword());
        requestDTO.setPassword(encoder.encode(requestDTO.getPassword()));
        requestDTO.setImage(uploadedFilePath);
        User saved = userRepository.save(requestDTO.toEntity());
        return new UserRegisterResponseDTO(saved);
    }

    //닉네임 중복검사
    public boolean isDuplicateNick (String nick) {
        return userRepository.existsByNick(nick);
    }


    //이메일 중복검사
    public boolean isDuplicate(String email) {

        return userRepository.existsByEmail(email);
    }

    // 회원 인증
    public LoginResponseDTO authenticate(LoginRequestDTO dto) {


        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(
                        () -> new RuntimeException("가입된 회원이 아닙니다!")
                );
        //패스워드 검증
        String rawPassword = dto.getPassword(); // 입력 비번
        String encodedPassword = user.getPassword(); // DB에 저장된 비번

        if(!encoder.matches(rawPassword, encodedPassword)) {
            throw new RuntimeException("비밀번호가 틀렸습니다.");
        }


        String token = tokenProvider.createToken(user);

        return new LoginResponseDTO(user, token);
    }

    public LoginResponseDTO kakaoAuthenticate(LoginRequestDTO dto) {


        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(
                        () -> new RuntimeException("가입된 회원이 아닙니다!")
                );

        String token = tokenProvider.createToken(user);

        return new LoginResponseDTO(user, token);
    }


    //회원정보수정

    @Transactional
    public LoginResponseDTO updateInfo(UserUpdateDTO dto, TokenUserInfo userInfo) {
        User user = userRepository
                .findById(userInfo.getUserId())
                .orElseThrow(() -> new RuntimeException("로그인 유저 정보가 없습니다."));

        user.setPassword(dto.getPassword());
        user.setNick(dto.getNick());
        user.setImage(dto.getImage());

        String token = tokenProvider.createToken(user);

        return new LoginResponseDTO(user, token);

    }


    //회원 탈퇴
    @Transactional
        public void deleteUser(TokenUserInfo userInfo)
            throws NoRegisteredArgumentsException, IllegalStateException
    {
                if (userInfo.getUserId() == null) {
                throw new RuntimeException("로그인 유저 정보가 없습니다.");
            }
        userRepository.deleteById(userInfo.getUserId());
        }

        //프로필 사진 업로드
         public String uploadProfileImage(MultipartFile originalFile) throws IOException {

        //루트 디렉토리가 존재하는 지 확인 후 존재하지 않으면 생성
        File rootDir = new File(uploadRootPath);
        if (!rootDir.exists()) rootDir.mkdir();

        // 파일명을 유니크하게 변경
        String uniqueFileName = UUID.randomUUID()
                + "_" + originalFile.getOriginalFilename();

        // 파일을 저장
        File uploadFile = new File(uploadRootPath + "/" + uniqueFileName);
        originalFile.transferTo(uploadFile);

        return uniqueFileName;
    }


    public String findProfilePath(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow();
        return uploadRootPath + "/" + user.getImage();
    }


    //카카오 로그인 토큰으로 정보받기
//    public void createKakaoUser(String token) throws Exception {
//
//        String reqURL = "https://kapi.kakao.com/v2/user/me";
//
//        //access_token을 이용하여 사용자 정보 조회
//        try {
//            URL url = new URL(reqURL);
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//
//            conn.setRequestMethod("POST");
//            conn.setDoOutput(true);
//            conn.setRequestProperty("Authorization", "Bearer " + token); //전송할 header 작성, access_token전송
//
//            //결과 코드가 200이라면 성공
//            int responseCode = conn.getResponseCode();
//            System.out.println("responseCode : " + responseCode);
//
//            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
//            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//            String line = "";
//            String result = "";
//
//            while ((line = br.readLine()) != null) {
//                result += line;
//            }
//            System.out.println("response body : " + result);
//
////            //Gson 라이브러리로 JSON파싱
////            JsonParser parser = new JsonParser();
////            JsonElement element = parser.parse(result);
////
////            JsonObject properties = element.getAsJsonObject().get("properties").getAsJsonObject();
////            JsonObject kakao_account = element.getAsJsonObject().get("kakao_account").getAsJsonObject();
////
////            String nickname = properties.getAsJsonObject().get("nickname").getAsString();
////            String email = kakao_account.getAsJsonObject().get("email").getAsString();
////            String image = kakao_account.getAsJsonObject().get("image").getAsString();
////            String gender = kakao_account.getAsJsonObject().get("gender").getAsString();
////            String age = kakao_account.getAsJsonObject().get("age_range").getAsString();
////
////            userRepository.put("nickname", nickname);
////            userRepository.put("email", email);
////
//////            int id = element.getAsJsonObject().get("id").getAsInt();
//////            String nick = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("nickname").getAsString();
//////            String image = element.getAsJsonObject().get("kakao_profile").getAsJsonObject().get("image").getAsString();
//////            boolean hasEmail = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("has_email").getAsBoolean();
//////            boolean hasGender = element.getAsJsonObject().get("kakao").getAsJsonObject().get("has_gender").getAsBoolean();
//////            boolean hasAge = element.getAsJsonObject().get("kakao").getAsJsonObject().get("has_age_range").getAsBoolean();
//////            String email = "";
//////            if(hasEmail){
//////                email = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("email").getAsString();
//////            }
//////            String gender = "";
//////            if(hasGender){
//////                gender = element.getAsJsonObject().get("kakao").getAsJsonObject().get("gender").getAsString();
//////            }
//////
//////            System.out.println("id : " + id);
//////            System.out.println("email : " + email);
//////
//////            br.close();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

//    //카카오 로그인 정보 저장
//    public HashMap<String, Object> getUserInfo (String access_Token) {
//
//        //    요청하는 클라이언트마다 가진 정보가 다를 수 있기에 HashMap타입으로 선언
//        HashMap<String, Object> userInfo = new HashMap<>();
//        String reqURL = "https://kapi.kakao.com/v2/user/me";
//        try {
//            URL url = new URL(reqURL);
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("POST");
//
//            //    요청에 필요한 Header에 포함될 내용
//            conn.setRequestProperty("Authorization", "Bearer " + access_Token);
//
//            int responseCode = conn.getResponseCode();
//            System.out.println("responseCode : " + responseCode);
//
//            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//
//            String line = "";
//            String result = "";
//
//            while ((line = br.readLine()) != null) {
//                result += line;
//            }
//            System.out.println("response body : " + result);
//
//            JsonParser parser = new JsonParser();
//            JsonElement element = parser.parse(result);
//
//            JsonObject properties = element.getAsJsonObject().get("properties").getAsJsonObject();
//            JsonObject kakao_account = element.getAsJsonObject().get("kakao_account").getAsJsonObject();
//
//            int id = element.getAsJsonObject().get("id").getAsInt();
//            String nickname = properties.getAsJsonObject().get("nickname").getAsString();
//            String image = properties.getAsJsonObject().get("profile_image").getAsString();
//            String email = kakao_account.getAsJsonObject().get("email").getAsString();
//            String gender = kakao_account.getAsJsonObject().get("gender").getAsString();
////            String age = kakao_account.getAsJsonObject().get("age_range").getAsString();
//
//            userInfo.put("id", id);
//            userInfo.put("nickname", nickname);
//            userInfo.put("email", email);
//            userInfo.put("image", image);
//            userInfo.put("gender", gender);
////            userInfo.put("age", age);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return userInfo;
//    }

    // 카카오 로그인
    @GetMapping("/callback/kakao")
    public LoginResponseDTO kakaoLogin(@RequestParam(name = "code") String code) throws IOException {
        log.info("카카오 API 서버 code : " + code);
        return oAuthService.kakaoLogin(code);
    }
//
//    //카카오 DTO
        private KakaoUserInfoDTO getKakaoUserInfoDTO(String code) throws JsonProcessingException {
        ResponseEntity<String> accessTokenResponse = kakaoOAuth.requestAccessToken(code);
        KakaoOAuthTokenDTO oAuthToken = kakaoOAuth.getAccessToken(accessTokenResponse);
        ResponseEntity<String> userInfoResponse = kakaoOAuth.requestUserInfo(oAuthToken);
        KakaoUserInfoDTO kakaoUser = kakaoOAuth.getUserInfo(userInfoResponse);
        return kakaoUser;
    }

}