package com.example.oauth.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class InterestingSport {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id",  nullable = false)
    private Member member;

    private String sportKind;

    public InterestingSport() {

    }
    public InterestingSport(String sportKind){
        this.sportKind=sportKind;
    }

    //연관관계 편의 메소드
    public void confirmMember(Member member){
        this.member = member;
        member.addInterestingSports(this);
    }


}
