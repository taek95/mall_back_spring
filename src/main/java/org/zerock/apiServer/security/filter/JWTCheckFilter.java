package org.zerock.apiServer.security.filter;


import com.google.gson.Gson;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.zerock.apiServer.dto.MemberDTO;
import org.zerock.apiServer.util.JWTUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

// OncePerRequestFilter는 모든 리퀘스트에 대하여 작동
@Log4j2
public class JWTCheckFilter extends OncePerRequestFilter {

    @Override
    // 필터링 안하는거
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

        // true면 체크안한다는 뜻
        String path = request.getRequestURI();
        log.info("check url----------" + path);

        if(path.startsWith("/api/member/"))
            return true;

        // 아니오의 아니오니까 긍정, 체크한다는 뜻
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        log.info("-------------------------");
        log.info("-------------------------");
        log.info("-------------------------");

        String authHeaderStr = request.getHeader("Authorization");
        try {
            String accessToken = authHeaderStr.substring(7);
            Map<String, Object> claims = JWTUtil.validateToken(accessToken);
            log.info(claims);

            // 성공했다면 사용자의 정보를 끄집어내기 가능
            String email = (String) claims.get("email");
            String pw = (String) claims.get("pw");
            String nickname = (String) claims.get("nickname");
            Boolean social = (Boolean) claims.get("social");
            List<String> roleNames = (List<String>) claims.get("roleNames");

            // 사용자의 정보를 가지고 memberDTO 구성
            MemberDTO memberDTO = new MemberDTO(email, pw, nickname, social.booleanValue(), roleNames);

            // 로그를 통해 이 사용자가 어떤 권한을 가지고 있는지 확인
            log.info("------------------------");
            log.info(memberDTO);
            log.info(memberDTO.getAuthorities());

            // memberDTO, pw, 권한을 이용해서 스프링 시큐리티 컨텍스트가 사용하는 토큰 추가
            UsernamePasswordAuthenticationToken authenticationToken
                    = new UsernamePasswordAuthenticationToken(memberDTO, pw, memberDTO.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            filterChain.doFilter(request,response);

        } catch (Exception e) {
            log.error("JWT check error.........");
            log.error(e.getMessage());

            Gson gson = new Gson();
            String msg = gson.toJson(Map.of("error", "ERROR_ACCESS_TOKEN"));

            response.setContentType("application/json");
            PrintWriter printWriter = response.getWriter();
            printWriter.println(msg);
            printWriter.close();
        }
    }
}
