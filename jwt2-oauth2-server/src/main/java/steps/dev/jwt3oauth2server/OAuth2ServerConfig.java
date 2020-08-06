/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package steps.dev.jwt3oauth2server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

/**
 *
 * @author stepin
 */
@Configuration
@EnableAuthorizationServer
//@EnableWebSecurity(debug = true)
public class OAuth2ServerConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private MyTokenConverter myTokenConverter;
    
    //@Autowired
    private final AuthenticationManager authenticationManager;

    public OAuth2ServerConfig(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        this.authenticationManager = authenticationConfiguration.getAuthenticationManager();
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {

        TokenEnhancerChain chain = new TokenEnhancerChain();
        chain.setTokenEnhancers(Arrays.asList(myTokenConverter, accessTokenConverter()));

        endpoints
                .authenticationManager(authenticationManager)
                .tokenStore(jwtTokenStore())
                .tokenEnhancer(chain)
                .accessTokenConverter(accessTokenConverter());
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("testclient")
                .secret("123456")
                .authorities("TEST_AUTHORITIES")
                .authorizedGrantTypes("authorization_code")
                .accessTokenValiditySeconds(3600)
                .redirectUris("http://localhost:6060/callback")
                //.redirectUris("http://localhost:9000/login")
                .scopes("read", "write")
                //.autoApprove(true)
                
                .and()
                .withClient("webfront-client")
                .secret("987")
                //.authorities("TEST_AUTHORITY-WEBFRONT")
                .authorizedGrantTypes("authorization_code")
                .accessTokenValiditySeconds(15)
                .redirectUris("http://localhost:9000/webfront/callback")
                //.redirectUris("http://localhost:9000/login")
                .scopes("read", "write")
                //.autoApprove(true)
                ;

    }

    @Bean
    public MyTokenConverter accessTokenConverter() {
        MyTokenConverter converter = new MyTokenConverter();
        converter.setSigningKey("non-prod-signature");
        return converter;
    }

    @Bean
    public JwtTokenStore jwtTokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

    @SuppressWarnings("deprecation")
    @Bean
    public static NoOpPasswordEncoder passwordEncoder() {
        return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
    }
}
