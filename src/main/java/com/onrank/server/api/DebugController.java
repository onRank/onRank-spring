package com.onrank.server.api;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class DebugController {
    @GetMapping("/debug/headers")
    public Map<String,String> headers(HttpServletRequest req) {
        return Map.of(
                "Host",               req.getHeader("Host"),
                "X-Forwarded-Host",   req.getHeader("X-Forwarded-Host"),
                "X-Forwarded-Proto",  req.getHeader("X-Forwarded-Proto")
        );
    }
}
