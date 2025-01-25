package com.ageinghippy.recipeapi.controller;

import com.ageinghippy.recipeapi.model.CustomUserDetails;
import com.ageinghippy.recipeapi.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    CustomUserDetailsService userDetailsService;

    @GetMapping
    public CustomUserDetails getUser(Authentication authentication) {
        return (CustomUserDetails) authentication.getPrincipal();
    }

    @PostMapping
    public ResponseEntity<?> createNewUser(@RequestBody CustomUserDetails userDetails) {
        return ResponseEntity.ok(userDetailsService.createNewUser(userDetails));
    }
}
