/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.steps.jwt3ssoclient;

import dev.steps.jwt3ssoclient.feign.TestClient;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author stepin
 */
@Controller
public class HomeController {

    @Autowired
    TestClient client;

    @Autowired
    RestTemplate restTemplate;

    @GetMapping("pureStr")
    public String getHome(
            Principal principal,
            Authentication authentication,
            SecurityContextHolder securityContextHolder,
            Model model) throws URISyntaxException {
        System.out.println("@@@ START");

        System.out.println("@@@ Principal:" + principal);
        System.out.println("@@@ Authentication:" + authentication);
        System.out.println("@@@ SecurityContextHolder:" + securityContextHolder);
        System.out.println("@@@ SecurityContextHolder.Context:" + SecurityContextHolder.getContext());

        OAuth2AuthenticationToken oauth2Token =  (OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        System.out.println("oauthToken from SecurityContextHolder: " + oauth2Token);

        OAuth2AuthenticationToken auth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
        String strToken = (String) auth2AuthenticationToken.getPrincipal().getAttributes().get("jti");

        ResponseEntity<String> response = restTemplate.exchange(
                RequestEntity.get(new URI("http://localhost:9000/jwt-back/test")).header("Authorization", "Bearer " + strToken).build(),
                String.class);

        //ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:9091/test", String.class);
        System.out.println("@@@ response" + response);

        model.addAttribute("responseStatus", response.getStatusCode());
        model.addAttribute("responseBody", response.getBody());
        return "home";
    }

    @GetMapping("testFeign")
    public String testfeign(Model model) {
        
        System.out.println("@@@@@@@@@@@@@@@@@ TEST FEIGN. START");
        
        ResponseEntity<String> response = client.getTest();

        System.out.println("@@@ response" + response);

        model.addAttribute("responseStatus", response.getStatusCode());
        model.addAttribute("responseBody", response.getBody());
        return "home";

    }

}
