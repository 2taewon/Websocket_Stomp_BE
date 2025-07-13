package com.example.chatserver.member.service;

import com.example.chatserver.common.configs.Securityconfigs;
import com.example.chatserver.member.domain.Member;
import com.example.chatserver.member.dto.request.MemberLoginReqDto;
import com.example.chatserver.member.dto.request.MemberSaveReqDto;
import com.example.chatserver.member.dto.response.MemberListResDto;
import com.example.chatserver.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Security;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public Member create(MemberSaveReqDto dto) {
        if(memberRepository.findByEmail(dto.getEmail()).isPresent()){
            throw new IllegalArgumentException("이미 존재하는 이메일입니다. ");
        }
        Member newMember = Member.builder()
                .email(dto.getEmail())
                .name(dto.getName())
                .password(passwordEncoder.encode(dto.getPassword())) // 데이터를 암호화 해서 DB에 넣겠다.
                .build();

        Member member = memberRepository.save(newMember);
        return member;
    }

    public Member Login(MemberLoginReqDto dto) {
        Member member = memberRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("이메일을 찾을 수 없습니다. "));

        if(!passwordEncoder.matches(dto.getPassword(), member.getPassword())){
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        return member;
    }

    public List<MemberListResDto> finaAll() {
        List<Member> members = memberRepository.findAll();
        List<MemberListResDto> dtos = new ArrayList<>();
        for(Member member : members){
            MemberListResDto dto = new MemberListResDto();
            dto.setId(member.getId());
            dto.setEmail(member.getEmail());
            dto.setName(member.getName());
            dtos.add(dto);
        }
        return dtos;
    }
}
