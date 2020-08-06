/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package steos.dev.jwt.jwtback;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import steps.dev.prob.oauth2.remote_api.contracts.TestContract;

/**
 *
 * @author stepin
 */

@Controller
public class TestController implements TestContract{
    
    @Override
    @GetMapping("test")
    public ResponseEntity<String> getTest(){
        System.out.println("TEST.Start");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("TEST.authentication:" + authentication);
        return ResponseEntity.ok(authentication.toString());
    }
}
