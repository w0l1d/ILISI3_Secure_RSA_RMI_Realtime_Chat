package org.ilisi.secure_rmi_chat.client;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class MessageCryptoClient {

    private PublicKey publicKey;
    private PrivateKey privateKey;

    // Constructor to generate public and private keys
    public MessageCryptoClient() {
        try {
            // Generate public and private keys
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            // You can adjust the key size as needed
            keyPairGenerator.initialize(2048);
            // Generate the key pair
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            publicKey = keyPair.getPublic();
            privateKey = keyPair.getPrivate();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    // Method to encrypt a message
    public String encryptMessage(String message, String receiverPublicKey) {
        try {

            // Convert sender's public key from string
            PublicKey senderPublicKey = getPublicKeyFromString(receiverPublicKey);

            // Encrypt the message
            Cipher cipher = Cipher.getInstance("RSA");
            // set the cipher to encrypt mode and use the receiver's public key to encrypt the message
            cipher.init(Cipher.ENCRYPT_MODE, senderPublicKey);
            // encode the encrypted message to base64 string
            // this is to ensure the encrypted message can be sent as a string
            return Base64.getEncoder().encodeToString(cipher.doFinal(message.getBytes()));

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                 IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private static PublicKey getPublicKeyFromString(String receiverPublicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Convert the string to public key format
        byte[] keyBytes = Base64.getDecoder().decode(receiverPublicKey);
        // Create a key specification
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        // Get the public key from the specification
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey senderPublicKey = keyFactory.generatePublic(spec);
        return senderPublicKey;
    }

    // get the public key as a string
    public String getPublicKeyAsString() {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    // Method to decrypt a message
    public String decryptMessage(String encryptedMessage) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedMessage);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                 IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }
    public PrivateKey getPrivateKey() {
        return privateKey;
    }
}
