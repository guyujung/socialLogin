package com.example.oauth.service;

import com.example.oauth.config.Constant;
import com.example.oauth.dto.GetSocialOAuthRes;
import com.example.oauth.dto.GoogleOAuthToken;
import com.example.oauth.dto.GoogleUser;
import com.example.oauth.entity.Member;
import com.example.oauth.repository.MemberRepository;
import com.example.oauth.utils.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@AllArgsConstructor
@Slf4j
public class OAuthService {
    private final HttpServletResponse response;
    private final GoogleOauth googleOauth;
    private final MemberService memberService;
    private final JwtService jwtService;
private final MemberRepository memberRepository;
    public void redirectTo(Constant.SocialLoginType socialLoginType) throws IOException {
        String redirectURL;
        switch (socialLoginType){
            case GOOGLE : {
                redirectURL = googleOauth.getOauthRedirectURL();
            } break;
            default : {
                throw new IllegalArgumentException("알 수 없는 소셜 로그인 형식입니다.");
            }
        }

        //각 소셜 로그인을 요청하면 소셜 로그인 페이지로 리다이렉트 해주는 프로세스
        response.sendRedirect(redirectURL);
    }

    public GetSocialOAuthRes oAuthLogin(Constant.SocialLoginType socialLoginType, String code) throws Exception {
        try{
            switch (socialLoginType){
                case GOOGLE: {
                    //Authorization code를 통해 AccessToken 획득
                    GoogleOAuthToken oAuthToken = googleOauth.getAccessToken(code);
                    //액세스 토큰을 다시 구글로 보내 구글에 저장된 사용자 정보가 담긴 응답 객체를 받아온다.
                    System.out.println("access token: "+oAuthToken.getAccess_token()+"\nrefresh token: "+ oAuthToken.getRefresh_token());


                    ResponseEntity<String> userInfoResponse = googleOauth.requestUserInfo(oAuthToken);
                    //다시 JSON 형식의 응답 객체를 자바 객체로 역직렬화한다.
                    GoogleUser googleUser = googleOauth.getUserInfo(userInfoResponse);

                    String user_email = googleUser.getEmail();
                    String picture_url = googleUser.getPicture();
                    String user_name=googleUser.getName();

                   
                    //우리 서버의 db와 대조하여 해당 User가 존재하는지 확인
                    Member member = memberService.getUser(user_email);


                    //refresh_token은 db에 저장하여 관리
                    //최초 로그인시 기본정보 member table에 저장
                    if((oAuthToken.getRefresh_token())!=null){
                        member.setRefreshToken(oAuthToken.getRefresh_token());
                        member.setPlatform(String.valueOf(socialLoginType));
                        member.setName(user_name);
                        member.setPictureUrl(picture_url);
                        memberRepository.save(member);
                    }

                    if (member != null){ //존재하는 경우
                        Long user_id = member.getId();
                        String role = member.getRole();
                        String jwtToken = jwtService.createJwt(user_id, role);
                        GetSocialOAuthRes getSocialOAuthRes = new GetSocialOAuthRes(jwtToken, user_id, user_email, picture_url, role);
                        return getSocialOAuthRes;
                    } else {
                        GetSocialOAuthRes getSocialOAuthRes = new GetSocialOAuthRes(null, null, user_email, picture_url, null);
                        return getSocialOAuthRes;
                    }
                }
                default: {
                    log.error("알 수 없는 소셜 로그인 형식입니다.");
                    throw new IllegalArgumentException("알 수 없는 소셜 로그인 형식입니다.");
                }
            }
        }catch(Exception ex){
            log.error(ex.getMessage());
            return null;
        }
    }


    public GetSocialOAuthRes newJWT(Constant.SocialLoginType socialLoginType, String accessToken) throws Exception {
        try{
            switch (socialLoginType){
                case GOOGLE: {
                    GoogleOAuthToken oAuthToken =new GoogleOAuthToken();
                    oAuthToken.setAccess_token(accessToken);
                    ResponseEntity<String> userInfoResponse = googleOauth.requestUserInfo(oAuthToken);
                    GoogleUser googleUser = googleOauth.getUserInfo(userInfoResponse);
                    String user_email = googleUser.getEmail();
                    String picture_url = googleUser.getPicture();


                    //우리 서버의 db와 대조하여 해당 User가 존재하는지 확인
                    Member member = memberService.getUser(user_email);

                    if (member != null){ //존재하는 경우
                        Long user_id = member.getId();
                        String role = member.getRole();
                        String jwtToken = jwtService.createJwt(user_id, role);
                        GetSocialOAuthRes getSocialOAuthRes = new GetSocialOAuthRes(jwtToken, user_id, user_email, picture_url, role);
                        return getSocialOAuthRes;
                    } else {
                        GetSocialOAuthRes getSocialOAuthRes = new GetSocialOAuthRes(null, null, user_email, picture_url, null);
                        return getSocialOAuthRes;
                    }
                }
                default: {
                    log.error("알 수 없는 소셜 로그인 형식입니다.");
                    throw new IllegalArgumentException("알 수 없는 소셜 로그인 형식입니다.");
                }
            }
        }catch(Exception ex){
            log.error(ex.getMessage());
            return null;
        }
    }


   //refresh token
    public GoogleOAuthToken refreshToken(Constant.SocialLoginType socialLoginType,String refreshToken) throws Exception {
        try{
            switch (socialLoginType){
                case GOOGLE: {
                    GoogleOAuthToken googleOAuthToken= googleOauth.getRefreshToken(refreshToken);
                    return googleOAuthToken;
                }
                default: {
                    log.error("알 수 없는 소셜 로그인 형식입니다.");
                    throw new IllegalArgumentException("알 수 없는 소셜 로그인 형식입니다.");
                }

            }
        }catch(Exception ex){
            log.error(ex.getMessage());
            return null;
        }
    }

    //revoke token
    public ResponseEntity revokeToken(Constant.SocialLoginType socialLoginType,String revokeToken) throws Exception {
        try{
            switch (socialLoginType){
                case GOOGLE: {
                    ResponseEntity responseEntity= googleOauth.requestRevokeToken(revokeToken);
                    return responseEntity;
                }
                default: {
                    log.error("알 수 없는 소셜 로그인 형식입니다.");
                    throw new IllegalArgumentException("알 수 없는 소셜 로그인 형식입니다.");
                }

            }
        }catch(Exception ex){
            log.error(ex.getMessage());
            return null;
        }
    }



}
