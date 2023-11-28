package com.example.aucation.member.api.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SignupRequest {

    private String memberId;
    private String memberPw;
    private String memberEmail;
    private String memberNickname;
    private double memberLng;
    private double memberLat;

}
