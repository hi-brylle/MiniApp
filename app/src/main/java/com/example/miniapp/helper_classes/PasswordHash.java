package com.example.miniapp.helper_classes;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class PasswordHash {

    public static String hash(String plainTextPassword) {
        String hash = null;
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
            md.update(plainTextPassword.getBytes());
            byte[] bytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }
}
