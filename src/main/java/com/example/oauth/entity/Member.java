package com.example.oauth.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.ALL;

@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String name;
    private String role;//default='ROLE_USER'
    private String refreshToken; //refresh token일단 임시로 member에서 관리

    @CreationTimestamp
    private Timestamp createdAt; //최초 가입일
    private String pictureUrl; //유저 사진
    private String platform; //로그인 플랫폼
    private String sportsmanship; // 스포츠 맨십
    private String nickname; //닉네임
    private String inviteCode; //초대코드


    @JsonIgnore
    @OneToMany(mappedBy = "member", cascade = ALL, fetch = FetchType.LAZY)
    private List<InterestingSport> interestingSportList = new ArrayList<>();    //관심있는 운동

    //연관관계 편의 메소드
    public void addInterestingSports(InterestingSport interestingSport){
        interestingSportList.add(interestingSport);
    }

    @Builder
    public Member(String email, String name, String role,  String pictureUrl, String platform,String sportsmanship, String nickname, String inviteCode ){
        this.email = email;
        this.name = name;
        this.role = role;
        this.pictureUrl = pictureUrl;
        this.platform=platform;
        this.sportsmanship=sportsmanship;
        this.nickname=nickname;
        this.inviteCode=inviteCode;
    }
}
