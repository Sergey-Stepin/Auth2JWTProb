/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package steps.dev.prob.oauth2.remote_api.contracts;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

/**
 *
 * @author stepin
 */
public interface TestContract {
    
    @GetMapping("test")
    public ResponseEntity<String> getTest();
    
}
