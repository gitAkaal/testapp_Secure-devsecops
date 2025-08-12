package com.wordstop;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class CommandExecutor {
    /**
     * WARNING: This class contains intentional OS command injection vulnerabilities
     * DO NOT USE IN PRODUCTION
     */
    
    public String executeCommand(String command) {
        StringBuilder output = new StringBuilder();
        
        try {
            // Vulnerable: Direct execution of user input
            Process process = Runtime.getRuntime().exec(command);
            
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream())
            );
            
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            
            process.waitFor();
            
        } catch (IOException | InterruptedException e) {
            return "Error executing command: " + e.getMessage();
        }
        
        return output.toString();
    }
    
    public String checkSystem(String host) {
        try {
            // Vulnerable: Command injection through string concatenation
            String command = "ping " + host;
            Process process = Runtime.getRuntime().exec(command);
            
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream())
            );
            
            StringBuilder output = new StringBuilder();
            String line;
            
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            
            process.waitFor();
            return output.toString();
            
        } catch (IOException | InterruptedException e) {
            return "Error: " + e.getMessage();
        }
    }
    
    // Example usage with vulnerable endpoints
    public static void main(String[] args) {
        CommandExecutor executor = new CommandExecutor();
        
        // These examples show how the vulnerabilities could be exploited
        System.out.println("Example 1 - Normal usage:");
        System.out.println(executor.executeCommand("dir"));
        
        System.out.println("\nExample 2 - Potential injection point:");
        System.out.println(executor.checkSystem("localhost"));
        
        // WARNING: These methods are vulnerable to command injection
        // For example, input like "localhost & del important.txt" would be dangerous
    }
}
