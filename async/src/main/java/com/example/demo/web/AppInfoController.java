package com.example.demo.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/info")
public class AppInfoController {

    @Value("${application.name:???}")
    private String appName;

    @Value("${application.version:???}")
    private String appVersion;

    @GetMapping
    public ResponseEntity<?> info() {
        return ok(Map.of("name", appName, "version", appVersion));
    }
}
