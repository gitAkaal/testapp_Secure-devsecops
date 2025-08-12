package com.wordstop;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.HttpURLConnection;

public class URLFetcher {
    // Vulnerable SSRF implementation
    public String fetchUrl(String userInput) {
        try {
            // Vulnerability: No URL validation or whitelist checking
            URL url = new URL(userInput);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // Vulnerability: Following redirects without validation
            HttpURLConnection.setFollowRedirects(true);

            BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream())
            );

            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            return response.toString();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    // Example usage with SSRF vulnerability
    public static void main(String[] args) {
        URLFetcher fetcher = new URLFetcher();
        
        // Vulnerable to SSRF - can access internal network or localhost
        String result = fetcher.fetchUrl("http://localhost:8080/internal/api");
        System.out.println(result);
        
        // Could be used to scan internal network
        result = fetcher.fetchUrl("http://192.168.1.1/admin");
        System.out.println(result);
    }
}
