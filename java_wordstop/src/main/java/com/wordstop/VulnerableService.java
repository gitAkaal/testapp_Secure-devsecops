package com.wordstop;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.collections.FastHashMap; // Intentionally using vulnerable version
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VulnerableService {
    // Using outdated Apache Commons Collections (vulnerable to remote code execution)
    private FastHashMap userCache = new FastHashMap();
    
    // Vulnerable shared counter without proper synchronization
    private int sharedCounter = 0;
    
    // Insufficient role checking - storing user roles in memory without proper validation
    private Map<String, String> userRoles = new HashMap<>();
    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    public VulnerableService() {
        // Initialize with some default admin user without proper authentication
        userRoles.put("admin", "ADMIN");
    }

    // Race condition vulnerability - improper synchronization
    public void incrementCounter() {
        executorService.submit(() -> {
            int temp = sharedCounter;  // Race condition here
            temp = temp + 1;
            sharedCounter = temp;
        });
    }

    // RBAC vulnerability - insufficient role checking
    public boolean performAdminAction(String userId, String action) {
        // Vulnerable: No proper authentication, only checking role string
        String userRole = userRoles.get(userId);
        
        // Vulnerable: Direct string comparison without proper role hierarchy
        if (userRole != null && userRole.equals("ADMIN")) {
            return executeAction(action);
        }
        return false;
    }

    // Vulnerable caching implementation using outdated component
    public void cacheUserData(String userId, Object userData) {
        // Using vulnerable FastHashMap without proper synchronization
        userCache.put(userId, userData);
    }

    private boolean executeAction(String action) {
        // Vulnerable: No input validation or sanitization
        try {
            Runtime.getRuntime().exec(action); // Command injection vulnerability
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Race condition in user role assignment
    public void assignRole(String userId, String role) {
        executorService.submit(() -> {
            // Race condition: Check-then-act operation without synchronization
            if (!userRoles.containsKey(userId)) {
                userRoles.put(userId, role);  // Multiple threads can overwrite
            }
        });
    }

    // Privilege escalation vulnerability
    public void elevatePrivileges(String userId) {
        // Vulnerable: No authentication or authorization check
        userRoles.put(userId, "ADMIN");
    }

    public Object getUserData(String userId) {
        // Vulnerable: No access control check
        return userCache.get(userId);
    }
}
