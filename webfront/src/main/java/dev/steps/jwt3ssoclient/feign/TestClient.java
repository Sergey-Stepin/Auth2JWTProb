/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.steps.jwt3ssoclient.feign;

import org.springframework.cloud.openfeign.FeignClient;
import steps.dev.prob.oauth2.remote_api.contracts.TestContract;

/**
 *
 * @author stepin
 */

//@FeignClient(name = "jwt-back", configuration = TestClientConfiguration.class)
@FeignClient(name = "jwt-back")
public interface TestClient extends TestContract{
    
}
