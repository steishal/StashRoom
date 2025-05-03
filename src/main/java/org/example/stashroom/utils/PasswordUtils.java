package org.example.stashroom.utils;
import org.springframework.security.crypto.password.PasswordEncoder;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;


public class PasswordUtils implements PasswordEncoder {
    @Value("${security.hash.saltLength}")
    private static int saltLength;
    @Value("${security.hash.iterations}")
    private static int iterations;
    @Value("${security.hash.keyLength}")
    private static int keyLength;

    private static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[saltLength];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    @Override
    public String encode(CharSequence rawPassword) {
        try {
            String salt = generateSalt();
            byte[] hash = hashPasswordWithSalt(rawPassword.toString(), salt);
            return Base64.getEncoder().encodeToString(hash) + ":" + salt;
        } catch (Exception e) {
            throw new RuntimeException("Error encoding password", e);
        }
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        try {
            String[] parts = encodedPassword.split(":");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid password format");
            }
            String storedHash = parts[0];
            String storedSalt = parts[1];

            byte[] inputHash = hashPasswordWithSalt(rawPassword.toString(), storedSalt);
            return storedHash.equals(Base64.getEncoder().encodeToString(inputHash));
        } catch (Exception e) {
            throw new RuntimeException("Error verifying password", e);
        }
    }

    private static byte[] hashPasswordWithSalt(String password, String salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(
                    password.toCharArray(),
                    Base64.getDecoder().decode(salt),
                    iterations,
                    keyLength
            );
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return keyFactory.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
}