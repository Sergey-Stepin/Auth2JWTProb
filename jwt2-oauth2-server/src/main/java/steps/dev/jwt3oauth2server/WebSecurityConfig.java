/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package steps.dev.jwt3oauth2server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;

/**
 *
 * @author stepin
 */

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter{
    

    @Override
    public void configure(HttpSecurity http) throws Exception {
            http.csrf().disable()
                    .authorizeRequests().antMatchers("/oauth/authorize", "/user/info").authenticated()
                    .and()
                    .formLogin().permitAll()
                    .and()
                    .authorizeRequests().anyRequest().authenticated();
        }    
    
}
