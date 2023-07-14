package com.finalProject.stockbeginner.user.service;

import com.finalProject.stockbeginner.exception.DuplicatedEmailException;
import com.finalProject.stockbeginner.exception.NoRegisteredArgumentsException;
import com.finalProject.stockbeginner.user.auth.TokenProvider;
import com.finalProject.stockbeginner.user.auth.TokenUserInfo;
import com.finalProject.stockbeginner.user.dto.UserUpdateDTO;
import com.finalProject.stockbeginner.user.dto.request.KakaoRegisterRequestDTO;
import com.finalProject.stockbeginner.user.dto.request.LoginRequestDTO;
import com.finalProject.stockbeginner.user.dto.request.UserRegisterRequestDTO;
import com.finalProject.stockbeginner.user.dto.response.KakaoLoginResponseDTO;
import com.finalProject.stockbeginner.user.dto.response.LoginResponseDTO;
import com.finalProject.stockbeginner.user.dto.response.UserRegisterResponseDTO;
import com.finalProject.stockbeginner.user.entity.User;
import com.finalProject.stockbeginner.user.repository.UserRepository;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.dynamic.DynamicType;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final TokenProvider tokenProvider;




    @Value("${upload.path}")
    private String uploadRootPath;
    private LoginResponseDTO kakaoDTO;


//    //카카오
//    public LoginResponseDTO getSingleResult(User user) {
//        LoginRequestDTO loginRequestDTO = new LoginRequestDTO(user.getEmail(),user.getPassword());
//        return kakaoAuthenticate(loginRequestDTO);
//
//    }

    //회원 가입
    public UserRegisterResponseDTO register(UserRegisterRequestDTO requestDTO, String uploadedFilePath)

            throws RuntimeException {

        String email = requestDTO.getEmail();

        if (isDuplicate(email)) {
            throw new DuplicatedEmailException("중복된 이메일 입니다.");
        }

        log.info("service password, {}", requestDTO.getPassword());
        requestDTO.setPassword(encoder.encode(requestDTO.getPassword()));
        requestDTO.setImage(uploadedFilePath);
        User saved = userRepository.save(requestDTO.toEntity());
        return new UserRegisterResponseDTO(saved);
    }

    //닉네임 중복검사
    public boolean isDuplicateNick(String nick) {
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

        if (!encoder.matches(rawPassword, encodedPassword)) {
            throw new RuntimeException("비밀번호가 틀렸습니다.");
        }


        String token = tokenProvider.createToken(user);

        return new LoginResponseDTO(user, token);
    }
    //카카오 회원용 토큰 발급
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
            throws NoRegisteredArgumentsException, IllegalStateException {
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


//카카오 받은 정보 가입하고 dto로

    public LoginResponseDTO kakaoLogin(String access_Token) {
        KakaoRegisterRequestDTO dto = new KakaoRegisterRequestDTO();
        String reqURL = "https://kapi.kakao.com/v2/user/me";
        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");

            //  요청에 필요한 Header에 포함될 내용
            conn.setRequestProperty("Authorization", "Bearer " + access_Token);

            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println("response body : " + result);

            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            JsonObject properties = element.getAsJsonObject().get("properties").getAsJsonObject();
            JsonObject kakao_account = element.getAsJsonObject().get("kakao_account").getAsJsonObject();

            long kakaoId = element.getAsJsonObject().get("id").getAsLong();
            dto.setKakaoId(kakaoId);
            String nickname = properties.getAsJsonObject().get("nickname").getAsString();
            dto.setNickname(nickname);
            String image = properties.getAsJsonObject().get("profile_image").getAsString();
            dto.setImage(image);
            String email = kakao_account.getAsJsonObject().get("email").getAsString();
            dto.setEmail(email);
            String gender = kakao_account.getAsJsonObject().get("gender").getAsString();
            dto.setGender(gender);
            String age = kakao_account.getAsJsonObject().get("age_range").getAsString();
            dto.setAge(age);
            System.out.println("카카오 로그인 저장 : " + dto);

            Optional<User> user = userRepository.findByKakaoId(kakaoId);

            LoginRequestDTO loginRequestDTO = null;
            if (user != null) { //가입한적 없으면
                User kakaoUser = dto.toEntity();
                userRepository.save(kakaoUser);  //레파지토리에 저장하고
                loginRequestDTO = new LoginRequestDTO();
                loginRequestDTO.setEmail(kakaoUser.getEmail());
                loginRequestDTO.setPassword(kakaoUser.getPassword());  //리퀘스트에 이메일 패스워드 넣어준다
                System.out.println("카카오 리퀘스트 : " + loginRequestDTO);

            }
            LoginResponseDTO kakaoDTO = kakaoAuthenticate(loginRequestDTO);
            System.out.println("카카오 리스폰스: " + kakaoDTO);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return kakaoDTO;
    }

    }



//            userInfo.put("id", id);
//            userInfo.put("nickname", nickname);
//            userInfo.put("email", email);
//            userInfo.put("name", name);
//            userInfo.put("image", image);
//            userInfo.put("gender", gender);
//            userInfo.put("age", age);

//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return dto;


    // 카카오 아이디로 중복체크 하고 회원가입 또는 진행

//    public LoginResponseDTO kakaoLogin(KakaoRegisterRequestDTO krrDTO) {
//        Optional<User> user = userRepository.findByKakaoId(krrDTO.getKakaoId());
//        if (user == null) {
//            User kakaoUser = new User();
//            kakaoUser = krrDTO.toEntity();
//            userRepository.save(kakaoUser);
//            LoginRequestDTO DTO = new LoginRequestDTO();
//            DTO.setEmail(kakaoUser.getEmail());
//            DTO.setPassword(kakaoUser.getPassword());
//            System.out.println("카카오 리퀘스트 : " + DTO);
//            return authenticate(DTO);
//
//        } else {
//            LoginRequestDTO DTO = new LoginRequestDTO();
//            DTO.setEmail(krrDTO.getEmail());
//            DTO.setPassword(krrDTO.getPassword());
//            return authenticate(DTO);
//
//        }
//    }
//}



//    User kakaoUser = userRepository.findByKakaoId(kakaoId)
//            .orElse(null);
//           // 패스워드 인코딩
//            String encodedPassword = PasswordEncoder.encode(password);
//            // ROLE = 사용자
//
//            kakaoUser = new User(username, encodedPassword, nickname, kakaoId);
//            userRepository.save(kakaoUser);
//        }

//        // 로그인 처리
//        Authentication kakaoUsernamePassword = new UsernamePasswordAuthenticationToken(username, password);
//        //  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException { 로 진행됨
//
//        Authentication authentication = authenticationManager.authenticate(kakaoUsernamePassword);
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        return username;
//    }


//
//        return dto;
//    }


//    // 카카오 로그인
//    @GetMapping("/callback/kakao")
//    public LoginResponseDTO kakaoLogin(@RequestParam(name = "code") String code) throws IOException {
//        log.info("카카오 API 서버 code : " + code);
//        return oAuthService.kakaoLogin(code);
//    }
////
////    //카카오 DTO
//        private KakaoUserInfoDTO getKakaoUserInfoDTO(String code) throws JsonProcessingException {
//        ResponseEntity<String> accessTokenResponse = kakaoOAuth.requestAccessToken(code);
//        KakaoOAuthTokenDTO oAuthToken = kakaoOAuth.getAccessToken(accessTokenResponse);
//        ResponseEntity<String> userInfoResponse = kakaoOAuth.requestUserInfo(oAuthToken);
//        KakaoUserInfoDTO kakaoUser = kakaoOAuth.getUserInfo(userInfoResponse);
//        return kakaoUser;
//    }
//
//    public void kakaoLogout(String access_Token) {
//        String reqURL = "https://kapi.kakao.com/v1/user/logout";
//        try {
//            URL url = new URL(reqURL);
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("POST");
//            conn.setRequestProperty("Authorization", "Bearer " + access_Token);
//
//            int responseCode = conn.getResponseCode();
//            System.out.println("responseCode : " + responseCode);
//
//            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//
//            String result = "";
//            String line = "";
//
//            while ((line = br.readLine()) != null) {
//                result += line;
//            }
//            System.out.println(result);
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//    }

//}