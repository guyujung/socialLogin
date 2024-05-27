package com.example.oauth.controller;


import com.example.oauth.config.Constant;
import com.example.oauth.dto.*;
import com.example.oauth.service.GoogleOauth;
import com.example.oauth.service.MemberService;
import com.example.oauth.service.OAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/")
@Slf4j
@RequiredArgsConstructor
public class MemberController {
    private final OAuthService oauthService;
    private final GoogleOauth googleOauth;
    private final MemberService memberService;

    //redirectURl 반환
    @GetMapping("/accounts/oauth2/{socialLoginType}")
    public void socialLoginRedirect(@PathVariable(name = "socialLoginType") String socialLoginPath) throws IOException {
        Constant.SocialLoginType socialLoginType = Constant.SocialLoginType.valueOf(socialLoginPath.toUpperCase());
        oauthService.redirectTo(socialLoginType);
    }

    //code를 통해, 최초 access token과 refresh token 발급
    // access token으로 jwt생성, client storage에서 관리
    @GetMapping("/accounts/oauth2/{socialLoginType}/callback")
    public ResponseEntity<GetSocialOAuthRes> socialLogin(@PathVariable(name = "socialLoginType") String socialLoginPath,
                                                         @RequestParam(name = "code") String code) {
       try{
           Constant.SocialLoginType socialLoginType = Constant.SocialLoginType.valueOf(socialLoginPath.toUpperCase());
           GetSocialOAuthRes getSocialOAuthRes = oauthService.oAuthLogin(socialLoginType, code);
           return ResponseEntity.status(HttpStatus.OK).body(getSocialOAuthRes);
       }catch (Exception exception){
           log.error(exception.getMessage());
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
       }
    }

    //access token 만료시 새로운 access token으로 사용자 정보 확인 및 jwt 토큰 재발급
    @GetMapping("/accounts/oauth2/{socialLoginType}/jwt")
    public ResponseEntity<GetSocialOAuthRes> newJWT( @PathVariable(name = "socialLoginType") String socialLoginPath,
                        @RequestParam(name = "code") String code)  {

        try{
            Constant.SocialLoginType socialLoginType = Constant.SocialLoginType.valueOf(socialLoginPath.toUpperCase());
            GetSocialOAuthRes getSocialOAuthRes = oauthService.newJWT(socialLoginType,code);
            return ResponseEntity.status(HttpStatus.OK).body(getSocialOAuthRes);
        }catch (Exception exception){
            log.error(exception.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

    }

    // 이전에 얻은 리프레시 토큰을 사용하여 access token 갱신
    @GetMapping("/refresh/token/{socialLoginType}")
    public  ResponseEntity<GoogleOAuthToken> refreshToken(@PathVariable(name = "socialLoginType") String socialLoginPath,
                                          @RequestParam(name="token") String token){

        try {
            Constant.SocialLoginType socialLoginType = Constant.SocialLoginType.valueOf(socialLoginPath.toUpperCase());
            GoogleOAuthToken googleOAuthToken= oauthService.refreshToken(socialLoginType, token);
            return ResponseEntity.status(HttpStatus.OK).body(googleOAuthToken);
        }catch (Exception exception){
            log.error(exception.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }



    //access token 회수
    @GetMapping("/revoke/token/{socialLoginType}")
    public  ResponseEntity<String> revokeToken(@PathVariable(name = "socialLoginType") String socialLoginPath,
                             @RequestParam(name="token") String token)  {

        try{
            Constant.SocialLoginType socialLoginType = Constant.SocialLoginType.valueOf(socialLoginPath.toUpperCase());
            ResponseEntity responseEntity= oauthService.revokeToken(socialLoginType,token);
            return ResponseEntity.status(responseEntity.getStatusCode()).body("access token 회수 성공");
        }catch (Exception exception){
            log.error(exception.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    //회원가입
    @PostMapping({"/accounts/signup"})
    public ResponseEntity<PostMemberRes> signUp(@RequestBody MemberDto memberDto) {
        PostMemberRes postMemberRes = this.memberService.registerUser(memberDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(postMemberRes);
    }

}
