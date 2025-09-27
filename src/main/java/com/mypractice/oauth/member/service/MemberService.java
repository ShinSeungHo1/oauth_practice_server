package com.mypractice.oauth.member.service;

import com.mypractice.oauth.member.domain.Member;
import com.mypractice.oauth.member.domain.SocialType;
import com.mypractice.oauth.member.dto.MemberCreateDto;
import com.mypractice.oauth.member.dto.MemberLoginDto;
import com.mypractice.oauth.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public Member create(MemberCreateDto memberCreateDto) {
        Member member = Member.builder()
                .email(memberCreateDto.getEmail())
                .password(passwordEncoder.encode(memberCreateDto.getPassword())) //password를 암호화 시키는 것!
                .build(); // builder pattern을 쓸거임

        memberRepository.save(member);

        return member;
    }

    public Member login(MemberLoginDto memberLoginDto) {
        Optional<Member> optMember =  memberRepository.findByEmail(memberLoginDto.getEmail());
        //Optional 객체로 리턴(우리가 정의해주기 나름임) => Member 객체가 있을수도 있고 없을 수도 있다.

        if(optMember.isEmpty()) {
            throw new IllegalArgumentException("email이 존재하지 않습니다.");
        }

        Member member = optMember.get();
        if(!passwordEncoder.matches(memberLoginDto.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("password가 일치 하지 않습니다.");
        }
        return member;
    }

    public Member getMemberBySocialId(String socialId) {
        Member member = memberRepository.findBySocialId(socialId).orElse(null);
        return member;
    }

    public Member createOauth(String socialId, String email, SocialType socialType) {
        Member member = Member.builder()
                .email(email)
                .socialType(socialType)
                .socialId(socialId)
                .build();

        memberRepository.save(member);
        return member;
    }
}
