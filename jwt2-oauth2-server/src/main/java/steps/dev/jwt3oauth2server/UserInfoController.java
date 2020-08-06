/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package steps.dev.jwt3oauth2server;

import java.security.Principal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author stepin
 */
@RestController
public class UserInfoController {

    @GetMapping("/user/info")
    @ResponseBody
    public Principal user(Principal principal) {
        System.out.println("@@@ principal=" + principal);
        return principal;
    }
}
