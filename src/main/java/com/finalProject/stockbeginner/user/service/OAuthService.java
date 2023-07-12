package com.finalProject.stockbeginner.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.finalProject.stockbeginner.user.dto.response.LoginResponseDTO;
import com.finalProject.stockbeginner.user.entity.User;
import com.finalProject.stockbeginner.user.repository.UserRepository;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class OAuthService {
//    public KakaoOAuth kakaoOAuth;
//    private UserRepository userRepository;
//    private UserService userService;

//    private KakaoUserInfoDTO getKakaoUserInfoDTO(String code) throws JsonProcessingException {
//        ResponseEntity<String> accessTokenResponse = kakaoOAuth.requestAccessToken(code);
//        KakaoOAuthTokenDTO oAuthToken = kakaoOAuth.getAccessToken(accessTokenResponse);
//        ResponseEntity<String> userInfoResponse = kakaoOAuth.requestUserInfo(oAuthToken);
//        KakaoUserInfoDTO kakaoUser = kakaoOAuth.getUserInfo(userInfoResponse);
//        return kakaoUser;
//    }

//    public LoginResponseDTO kakaoLogin(String code) throws IOException {
//        KakaoUserInfoDTO kakaoUser = getKakaoUserInfoDTO(code);
//
//        if(!userRepository.existsByEmail(kakaoUser.getKakao_account().getEmail())) {
//            userRepository.save(
//                    User.builder()
//                            .email(kakaoUser.getKakao_account().getEmail())
//                            .nick(kakaoUser.getKakao_account().getProfile().getNickname())
//                            .password("kakao")
//                            .name("kakao")
//                            .build()
//            );
//            return userService.getSingleResult(userRepository.findByEmail(
//                    kakaoUser.getKakao_account().getEmail()
//            ).orElseThrow(EmailSigninFailedException::new));
//        }
//        return userService.getSingleResult(userRepository.findByEmail(
//                kakaoUser.getKakao_account().getEmail()
//                ).orElseThrow(EmailSigninFailedException::new));
//
//
//
//
//        }
//






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


}
