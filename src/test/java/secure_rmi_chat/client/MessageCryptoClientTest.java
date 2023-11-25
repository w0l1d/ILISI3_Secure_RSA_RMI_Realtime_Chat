package secure_rmi_chat.client;

import org.ilisi.secure_rmi_chat.client.MessageCryptoClient;
import org.junit.jupiter.api.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static org.junit.jupiter.api.Assertions.*;

public class MessageCryptoClientTest {

    @Test
    public void testKeyGeneration() {
        MessageCryptoClient cryptoClient = new MessageCryptoClient();
        assertNotNull(cryptoClient.getPublicKey());
        assertNotNull(cryptoClient.getPrivateKey());
    }

    @Test
    public void testPublicKeyAsString() {
        MessageCryptoClient cryptoClient = new MessageCryptoClient();
        String publicKeyStr = cryptoClient.getPublicKeyAsString();
        assertNotNull(publicKeyStr);
        assertFalse(publicKeyStr.isEmpty());
    }

    @Test
    public void testEncryptionAndDecryption() throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException, InvalidKeyException {
        MessageCryptoClient senderCrypto = new MessageCryptoClient();
        MessageCryptoClient receiverCrypto = new MessageCryptoClient();

        String originalMessage = "Hello, secure world!";
        String encryptedMessage = senderCrypto.encryptMessage(originalMessage, receiverCrypto.getPublicKeyAsString());
        assertNotNull(encryptedMessage);

        String decryptedMessage = receiverCrypto.decryptMessage(encryptedMessage);
        System.out.printf("Original message: %s\nEncrypted message: %s\nDecrypted message: %s%n",
                originalMessage, encryptedMessage, decryptedMessage);
        assertEquals(originalMessage, decryptedMessage);
    }



    @Test
    public void testEncryptionWithInvalidPublicKey() {
        MessageCryptoClient senderCrypto = new MessageCryptoClient();
        String invalidPublicKeyStr = "invalid_public_key_string";

        assertThrows(Exception.class,
                () -> senderCrypto.encryptMessage("Message", invalidPublicKeyStr),
                "Encrypting with an invalid public key should throw an exception");
    }

    @Test
    public void testDecryptionWithInvalidPrivateKey() {
        MessageCryptoClient cryptoClient = new MessageCryptoClient();
        String encryptedMessage = "encrypted_message_string";

        Throwable exception = assertThrows(Exception.class,
                () -> cryptoClient.decryptMessage(encryptedMessage),
                "Decrypting with an invalid private key should throw an exception");
    }

}
