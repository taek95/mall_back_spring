package org.zerock.apiServer.security;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.zerock.apiServer.domain.Member;
import org.zerock.apiServer.dto.MemberDTO;
import org.zerock.apiServer.repository.MemberRepository;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Log4j2
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    // username은 email, UserDetails는 MemberDTO
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("------------loadUserByUsername---------" + username);
        Member member = memberRepository.getWithRoles(username);
        if(member == null) {
            throw new UsernameNotFoundException("Not Found");
        }

        MemberDTO memberDTO = new MemberDTO(
                member.getEmail(),
                member.getPw(),
                member.getNickname(),
                member.isSocial(),
                member.getMemberRoleList()
                        .stream()
                        .map(memberRole -> memberRole.name()).collect(Collectors.toList()));


        return memberDTO;
    }
}
