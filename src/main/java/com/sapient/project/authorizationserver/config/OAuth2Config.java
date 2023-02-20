package com.sapient.project.authorizationserver.config;

import com.sapient.project.authorizationserver.service.CustomTokenEnhancer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
public class OAuth2Config extends AuthorizationServerConfigurerAdapter {

    @Value("${config.oauth2.clientid}")
    private String clientId;

    @Value("${config.oauth2.clientSecret}")
    private String clientSecret;

    @Value("${config.oauth2.privateKey}")
    private String privateKey;

    @Value("${config.oauth2.publicKey}")
    private String publicKey;

    @Autowired
    @Qualifier("authenticationManagerBean")
    private AuthenticationManager authenticationManager;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception{
        clients.inMemory().withClient(clientId).secret(passwordEncoder.encode(clientSecret)).scopes("read","write")
                .authorizedGrantTypes("password", "refresh_token", "authorization_code").accessTokenValiditySeconds(3600)
                .refreshTokenValiditySeconds(18000);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints.authenticationManager(authenticationManager).tokenStore(tokenStore()).accessTokenConverter(tokenEnhancer());
    }

    private JwtTokenStore tokenStore() {
        return new JwtTokenStore(tokenEnhancer());
    }

    private JwtAccessTokenConverter tokenEnhancer() {
        JwtAccessTokenConverter conv = new CustomTokenEnhancer();
        conv.setSigningKey(privateKey);
        conv.setVerifierKey(publicKey);

        return conv;
    }
}
