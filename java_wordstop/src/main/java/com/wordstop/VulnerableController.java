package com.wordstop;

import org.springframework.web.bind.annotation.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class VulnerableController {

    private static final Map<Integer, String> userdata = new HashMap<>();
    static {
        userdata.put(1, "admin:secretpass");
        userdata.put(2, "user:password123");
    }

    // Vulnerable to XSS - Reflects input without sanitization
    @GetMapping("/echo")
    public String echoInput(@RequestParam String input) {
        return "<div>" + input + "</div>";  // XSS vulnerability
    }

    // Vulnerable to SQL Injection
    @GetMapping("/users/search")
    public String searchUsers(@RequestParam String name) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "");
            Statement stmt = conn.createStatement();
            // Vulnerable SQL query - direct string concatenation
            String query = "SELECT * FROM USERS WHERE username = '" + name + "'";
            stmt.execute(query);
            return "Search completed for: " + name;
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    // Vulnerable to Insecure Direct Object Reference (IDOR)
    @GetMapping("/users/{id}/data")
    public String getUserData(@PathVariable Integer id) {
        // No authentication check - anyone can access any user's data
        return userdata.getOrDefault(id, "User not found");
    }

    // Vulnerable to Command Injection
    @PostMapping("/execute")
    public String executeCommand(@RequestParam String cmd) {
        try {
            Process process = Runtime.getRuntime().exec(cmd);  // Command injection vulnerability
            return "Command executed successfully";
        } catch (Exception e) {
            return "Error executing command: " + e.getMessage();
        }
    }

    // Information Disclosure Vulnerability
    @GetMapping("/system/info")
    public Map<String, String> getSystemInfo() {
        Map<String, String> info = new HashMap<>();
        info.put("java.version", System.getProperty("java.version"));
        info.put("os.name", System.getProperty("os.name"));
        info.put("user.name", System.getProperty("user.name"));
        info.put("user.home", System.getProperty("user.home"));
        return info;  // Exposes sensitive system information
    }

    // Open Redirect Vulnerability
    @GetMapping("/redirect")
    public String redirect(@RequestParam String url) {
        return "redirect:" + url;  // No validation of redirect URL
    }

    // Debug Endpoint (Security Misconfiguration)
    @GetMapping("/debug")
    public Map<String, Object> debugInfo() {
        Map<String, Object> debug = new HashMap<>();
        debug.put("heap_memory", Runtime.getRuntime().totalMemory());
        debug.put("thread_count", Thread.activeCount());
        debug.put("system_env", System.getenv());
        return debug;  // Exposes sensitive debug information
    }
}
