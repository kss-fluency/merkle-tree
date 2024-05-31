package kss.merkle.crypto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Sha256Hasher implements Hasher {
    private final MessageDigest messageDigest;

    public Sha256Hasher() {
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] hash(byte[] input) {
        messageDigest.update(input);
        return messageDigest.digest();
    }
}
