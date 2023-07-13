package com.finalProject.stockbeginner.user.service;

import com.finalProject.stockbeginner.exception.BaseException;
import com.finalProject.stockbeginner.exception.BaseResponseCode;
import com.finalProject.stockbeginner.user.auth.TokenProvider;
import com.finalProject.stockbeginner.user.dto.request.KakaoRegisterRequestDTO;
import com.finalProject.stockbeginner.user.dto.response.KakaoLoginResponseDTO;
import com.finalProject.stockbeginner.user.dto.response.UserResponseDTO;
import com.finalProject.stockbeginner.user.entity.User;
import com.finalProject.stockbeginner.user.repository.UserRepository;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

@Service
public class OAuthService {

    private TokenProvider tokenProvider;
    private  UserService userService;
    private UserRepository userRepository;
    private UserResponseDTO userResponseDTO;

    public String getKakaoAccessToken (String code) {
            String access_Token = "";
            String refresh_Token = "";
            String reqURL = "https://kauth.kakao.com/oauth/token";

            try {
                URL url = new URL(reqURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                //POST 요청을 위해 기본값이 false인 setDoOutput을 true로
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                //POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
                StringBuilder sb = new StringBuilder();
                sb.append("grant_type=authorization_code");
                sb.append("&client_id=6d5436bbca874585f1aa1c07883d5eab");
                sb.append("&redirect_uri=http://localhost:8181/api/user/callback/kakao");
                sb.append("&code=" + code);
                bw.write(sb.toString());
                bw.flush();

                //결과 코드가 200이라면 성공
                int responseCode = conn.getResponseCode();
                System.out.println("responseCode : " + responseCode);

                //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line = "";
                String result = "";

                while ((line = br.readLine()) != null) {
                    result += line;
                }
                System.out.println("response body : " + result);

                //Gson 라이브러리에 포함된 클래스로 JSON파싱 객체 생성
                JsonParser parser = new JsonParser();
                JsonElement element = parser.parse(result);

                access_Token = element.getAsJsonObject().get("access_token").getAsString();
                refresh_Token = element.getAsJsonObject().get("refresh_token").getAsString();

                System.out.println("access_token : " + access_Token);
                System.out.println("refresh_token : " + refresh_Token);

                br.close();
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return access_Token;
        }

        public KakaoLoginResponseDTO kakaoLogin(String access_Token) throws BaseException {
            KakaoRegisterRequestDTO kakaoRegisterRequestDTO = getUserKakaoSignupRequestDto(
                    userService.getUserInfo(access_Token));
            UserResponseDTO userResponseDto = findByUserKakaoIdentifier(kakaoRegisterRequestDTO.getUserKakaoIdentifier());
            if (userResponseDTO == null) {
                signUp(kakaoRegisterRequestDTO);
                userResponseDTO = findByUserKakaoIdentifier(kakaoRegisterRequestDTO.getUserKakaoIdentifier());
            }
            String token = tokenProvider.createTokenToKakao(userResponseDTO.getEmail());
            return new KakaoLoginResponseDTO(HttpStatus.OK, token, userResponseDTO.getEmail());
        }


    public UserResponseDTO findByUserKakaoIdentifier(String kakaoIdentifier) {
        List<User> user = (List<User>) userRepository.findUserByUserKakaoIdentifier(kakaoIdentifier).orElseThrow(() -> new UsernameNotFoundException("해당하는 유저를 찾을 수 없습니다."));

        if(user.size() == 0){
            return null;
        }
        return new UserResponseDTO(user.get(0));
    }
    @Transactional
    public String signUp(KakaoRegisterRequestDTO kakaoRegisterRequestDto) throws BaseException {
        String id;
        try {
            id = userRepository.save(kakaoRegisterRequestDto.toEntity()).getId();
        } catch (Exception e) {
            throw new BaseException(BaseResponseCode.FAILED_TO_SAVE_USER) {

            };
        }
        return id;
    }

    private KakaoRegisterRequestDTO getUserKakaoSignupRequestDto(HashMap<String, Object> userInfo){
        String userPassword = "-1";
        KakaoRegisterRequestDTO kakaoRegisterRequestDto = new KakaoRegisterRequestDTO(userInfo.get("email").toString(), userInfo.get("nickname").toString(),userPassword,userInfo.get("nickname").toString(),userInfo.get("id").toString());
        return kakaoRegisterRequestDto;
    }
}
