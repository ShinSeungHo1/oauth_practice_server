package com.mypractice.oauth.member.service;

import com.mypractice.oauth.member.dto.AccessTokenDto;
import com.mypractice.oauth.member.dto.GoogleProfileDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Service
public class GoogleService {

    @Value("${oauth.google.client-id}")
    private String googleClientId;

    @Value("${oauth.google.client-secret}")
    private String googleClientSecret;

    @Value("${oauth.google.redirect-uri}")
    private String googleRedirectUri;

    public AccessTokenDto getAccessToken(String code) {
        // 인가코드, clientId, clientSecret, redirectUri, grantType
        // 서버 to 서버로 데이터로 전달할 때 RestTemplate 썼는데 이건 비추천 상태이기 때문에 대신 RestClient 사용
        RestClient restClient = RestClient.create();

        // MultiValueMap을 통해 자동으로 form-data 형식으로 body 조립 가능
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", googleClientId);
        params.add("client_secret", googleClientSecret);
        params.add("redirect_uri", googleRedirectUri);
        params.add("grant_type", "authorization_code");
        ResponseEntity<AccessTokenDto> response = restClient.post()
                .uri("https://oauth2.googleapis.com/token")// token을 받아오는 uri를 입력해주면 된다.
                .header("Content-Type", "application/x-www-form-urlencoded")
                // ?code=xxxx&client_id=yyyy&~~
                .body(params)
                //retrieve: 응답 body값만을 추출
                .retrieve()
                .toEntity(AccessTokenDto.class);
        return response.getBody();
    }

    public GoogleProfileDto getGoogleProfile(String token) {
        RestClient restClient = RestClient.create();
        ResponseEntity<GoogleProfileDto> response = restClient.get()
                .uri("https://openidconnect.googleapis.com/v1/userinfo")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toEntity(GoogleProfileDto.class);
        System.out.println("profile JSON : " + response.getBody());
        return response.getBody();
    }
}
