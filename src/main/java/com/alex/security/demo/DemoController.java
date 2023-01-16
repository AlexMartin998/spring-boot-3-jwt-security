package com.alex.security.demo;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/demo-controller")
public class DemoController {

    @GetMapping
    @Secured("ADMIN")
    public ResponseEntity<String> sayHelloAdmin() {
        return ResponseEntity.ok("Hello from secured endpoint - Admin");
    }

    @GetMapping("/free")
    // @Secured("USER")  <-  default: AuthenticationService: register() le establece ese static role
    public ResponseEntity<String> sayHello() {
        return ResponseEntity.ok("Hello from secured endpoint");
    }

}
