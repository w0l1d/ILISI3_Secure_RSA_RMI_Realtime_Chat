package secure_rmi_chat.client;

import org.ilisi.secure_rmi_chat.client.MessageCryptoClient;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MessageCryptoClientTest {

    @Test
    // Test that the public and private keys are generated
    public void testKeyGeneration() {
        MessageCryptoClient cryptoClient = new MessageCryptoClient();
        assertNotNull(cryptoClient.getPublicKey());
        assertNotNull(cryptoClient.getPrivateKey());
    }

    @Test
    // Test that the public key can be converted to a string
    // This is needed to send the public key to the server
    public void testPublicKeyAsString() {
        MessageCryptoClient cryptoClient = new MessageCryptoClient();
        String publicKeyStr = cryptoClient.getPublicKeyAsString();
        assertNotNull(publicKeyStr);
        assertFalse(publicKeyStr.isEmpty());
    }

    @Test

    public void testEncryptionAndDecryption() {
        // Create two instances of the MessageCryptoClient
        MessageCryptoClient senderCrypto = new MessageCryptoClient();
        MessageCryptoClient receiverCrypto = new MessageCryptoClient();

        // Encrypt a message with the receiver's public key
        String originalMessage = "Hello, secure world!";
        String encryptedMessage = senderCrypto.encryptMessage(originalMessage, receiverCrypto.getPublicKeyAsString());
        assertNotNull(encryptedMessage);

        // Decrypt the message with the receiver's private key
        String decryptedMessage = receiverCrypto.decryptMessage(encryptedMessage);
        System.out.printf("Original message: %s\nEncrypted message: %s\nDecrypted message: %s%n",
                originalMessage, encryptedMessage, decryptedMessage);
        assertEquals(originalMessage, decryptedMessage);
    }


    @Test
    public void testEncryptionWithInvalidPublicKey() {
        // Create an instance of the MessageCryptoClient
        MessageCryptoClient senderCrypto = new MessageCryptoClient();
        // Create an invalid public key string
        String invalidPublicKeyStr = "invalid_public_key_string";

        // Encrypt a message with the invalid public key
        // This should throw an exception because the public key is invalid
        assertThrows(Exception.class,
                () -> senderCrypto.encryptMessage("Message", invalidPublicKeyStr),
                "Encrypting with an invalid public key should throw an exception");
    }

    @Test
    public void testDecryptionWithInvalidPrivateKey() {
        // Create an instance of the MessageCryptoClient
        MessageCryptoClient cryptoClient = new MessageCryptoClient();
        // Create an invalid encrypted message string
        String encryptedMessage = "encrypted_message_string";

        // Decrypt the message with the invalid private key
        // This should throw an exception because the private key is invalid
        Throwable exception = assertThrows(Exception.class,
                () -> cryptoClient.decryptMessage(encryptedMessage),
                "Decrypting with an invalid private key should throw an exception");
    }

}
