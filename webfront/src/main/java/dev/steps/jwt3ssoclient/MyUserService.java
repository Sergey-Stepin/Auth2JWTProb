/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.steps.jwt3ssoclient;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 *
 * @author stepin
 */
public class MyUserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    
    private final Logger LOGGER = LogManager.getLogger(this.getClass());

    private final DefaultTokenServices tokenServices;

    public MyUserService(DefaultTokenServices tokenServices) {
        this.tokenServices = tokenServices;
        LOGGER.info("~~~ MyUserService");
    }
    
    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException  {
        LOGGER.info("@@@ OAuth2User from request:");

        OAuth2AccessToken accessToken = tokenServices.readAccessToken(request.getAccessToken().getTokenValue());

        LOGGER.info("@@@ VALUE: ------------------------------");
        LOGGER.info("@@@\t accessToken:" + accessToken);
        accessToken.getAdditionalInformation().entrySet()
                .stream()
                .forEach(System.out::println);
        LOGGER.info("@@@ --------------------------------------");

        Map<String, Object> additionals = accessToken.getAdditionalInformation();
        
        String userName = (String) additionals.get("user_name");
        Assert.notNull(userName, " Parameter user_name was not found in the token ");

        List<GrantedAuthority> authorities;
        List<String> authoritiesAsString;
        if (additionals.containsKey("authorities")) {
            authoritiesAsString = (List<String>) additionals.get("authorities");
            authorities = authoritiesAsString
                    .stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
            
//            authorities = Arrays.stream(StringUtils.delimitedListToStringArray(authoritiesAsString.replace("[", "").replace("]", ""), ", "))
//                    .map(SimpleGrantedAuthority::new)
//                    .collect(Collectors.toList());
            
            if (authorities == null || authorities.isEmpty()){
                authorities = AuthorityUtils.createAuthorityList("ANONYMOUS");
            }

        } else {
            authorities = AuthorityUtils.createAuthorityList("ANONYMOUS");
        }

//        List<GrantedAuthority> authorities;
//        String[] authoritiesAsString;
//        if (additionals.containsKey("authorities")) {
//            System.out.println("$$$ additionals.get(\"authorities\")=" + additionals.get("authorities"));
//            StringUtils.delimitedListToStringArray(userName, userName, userName)
//            authoritiesAsString = (String[]) additionals.get("authorities");
//            authorities = Arrays.stream(authoritiesAsString)
//                    .map(SimpleGrantedAuthority::new)
//                    .collect(Collectors.toList());
//
//            if (authorities == null || authorities.isEmpty()){
//                authorities = AuthorityUtils.createAuthorityList("ANONYMOUS");
//            }
//
//        } else {
//            authorities = AuthorityUtils.createAuthorityList("ANONYMOUS");
//        }

        LOGGER.info("@@@ Creating OAuth2User: " + userName);
        
        return new DefaultOAuth2User(authorities, additionals, "user_name");

    }

}
