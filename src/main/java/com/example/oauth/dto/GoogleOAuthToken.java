package com.example.oauth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class GoogleOAuthToken {
    private String refresh_token;
    private String access_token;
    private int expires_in;
    private String scope;
    private String token_type;
    private String id_token;
}