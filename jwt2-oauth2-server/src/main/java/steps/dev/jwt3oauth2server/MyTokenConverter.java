/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package steps.dev.jwt3oauth2server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

/**
 *
 * @author stepin
 */
public class MyTokenConverter extends JwtAccessTokenConverter implements TokenEnhancer {

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken originalToken, OAuth2Authentication oauth2Authentication) {

        System.out.println("%%%%%%%%%% ENHANCE");
        String userName = oauth2Authentication.getUserAuthentication().getName();
        //String shortUserName = userName.substring(0, userName.lastIndexOf('@'));

        //user_name
        Map<String, Object> additionals = new HashMap<>(originalToken.getAdditionalInformation());
        additionals.put("user_name", userName);

        List<String> authorities = new ArrayList<>();
        authorities.add("TEST_UU_AUTHORITY-2");
        authorities.add("TEST_UU_AUTHORITY-3");
        authorities.add("TEST_AUTHORITY-WEBFRONT");
                
        additionals.put("authorities", authorities.toArray(new String[authorities.size()]));

        ((DefaultOAuth2AccessToken) originalToken).setAdditionalInformation(additionals);
        OAuth2AccessToken enhancedToken = super.enhance(originalToken, oauth2Authentication);

        ((DefaultOAuth2AccessToken) enhancedToken).setAdditionalInformation(new HashMap<>());
        return enhancedToken;

    }

}
