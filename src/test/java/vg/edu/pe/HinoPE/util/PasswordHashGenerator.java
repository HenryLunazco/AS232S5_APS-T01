package vg.edu.pe.HinoPE.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utility class to generate BCrypt password hashes for testing and database seeding
 * Run this class to generate hashes for your passwords
 */
public class PasswordHashGenerator {
    
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);
        
        String password = "12345678";
        String email = "admin@colegio.edu";
        
        System.out.println("BCrypt Password Hash Generator");
        System.out.println("==============================");
        System.out.println("\nGenerating hash for admin user...\n");
        
        String hash = encoder.encode(password);
        
        System.out.println("Email:    " + email);
        System.out.println("Password: " + password);
        System.out.println("Hash:     " + hash);
        
        // Verify the hash works
        boolean matches = encoder.matches(password, hash);
        System.out.println("Verified: " + matches);
        
        System.out.println("\n\nSQL Update Statement:");
        System.out.println("=====================");
        System.out.println("UPDATE users SET password_hash = '" + hash + "' WHERE email = '" + email + "';");
        
        System.out.println("\n\nCopy the hash above and run the SQL statement in your database.");
    }
}
