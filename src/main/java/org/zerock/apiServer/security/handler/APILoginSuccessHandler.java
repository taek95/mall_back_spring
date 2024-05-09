package org.zerock.apiServer.security.handler;

import com.google.gson.Gson;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.zerock.apiServer.dto.MemberDTO;
import org.zerock.apiServer.util.JWTUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

// 로그인 성공하고 난 뒤 할 행동 -> handler
@Log4j2
public class APILoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("---------------------");
        log.info(authentication);
        log.info("---------------------");

        MemberDTO memberDTO = (MemberDTO) authentication.getPrincipal();
        Map<String , Object> claims = memberDTO.getClaims();

        // 이 정보들을 JWT 토큰으로 만들거고, 유효시간 10분
        String accessToken = JWTUtil.generateToken(claims, 10);
        String refreshToken = JWTUtil.generateToken(claims, 10);
        claims.put("accessToken",accessToken);
        claims.put("refreshToken",refreshToken);

        // claim를 JSON으로 바꾸기, 프론트에 연동하기 위해서
        Gson gson = new Gson();
        String jsonStr = gson.toJson(claims);
        response.setContentType("application/json; charset=UTF-8");
        PrintWriter printWriter = response.getWriter();
        printWriter.println(jsonStr);
        printWriter.close();
    }
}
