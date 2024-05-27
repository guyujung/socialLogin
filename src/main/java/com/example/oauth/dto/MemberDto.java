package com.example.oauth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class MemberDto {
    private String user_email; //사용자 이메일
    private String nickname; //닉네임
    private String inviteCode; //초대코드
    private List<String> interestingSports;
}