package vg.edu.pe.HinoPE.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordUtil {
    
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);
    
    /**
     * Hash a plain text password using BCrypt
     */
    public static String hashPassword(String plainPassword) {
        return encoder.encode(plainPassword);
    }
    
    /**
     * Verify a plain text password against a hashed password
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        return encoder.matches(plainPassword, hashedPassword);
    }
}
