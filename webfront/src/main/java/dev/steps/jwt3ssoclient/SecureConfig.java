/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.steps.jwt3ssoclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.cloud.security.oauth2.client.feign.OAuth2FeignRequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.common.AuthenticationScheme;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

/**
 *
 * @author stepin
 */
@Configuration
@EnableOAuth2Client
//@EnableWebSecurity(debug = true)
public class SecureConfig extends WebSecurityConfigurerAdapter {

    private final long millisecToUseToken = 5000L;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
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
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                //                .and()
                //                .authorizeRequests().antMatchers("/**").permitAll()
                //                .antMatchers("/", "/**").permitAll()
                //                .anyRequest().authenticated()

                .antMatchers("/callback**", "/accessDenied**", "/login**", "/error**", "/favicon.ico").permitAll()
                .antMatchers("/", "/**").hasAnyAuthority("TEST_AUTHORITY-WEBFRONT")
                .anyRequest().authenticated()
                .and()
                .csrf().disable()
                .oauth2Login()
                .authorizationEndpoint()
                .baseUri("/login")
                .and()
                .redirectionEndpoint()
                .baseUri("/callback")
                .and()
                .userInfoEndpoint()
                .userService(oauth2UserService());

    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {

        ClientRegistration registration = ClientRegistration.withRegistrationId("test-reg")
                .clientId("webfront-client")
                .clientSecret("987")
                .authorizationUri("http://localhost:9000/oauth/authorize")
                .tokenUri("http://localhost:7000/oauth/token")
                .redirectUriTemplate("http://localhost:9000/webfront/callback")
                .scope("read", "write")
                .clientName("webfront-client-test")
                //.userInfoUri("http://localhost:9000/user/info")
                //.userNameAttributeName("name")                 
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .clientAuthenticationMethod(ClientAuthenticationMethod.BASIC)
                .build();

//        ClientRegistration registration = ClientRegistration.withRegistrationId("test-oauth2")
//                .clientId("webfront-client")
//                .clientSecret("987")
//                .authorizationUri("http://localhost:9000/oauth/authorize")
//                .tokenUri("http://localhost:9000/oauth/token")
//                .redirectUriTemplate("http://localhost:9000/callback")
//                .scope("read", "write")
//                .clientName("webfront-client-test")
//                //.userInfoUri("http://localhost:7071/api/profile")
//                //.userNameAttributeName("name")                 
//                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
//                .clientAuthenticationMethod(ClientAuthenticationMethod.BASIC)
//                .build();


        return new InMemoryClientRegistrationRepository(Arrays.asList(registration));
    }

    @Bean
    public OAuth2AuthorizedClientService authorizedClientService() {
        return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository());
    }
    
    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
        return new MyUserService(tokenServices());
    }

    @Bean
    public DefaultTokenServices tokenServices() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        return defaultTokenServices;
    }
    
    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey("non-prod-signature");
        return converter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence cs) {
                return cs.toString();
            }

            @Override
            public boolean matches(CharSequence cs, String str) {
                return cs.toString().equals(str);
            }
        };
    }

    @Bean
    protected OAuth2ProtectedResourceDetails resourceDetails() {

//        AuthorizationCodeResourceDetails resourceDetails = new AuthorizationCodeResourceDetails();
//        resourceDetails.setId("oauth2server");
//        resourceDetails.setTokenName("oauth_token");
//        resourceDetails.setClientId("webfront-client");
//        resourceDetails.setClientSecret("987");
//        resourceDetails.setAccessTokenUri("http://localhost:7000/oauth/token");
//        resourceDetails.setUserAuthorizationUri("http://localhost:7000/oauth/authorize");
//        resourceDetails.setScope(Arrays.asList("read", "write"));
//        resourceDetails.setPreEstablishedRedirectUri("http://localhost:6061/callback");
//        resourceDetails.setUseCurrentUri(false);
//        //resourceDetails.setAuthenticationScheme(AuthenticationScheme.query);
//        resourceDetails.setClientAuthenticationScheme(AuthenticationScheme.header);

        AuthorizationCodeResourceDetails resourceDetails = new AuthorizationCodeResourceDetails();
        resourceDetails.setId("test-reg");
        resourceDetails.setTokenName("oauth_token");
        resourceDetails.setClientId("webfront-client");
        resourceDetails.setClientSecret("987");
        resourceDetails.setAccessTokenUri("http://localhost:9000/oauth/token");
        resourceDetails.setUserAuthorizationUri("http://localhost:9000/oauth/authorize");
        resourceDetails.setScope(Arrays.asList("read", "write"));
        resourceDetails.setPreEstablishedRedirectUri("http://localhost:9000/webfront/callback");
        resourceDetails.setUseCurrentUri(false);
        //resourceDetails.setAuthenticationScheme(AuthenticationScheme.query);
        resourceDetails.setClientAuthenticationScheme(AuthenticationScheme.header);

        return resourceDetails;
    }

//    @Bean
//    public OAuth2RestTemplate oauth2RestTemplate() {
//        AccessTokenRequest atr = new DefaultAccessTokenRequest();
//
//        //OAuth2RestTemplate oauth2RestTemplate = new OAuth2RestTemplate(resourceDetails(), new DefaultOAuth2ClientContext(atr));
//        OAuth2RestTemplate oauth2RestTemplate = new OAuth2RestTemplate(resourceDetails(), oauth2ClientContext);
//        oauth2RestTemplate.getAccessToken();
//
//        return oauth2RestTemplate;
//    }
//    @Bean
//    public ResourceServerProperties resourceServerProperties() {
//        ResourceServerProperties resourceServerProperties = new ResourceServerProperties();
//        resourceServerProperties.setUserInfoUri("http://localhost:9000/userinfo");
//        return resourceServerProperties;
//    }
}
