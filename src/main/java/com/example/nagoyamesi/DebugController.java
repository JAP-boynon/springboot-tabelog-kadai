package com.example.nagoyamesi;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DebugController {

    @GetMapping("/debug/health")
    public String health() {
        return "OK";
    }
}
