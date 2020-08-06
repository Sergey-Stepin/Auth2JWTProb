/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.steps.jwt3ssoclient.feign;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Logger;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.security.oauth2.client.feign.OAuth2FeignRequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.common.AuthenticationScheme;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

/**
 *
 * @author stepin
 */
public class TestClientConfiguration {

    @Autowired
    OAuth2ProtectedResourceDetails resourceDetails;

    private final long millisecToUseToken = 5000L;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private JwtAccessTokenConverter jwtAccessTokenConverter;

    @Autowired
    private OAuth2ClientContext oauth2ClientContext;

    @Autowired
    private DefaultTokenServices tokenServices;

    @Autowired
    OAuth2AuthorizedClientService clientService;

//    @Bean
//    public RequestInterceptor oauth2FeignRequestInterceptor() {
//        return new OAuth2FeignRequestInterceptor(new DefaultOAuth2ClientContext(), resourceDetails);
//    }

    @Bean
    public RequestInterceptor oauth2FeignRequestInterceptor() {
        return (RequestTemplate requestTemplate) -> {
            System.out.println("### Feign.Interceptor");

            System.out.println("## FROM SecurityContextHolder:" + SecurityContextHolder.getContext());

            Authentication authentication = SecurityContextHolder
                    .getContext()
                    .getAuthentication();

            System.out.println("### authentication:" + authentication);

            OAuth2AuthenticationToken authenticationToken = (OAuth2AuthenticationToken) authentication;
            String strToken = (String) authenticationToken.getPrincipal().getAttributes().get("jti");

            OAuth2AuthorizedClient client = clientService.loadAuthorizedClient(
                            authenticationToken.getAuthorizedClientRegistrationId(),
                            authenticationToken.getName());

            System.out.println("###: client.getAccessToken().getTokenValue()=" + client.getAccessToken().getTokenValue());
            System.out.println("###: revoke: " + tokenServices.revokeToken(strToken));
            System.out.println("###: revoke: " + tokenServices.revokeToken(strToken));

            try {
                Map<String, Object> claims = objectMapper.readValue(
                        JwtHelper.decode(strToken).getClaims(),
                        Map.class);

                System.out.println("---------------------------");
                claims.entrySet()
                        .stream()
                        .forEach(System.out::println);
                System.out.println("---------------------------");

                boolean timeToRenewToken = false;

                System.out.println("EXP: " + claims.get(JwtAccessTokenConverter.EXP));
                long exp = 1000 * ((Integer) claims.get(JwtAccessTokenConverter.EXP)).longValue();

                Date expirationDate = new Date(exp);
                System.out.println("EXP DATE:" + expirationDate);

                long now = (new Date()).getTime();
                if (exp + millisecToUseToken >= now) {
                    System.out.println("### EXPIRED");
                }

            } catch (JsonProcessingException ex) {
                System.out.println("##########################################");
                System.out.println("### ERROR: " + ex.getMessage());
                System.out.println("##########################################");
            }

            requestTemplate.header("Authorization", "Bearer " + strToken);
        };
    }
    
    @Bean
    Logger.Level feignLoggerLevel() {
        return feign.Logger.Level.FULL;
    }

    private OAuth2RestTemplate createOAuth2RestTemplate() {

        AuthorizationCodeResourceDetails resourceDetails = new AuthorizationCodeResourceDetails();
        resourceDetails.setId("oauth2server");
        resourceDetails.setTokenName("oauth_token");
        resourceDetails.setClientId("webfront-client");
        resourceDetails.setClientSecret("987");
        resourceDetails.setAccessTokenUri("http://localhost:7000/oauth/token");
        resourceDetails.setUserAuthorizationUri("http://localhost:7000/oauth/authorize");
        resourceDetails.setScope(Arrays.asList("read", "write"));
        resourceDetails.setPreEstablishedRedirectUri("http://localhost:6061/callback");
        resourceDetails.setUseCurrentUri(false);
        //resourceDetails.setAuthenticationScheme(AuthenticationScheme.query);
        resourceDetails.setClientAuthenticationScheme(AuthenticationScheme.form);

        return new OAuth2RestTemplate(resourceDetails, oauth2ClientContext);
    }
}
