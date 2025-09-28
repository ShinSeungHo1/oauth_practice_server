package com.mypractice.oauth.member.service;

import com.mypractice.oauth.member.dto.AccessTokenDto;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Service
public class KakaoService {



    public AccessTokenDto getAccessToken(String code) {
        //서버간 통신을 위하여 RestClient 객체 사용
        // 필용한 정보 : ContentType, 인가 코드, redirect uri, client_secret, client_id, grant_type
        RestClient restClient = RestClient.create();

        //MultiValueMap을 통해 form-data 형식으로 body 조합하기
        // 왜냐 원하는 content-type이 form 형태이기 때문
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        params.add("code");
    }
}
