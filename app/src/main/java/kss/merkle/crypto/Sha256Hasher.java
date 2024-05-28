package kss.merkle.crypto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Sha256Hasher {
    static MessageDigest messageDigest;

    static {
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] hash(byte[] input) {
        messageDigest.update(input);
        return messageDigest.digest();
    }
}
