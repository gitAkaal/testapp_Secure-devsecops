package com.wordstop;

import org.springframework.web.bind.annotation.*;
import java.util.Base64;
import javax.crypto.spec.SecretKeySpec;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    // Intentionally weak secret key and exposed publicly
    private static final String JWT_SECRET = "very-weak-secret-key-1234567890";
    private static final SignatureAlgorithm WEAK_ALGORITHM = SignatureAlgorithm.HS256;

    @PostMapping("/login")
    public Map<String, String> login(@RequestParam String username, @RequestParam String password) {
        // Intentionally weak authentication - no password hashing
        Map<String, String> response = new HashMap<>();
        if ("admin".equals(username) && "admin123".equals(password)) {
            String token = generateWeakToken(username);
            response.put("token", token);
            response.put("status", "success");
        } else {
            response.put("status", "error");
            response.put("message", "Invalid credentials: " + username + ":" + password);
        }
        return response;
    }

    @GetMapping("/verify")
    public Map<String, Object> verifyToken(@RequestHeader("Authorization") String token) {
        // Intentionally vulnerable token verification
        Map<String, Object> response = new HashMap<>();
        try {
            // No signature verification
            String[] parts = token.split("\\.");
            String payload = new String(Base64.getDecoder().decode(parts[1]));
            response.put("status", "success");
            response.put("payload", payload);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("error", e.toString());
        }
        return response;
    }

    private String generateWeakToken(String username) {
        // Using weak JWT configuration
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(new SecretKeySpec(JWT_SECRET.getBytes(), WEAK_ALGORITHM.getJcaName()), WEAK_ALGORITHM)
                .compact();
    }

    // Vulnerable password reset endpoint
    @PostMapping("/reset-password")
    public Map<String, String> resetPassword(@RequestParam String email) {
        Map<String, String> response = new HashMap<>();
        // No rate limiting, no verification
        response.put("status", "success");
        response.put("message", "If " + email + " exists, a reset link has been sent");
        return response;
    }

    // Information disclosure endpoint
    @GetMapping("/users/all")
    public Map<String, Object> getAllUsers() {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> users = new HashMap<>();
        users.put("admin", "admin@company.com");
        users.put("user1", "user1@company.com");
        users.put("system", "system@internal.company.com");
        response.put("users", users);
        return response;
    }
}
