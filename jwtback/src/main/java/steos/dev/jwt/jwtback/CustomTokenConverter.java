/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package steos.dev.jwt.jwtback;

import java.util.Map;
import java.util.function.Consumer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.stereotype.Component;

/**
 *
 * @author stepin
 */
@Component
public class CustomTokenConverter extends DefaultAccessTokenConverter {

    @Override
    public OAuth2Authentication extractAuthentication(Map<String, ?> claims) {
        OAuth2Authentication authentication = super.extractAuthentication(claims);
        authentication.setDetails(claims);
        
        System.out.println("*** CLAIMS:");
        claims.entrySet().stream()
                .forEach((Map.Entry<String, ?> entry) -> 
                        System.out.println(entry.getKey() + ":" + entry.getValue().toString()));
        System.out.println("******************************************");
        
        return authentication;
    }

}
