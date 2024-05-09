package org.zerock.apiServer.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zerock.apiServer.util.CustomJWTException;
import org.zerock.apiServer.util.JWTUtil;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Log4j2
public class APIRefreshController {

    // authHeader는 accessToken 의미
    @RequestMapping("/api/member/refresh")
    public Map<String, Object> refresh (
            @RequestHeader("Authorization") String authHeader,
            String refreshToken
    ) {
        if (refreshToken == null) {
            throw new CustomJWTException("NULL_REFRESH");
        }

        if (authHeader == null || authHeader.length() < 7) {
            throw new CustomJWTException("INVALID String");
        }

        //Bearer 와 뛰어쓰기 제외해야 해서.. 그게 길이가 7
        String accessToken = authHeader.substring(7);

        // AccessToken의 만료여부 확인, false는 만료 안됐다는 것
        if(checkExpiredToken(accessToken) == false) {
            return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
        }

        // refresh 토큰 검증
        Map<String, Object> claims = JWTUtil.validateToken(refreshToken);
        log.info("refresh.... claims: " + claims);
        String newAccessToken = JWTUtil.generateToken(claims, 10);
        String newRefreshToken = checkTime((Integer)claims.get("exp")) == true ? JWTUtil.generateToken(claims, 60*24) : refreshToken;
        return Map.of("accessToken", newAccessToken, "refreshToken" , newRefreshToken);
    }

    // 시간이 1시간 미만으로 남았을때 check
    private boolean checkTime(Integer exp) {

        // JWT exp를 날짜로 변환
        java.util.Date expDate = new java.util.Date( (long)exp * (1000));

        // 현재 시간과의 차이 계싼
        long gap = expDate.getTime() - System.currentTimeMillis();

        // 분단위 계산
        long leftMin = gap / (1000*60);

        // 1시간도 안남았는지 확인
        return leftMin < 60;
    }

    // 만료가 됐는지 안됐는지 check
    private boolean checkExpiredToken(String token) {
        try {
            JWTUtil.validateToken(token);
        }catch (CustomJWTException ex) {
            if(ex.getMessage().equals("Expired")){
                return true;
            }
        }
        return false;
    }
}
