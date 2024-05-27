package com.example.oauth.service;

import com.example.oauth.dto.MemberDto;
import com.example.oauth.dto.PostMemberRes;
import com.example.oauth.entity.InterestingSport;
import com.example.oauth.entity.Member;
import com.example.oauth.repository.MemberRepository;
import com.example.oauth.utils.JwtService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
@Slf4j
public class MemberService {
    private final MemberRepository memberRepository;
    private final InterestingSportService interestingSportService;
    private final JwtService jwtService;
    /**
     * 이메일로 찾은 사용자 id(PK) 반환
     */
    public Member getUser(String email){

        Optional<Member> member = memberRepository.findByEmail(email);
        if (member.isPresent()){
            return member.get();
        } else {
            return null;
        }
    }

    public PostMemberRes registerUser(MemberDto memberDto){
        if(getUser(memberDto.getUser_email()) != null){
            log.error("이미 가입된 회원입니다.");
            return null;
        }

        Member member = Member.builder()
                .email(memberDto.getUser_email())
                .nickname(memberDto.getNickname())
                .inviteCode(memberDto.getInviteCode())
                .build();


        List<InterestingSport> interestingSports = memberDto.getInterestingSports().stream()
                .map(sportName -> {
                    InterestingSport interestingSport = new InterestingSport(sportName);
                    interestingSport.confirmMember(member); // 연관관계 편의 메소드 호출
                    return interestingSport;
                })
                .collect(toList());


        
        Member postMember = memberRepository.save(member);
        interestingSportService.SaveAllInterestingSport(interestingSports);

        Long id = postMember.getId();
        String role = member.getRole();

        String jwtToken = jwtService.createJwt(id, role);
        PostMemberRes postMemberRes = new PostMemberRes(jwtToken, id, role);
        return postMemberRes;
    }


}
