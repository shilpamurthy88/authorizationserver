package com.sapient.project.authorizationserver.service;

import com.sapient.project.authorizationserver.model.CustomUserDetails;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import java.util.LinkedHashMap;
import java.util.Map;

public class CustomTokenEnhancer extends JwtAccessTokenConverter {

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
        Map<String , Object> info = new LinkedHashMap<>(accessToken.getAdditionalInformation());
        if(user.getUsername() != null) {
            info.put("username", user.getUsername());
        }
        if(user.getFullName() != null) {
            info.put("fullName", user.getFullName());
        }
        if(user.getIs2faEnabled() != null) {
            info.put("is2faEnabled", user.getIs2faEnabled());
        }
        if(user.getTwofaDefaultType() != null) {
            info.put("2faDefaultType", user.getTwofaDefaultType());
        }

        DefaultOAuth2AccessToken customeAccessToken = new DefaultOAuth2AccessToken(accessToken);
        customeAccessToken.setAdditionalInformation(info);

        return super.enhance(customeAccessToken, authentication);
    }
}
