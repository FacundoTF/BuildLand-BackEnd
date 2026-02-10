package com.uai.BuildLand.config;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Clase para encriptar y verificar contraseñas
 */
public class PasswordEncoder {

    /**
     * Encripta una contraseña usando SHA-256
     * @param password Contraseña en texto plano
     * @return Contraseña encriptada en Base64
     */
    public static String encode(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(encodedHash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error encoding password", e);
        }
    }

    /**
     * Verifica si una contraseña en texto plano coincide con una encriptada
     * @param password Contraseña en texto plano
     * @param encryptedPassword Contraseña encriptada
     * @return true si coinciden, false en caso contrario
     */
    public static boolean matches(String password, String encryptedPassword) {
        String encodedPassword = encode(password);
        return encodedPassword.equals(encryptedPassword);
    }

    // public static void main(String[] args) {
    //     String password = "HolaMundo";
    //     String encrypted = encode(password);
    //     System.out.println("Contraseña encriptada: " + encrypted);

    //     boolean match = matches("HolaMundo", encrypted);
    //     System.out.println("¿Coinciden? " + match);
    // }
}


