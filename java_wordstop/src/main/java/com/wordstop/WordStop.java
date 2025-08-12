package com.wordstop;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.io.IOException;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.nio.file.Files;
import java.nio.file.Paths;

public class WordStop {
    // Vulnerability: Hardcoded Credentials
    private static final String DB_URL = "jdbc:mysql://localhost:3306/testdb";
    private static final String DB_USER = "admin";
    private static final String DB_PASSWORD = "password123";
    
    // Vulnerability: Hardcoded Encryption Key
    private static final String ENCRYPTION_KEY = "mysecretkey12345";

    public static void main(String[] args) {
        WordStop app = new WordStop();
        app.run();
    }

    public void run() {
        try {
            // Vulnerability: Insecure Database Connection
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            
            // Vulnerability: SQL Injection
            String username = getUserInput();
            String password = getUserInput();
            Statement stmt = conn.createStatement();
            String query = "SELECT * FROM users WHERE username = '" + username + "' AND password = '" + password + "'";
            stmt.executeQuery(query);
            
            // Vulnerability: Command Injection
            String userInput = getUserInput();
            executeCommand(userInput);
            
            // Vulnerability: Weak Encryption
            String sensitiveData = "secret_data";
            String encrypted = encryptWeakly(sensitiveData);
            
            // Vulnerability: Path Traversal
            String fileName = getUserInput();
            readFile(fileName);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Vulnerability: Command Injection
    private void executeCommand(String input) throws IOException {
        Runtime.getRuntime().exec("cmd.exe /c " + input);
    }
    
    // Vulnerability: Weak Encryption
    private String encryptWeakly(String data) throws Exception {
        // Using weak encryption algorithm
        SecretKeySpec key = new SecretKeySpec(ENCRYPTION_KEY.getBytes(), "DES");
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encrypted = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }
    
    // Vulnerability: Path Traversal
    private String readFile(String fileName) throws IOException {
        // No path validation
        return new String(Files.readAllBytes(Paths.get(fileName)));
    }
    
    // Vulnerability: Weak Hash
    private String hashPassword(String password) throws Exception {
        // Using weak MD5 hash
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hashBytes = md.digest(password.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
    
    private String getUserInput() {
        // Simplified for demonstration
        return "";
    }
}
