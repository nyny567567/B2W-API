package com.finalProject.stockbeginner.user.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.finalProject.stockbeginner.user.dto.KakaoUserInfoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class KakaoOAuth {

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String restApiKey;
    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String kakaoRedirecUrl;
    private final String KAKAO_TOKEN_REQUEST_URL = "https://kauth.kakao.com/oauth/token";

    public String responseUrl(){
        String kakaoLoginUrl = "https://kauth.kakao.com/oauth/authorize?client_id=" + restApiKey + "&redirect_uri=" + kakaoRedirecUrl + "&response_type=code";
        return kakaoLoginUrl;
    }

    public ResponseEntity<String> requestAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headersAccess = new HttpHeaders();
        headersAccess.add("Content-type","application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", "6d5436bbca874585f1aa1c07883d5eab");
        params.add("redirect_uri", "http://localhost:8181/api/user/callback/kakao");
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> kakaoRequest = new HttpEntity<>(params, headersAccess);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(KAKAO_TOKEN_REQUEST_URL, kakaoRequest, String.class);
        return responseEntity;
    }

        public KakaoOAuthTokenDTO getAccessToken(ResponseEntity<String> response) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        KakaoOAuthTokenDTO kakaoOAuthTokenDTO = objectMapper.readValue(response.getBody(), KakaoOAuthTokenDTO.class);
        return kakaoOAuthTokenDTO;
    }

    public ResponseEntity<String> requestUserInfo(KakaoOAuthTokenDTO oAuthToken) {
        HttpHeaders headers = new HttpHeaders();
        RestTemplate restTemplate = new RestTemplate();
        headers.add("Authorization", "Bearer" + oAuthToken.getAccess_token());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange("https://kapi.kakao.com/v2/user/me", HttpMethod.GET, request, String.class);
        return response;
    }

    public KakaoUserInfoDTO getUserInfo(ResponseEntity<String> response) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        KakaoUserInfoDTO kakaoUserInfoDTO = objectMapper.readValue(response.getBody(), KakaoUserInfoDTO.class);
        return kakaoUserInfoDTO;
    }
}
