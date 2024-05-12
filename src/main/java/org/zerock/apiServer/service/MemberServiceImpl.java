package org.zerock.apiServer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.zerock.apiServer.domain.Member;
import org.zerock.apiServer.domain.MemberRole;
import org.zerock.apiServer.dto.MemberDTO;
import org.zerock.apiServer.dto.MemberModifyDTO;
import org.zerock.apiServer.repository.MemberRepository;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class MemberServiceImpl implements MemberService{
    @Override
    public void modifyMember(MemberModifyDTO memberModifyDTO) {
        Optional<Member> res = memberRepository.findById(memberModifyDTO.getEmail());
        Member member = res.orElseThrow();

        member.changeNickname(memberModifyDTO.getNickname());
        member.changeSocial(false);
        member.changePw(passwordEncoder.encode(memberModifyDTO.getPw()));

        memberRepository.save(member);
    }

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public MemberDTO getKakaoMember(String accessToken) {
        // 1. accessToken 이용해서 사용자의 정보를 kakao에서 가져오기
        String nickname = getEmailFromKakaoAccessToken(accessToken);

        // DB에 회원정보가 있는지 확인
        Optional<Member> result = memberRepository.findById(nickname);
        if(result.isPresent()) {
            MemberDTO memberDTO = entityToDTO(result.get());
            log.info("existed................" + memberDTO);
            return memberDTO;
        }
        // 회원 정보가 없어서 만들어 주기
        Member socialMember = makeSocialMember(nickname);
        memberRepository.save(socialMember);
        MemberDTO memberDTO = entityToDTO(socialMember);

        return memberDTO;
    }

    private Member makeSocialMember(String nickname) {
        String tempPassword = makeTempPassword();
        log.info(tempPassword);
        Member member = Member.builder()
                .email(nickname)
                .pw(passwordEncoder.encode(tempPassword))
                .nickname("social member")
                .social(true)
                .build();
        member.addRole(MemberRole.USER);
        return member;
    }

    private String getEmailFromKakaoAccessToken(String accessToken) {
        String kakaoGetUserURL = "https://kapi.kakao.com/v2/user/me";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        HttpEntity<String> entity = new HttpEntity<>(headers);
        // 보내기
        UriComponents uriBuilder = UriComponentsBuilder.fromHttpUrl(kakaoGetUserURL).build();
        ResponseEntity<LinkedHashMap> response =
                restTemplate.exchange(uriBuilder.toUri(), HttpMethod.GET, entity, LinkedHashMap.class);

        log.info(response);

        LinkedHashMap<String, LinkedHashMap> bodyMap = response.getBody();
        LinkedHashMap<String, String> kakaoAccount = bodyMap.get("properties");
        log.info("kakaoAccount: " + kakaoAccount);
        String nickname = kakaoAccount.get("nickname");
        log.info("nickname: " + nickname);
        return nickname;
    }

    // 열자리의 패스워드를 만들어주는 함수
    private String makeTempPassword() {
        StringBuffer buffer = new StringBuffer();
        for(int i=0;i<10;i++) {
            buffer.append((char)((int)(Math.random()*55) + 65));
        }
        return buffer.toString();
    }
}
