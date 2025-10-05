package com.greenfox.dramacsoport.petclinicbackend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/about")
public class AboutController {
    @GetMapping("/")
    public ResponseEntity<String> about() {
        return new ResponseEntity<>("Welcome to the Pet Clinic Backend API! Current version: v1.1", org.springframework.http.HttpStatus.OK);
    }
}
