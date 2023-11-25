package myApp.utils;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.SecureRandom;
import java.util.Base64;

public class HashManager {
    private static final int SALT_LENGTH = 16; // Length of the salt in bytes
    private static final int ITERATIONS = 10000; // Number of iterations for PBKDF2
    private static final int KEY_LENGTH = 128; // Key length for PBKDF2

    // Generate a random salt
    private static byte[] generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);
        return salt;
    }

    // Hash the password using PBKDF2
    public static String hashPassword(String password) {
        try {
            byte[] salt = generateSalt();

            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            SecretKey key = factory.generateSecret(spec);

            // Convert the key and salt to Base64-encoded strings for storage
            Base64.Encoder encoder = Base64.getEncoder();
            String encodedSalt = encoder.encodeToString(salt);
            String encodedKey = encoder.encodeToString(key.getEncoded());

            // Concatenate the salt and key with a delimiter (you can choose a different delimiter)
            return encodedSalt + ":" + encodedKey;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            // Handle exceptions
            e.printStackTrace();
            return null;
        }
    }

    // Validate the password against the stored hash
    public static boolean validatePassword(String password, String storedHash) {
        try {
            // Split the stored hash into salt and key using the delimiter (you chose in hashPassword)
            String[] parts = storedHash.split(":");
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] storedKey = Base64.getDecoder().decode(parts[1]);

            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            SecretKey key = factory.generateSecret(spec);

            // Compare the derived key with the stored key
            return MessageDigest.isEqual(key.getEncoded(), storedKey);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            // Handle exceptions
            e.printStackTrace();
            return false;
        }
    }
}
