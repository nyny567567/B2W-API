package com.finalProject.stockbeginner.user.service;

import com.finalProject.stockbeginner.exception.DuplicatedEmailException;
import com.finalProject.stockbeginner.exception.NoRegisteredArgumentsException;
import com.finalProject.stockbeginner.trade.dto.response.RankResponseDTO;
import com.finalProject.stockbeginner.trade.entity.Stock;
import com.finalProject.stockbeginner.trade.repository.StockRepository;
import com.finalProject.stockbeginner.user.auth.TokenProvider;
import com.finalProject.stockbeginner.user.dto.request.*;
import com.finalProject.stockbeginner.user.dto.request.FavoriteRequestDTO;
import com.finalProject.stockbeginner.user.dto.request.KakaoRegisterRequestDTO;
import com.finalProject.stockbeginner.user.dto.request.LoginRequestDTO;
import com.finalProject.stockbeginner.user.dto.request.UserRegisterRequestDTO;
import com.finalProject.stockbeginner.user.dto.response.FavoriteListResponseDTO;
import com.finalProject.stockbeginner.user.dto.response.LoginResponseDTO;
import com.finalProject.stockbeginner.user.dto.response.MyInfoResponseDTO;
import com.finalProject.stockbeginner.user.dto.response.UserRegisterResponseDTO;
import com.finalProject.stockbeginner.user.entity.FavoriteStock;
import com.finalProject.stockbeginner.user.entity.User;
import com.finalProject.stockbeginner.user.repository.FavoriteStockRepository;
import com.finalProject.stockbeginner.user.repository.UserRepository;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Email;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final TokenProvider tokenProvider;
    private final FavoriteStockRepository favoriteStockRepository;
    private final StockRepository stockRepository;
    private final JavaMailSender mailSender;


    @Value("${upload.path}")
    private String uploadRootPath;

    //리다이렉트
    @GetMapping("/redirect")
    public ResponseEntity<?> redirect() {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("http://localhost:3000/"));
        return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
    }



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
        token= org.springframework.web.util.HtmlUtils.htmlEscape(token); //특수문자처리


        return new LoginResponseDTO(user, token);
    }

    //아이디 찾기
    public String searchId(SearchIdRequestDTO dto) {


        User user = userRepository.findByPhoneNumber(dto.getPhoneNumber());
        log.info("아이디찾기 1차 검증: " + String.valueOf(user));
        if (user != null) {
            if (Objects.equals(user.getName(), dto.getName())) {
                String email = user.getEmail();
                log.info("이메일 :" + email);
                return email;
            }
            return "일치하는 회원 정보가 없음";

        }
        return "일치하는 회원 정보가 없음";
    }

    //비밀번호 변경 전화번호 이메일 일치하는지
    public String sendEmail(ChangePasswordRequestDTO dto) {


        User user = userRepository.findByPhoneNumber(dto.getPhoneNumber());
        log.info("비번 변경 1차 검증: " + String.valueOf(user));
        if (user != null) {
            if(Objects.equals(user.getEmail(), dto.getEmail())){
                return user.getEmail();
            }
            return null;

        }
        return null;
    }


    public MailDTO createMailAndChangePassword(ChangePasswordRequestDTO cprdto) {

                String str = getTempPassword();
                MailDTO dto = new MailDTO();
                dto.setAddress(cprdto.getEmail());
                dto.setTitle(" Beginner Big Winner 임시비밀번호 안내 이메일 입니다.");
                dto.setMessage("안녕하세요." + " 회원님의 임시 비밀번호는 "
                        + str + " 입니다." + " 로그인 후에 비밀번호를 변경을 해주세요");
                updatePassword(str, cprdto);
                return dto;
            }



    //임시 비밀번호로 업데이트

    public void updatePassword(String str, ChangePasswordRequestDTO cprdto) {
        String tempPassword = str;
        User user = userRepository.findByPhoneNumber(cprdto.getPhoneNumber());
        user.setPassword(encoder.encode(tempPassword));
        userRepository.save(user);

    }


    //랜덤함수로 임시비밀번호 구문 만들기
    public String getTempPassword(){
        char[] charSet = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
                'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

        String str = "";

        // 문자 배열 길이의 값을 랜덤으로 10개를 뽑아 구문을 작성함
        int idx = 0;
        for (int i = 0; i < 10; i++) {
            idx = (int) (charSet.length * Math.random());
            str += charSet[idx];
        }
        return str;
    }

    // 메일보내기
    public void mailSend(MailDTO mailDTO) {
        System.out.println("전송 완료!");
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(mailDTO.getAddress());
        message.setSubject(mailDTO.getTitle());
        message.setText(mailDTO.getMessage());
        message.setFrom("ny567@naver.com");
        message.setReplyTo("ny567@naver.com");
        System.out.println("message"+message);
        mailSender.send(message);
    }



//    //회원정보수정
//
//    @Transactional
//    public void updateInfo(UserUpdateDTO dto, TokenUserInfo userInfo) {
//        User user = userRepository
//                .findById(userInfo.getUserId())
//                .orElseThrow(() -> new RuntimeException("로그인 유저 정보가 없습니다."));
//
//        user.setPassword(dto.getPassword());
//        user.setNick(dto.getNick());
//        user.setImage(dto.getImage());
//
//        userRepository.delete(user);
//        userRepository.save(user);
//
//    }
//
//
//    //회원 탈퇴
//    @Transactional
//    public void deleteUser(TokenUserInfo userInfo)
//            throws NoRegisteredArgumentsException, IllegalStateException {
//        if (userInfo.getUserId() == null) {
//            throw new RuntimeException("로그인 유저 정보가 없습니다.");
//        }
//        userRepository.deleteById(userInfo.getUserId());
//    }

        //프로필 사진 업로드
        public String uploadProfileImage (MultipartFile originalFile) throws IOException {

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


        public String findProfilePath (String userId){
            User user = userRepository.findById(userId)
                    .orElseThrow();
            return uploadRootPath + "/" + user.getImage();
        }

        @ResponseBody
//카카오 받은 정보 가입하고 dto로
        public LoginResponseDTO kakaoLogin (String access_Token){
            KakaoRegisterRequestDTO dto = new KakaoRegisterRequestDTO();
            String reqURL = "https://kapi.kakao.com/v2/user/me";
            try {
                URL url = new URL(reqURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

//            //  요청에 필요한 Header에 포함될 내용
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
                String email = kakao_account.getAsJsonObject().get("email").getAsString();
                email = org.springframework.web.util.HtmlUtils.htmlEscape(email); //특수문자처리
                dto.setEmail((email));


                if (userRepository.existsByKakaoId(kakaoId)) { //카카오 가입 이미 한 사람이면
                    LoginRequestDTO loginRequestDTO = new LoginRequestDTO();
                    loginRequestDTO.setEmail(dto.getEmail());
                    loginRequestDTO.setPassword(dto.getPassword());
                    loginRequestDTO.setImage(dto.getImage());
                    LoginResponseDTO kakaoDTO = kakaoAuthenticate(loginRequestDTO); //토큰발급
                    System.out.println("카카오 리스폰스: " + kakaoDTO);
                    return kakaoDTO;
                } else { //가입안하고
                    if (email == null) { //이메일 제공동의안하면
                        redirect();
                    } else {  //동의했는데
                        if (userRepository.existsByEmail(email)) { //기존에 일반회원으로 가입한 사람
                            redirect();
                        } else {//가입안했으면 가입시켜준다
                            String nickname = properties.getAsJsonObject().get("nickname").getAsString();
                            dto.setNickname(nickname);
                            String image = properties.getAsJsonObject().get("profile_image").getAsString();
                            dto.setImage(image);
                            String gender = kakao_account.getAsJsonObject().get("gender").getAsString();
                            dto.setGender(gender);
                            String age = kakao_account.getAsJsonObject().get("age_range").getAsString();
                            dto.setAge(age);
                            System.out.println("카카오 로그인 저장 : " + dto);
                            User kakaoUser = dto.toEntity();
                            userRepository.save(kakaoUser);  //레파지토리에 저장하고
                            LoginRequestDTO loginRequestDTO = new LoginRequestDTO();
                            loginRequestDTO.setEmail(kakaoUser.getEmail());
                            loginRequestDTO.setPassword(kakaoUser.getPassword()); //리퀘스트에 이메일 패스워드 넣어준다
                            System.out.println("카카오 리퀘스트 : " + loginRequestDTO);

                            LoginResponseDTO kakaoDTO = kakaoAuthenticate(loginRequestDTO); //토큰발급
                            System.out.println("카카오 리스폰스: " + kakaoDTO);
                            return kakaoDTO;

                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }


    public List<FavoriteListResponseDTO> favoriteToggle(FavoriteRequestDTO requestDTO) {
        User user = userRepository.findByEmail(requestDTO.getUserEmail()).orElseThrow();
        Integer resultCnt = favoriteStockRepository.existsByUserAndStock(user, requestDTO.getStockCode());
        if(resultCnt>0){
            List<FavoriteStock> byUserAndStockCode = favoriteStockRepository.findByUserAndStockCode(user, requestDTO.getStockCode());
            favoriteStockRepository.deleteAll(byUserAndStockCode);
        } else {
            FavoriteStock saved = favoriteStockRepository.save(FavoriteStock.builder()
                    .stockCode(requestDTO.getStockCode())
                    .stockName(requestDTO.getStockName())
                    .user(user)
                    .build());
        }

        List<FavoriteStock> list = favoriteStockRepository.findByUser(user);
        List<FavoriteListResponseDTO> responseDTOList = new ArrayList<>();

        for (FavoriteStock favoriteStock : list) {
            FavoriteListResponseDTO dto = FavoriteListResponseDTO.builder()
                    .stockCode(favoriteStock.getStockCode())
                    .stockName(favoriteStock.getStockName())
                    .build();
            responseDTOList.add(dto);
        }
        return responseDTOList;

    }

    public MyInfoResponseDTO getMyInfo(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        List<Stock> stockList = stockRepository.getByUser(user);
        return new MyInfoResponseDTO(user,stockList);
    }

    public List<RankResponseDTO> getRank(){
        List<User> ranks = userRepository.findAllByOrderByMoneyDesc();
        List<RankResponseDTO> responseDTOS = new ArrayList<>();
        Long i = 1L;
        for (User rank : ranks) {
            RankResponseDTO dto = RankResponseDTO.builder()
                    .rank(i).userName(rank.getName()).money(rank.getMoney())
                    .build();
            i++;
            responseDTOS.add(dto);
        }
        return responseDTOS;
    }

    public List<FavoriteListResponseDTO> favoriteList(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        List<FavoriteStock> list = favoriteStockRepository.findByUser(user);
        List<FavoriteListResponseDTO> responseDTOList = new ArrayList<>();

        for (FavoriteStock favoriteStock : list) {
            FavoriteListResponseDTO dto = FavoriteListResponseDTO.builder()
                    .stockCode(favoriteStock.getStockCode())
                    .stockName(favoriteStock.getStockName())
                    .build();
            responseDTOList.add(dto);
        }
        return responseDTOList;


    }


}