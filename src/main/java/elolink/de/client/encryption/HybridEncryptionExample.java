package elolink.de.client.encryption;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

public class HybridEncryptionExample {

    public static void main(String[] args) {
        try {
            // === RSA-Schlüsselpaar erzeugen === (Empfänger)
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair rsaKeyPair = keyGen.generateKeyPair();
            PublicKey rsaPublicKey = rsaKeyPair.getPublic();
            PrivateKey rsaPrivateKey = rsaKeyPair.getPrivate();

            // === AES-Schlüssel erzeugen === (Sender)
            KeyGenerator aesKeyGen = KeyGenerator.getInstance("AES");
            aesKeyGen.init(128); // 128 Bit AES
            SecretKey aesKey = aesKeyGen.generateKey();

            // === Nachricht === (Sender)
            String originalMessage = "Dies ist eine sehr lange Nachricht, die nicht direkt mit RSA verschlüsselt werden kann, weil sie größer als 245 Bytes ist.";
            System.out.println(originalMessage);

            // === Nachricht mit AES verschlüsseln === (Sender)
            Cipher aesCipher = Cipher.getInstance("AES");
            aesCipher.init(Cipher.ENCRYPT_MODE, aesKey);
            byte[] encryptedMessageBytes = aesCipher.doFinal(originalMessage.getBytes());
            String encryptedMessageBase64 = Base64.getEncoder().encodeToString(encryptedMessageBytes);

            // === AES-Schlüssel mit RSA verschlüsseln === (Sender)
            Cipher rsaCipher = Cipher.getInstance("RSA");
            rsaCipher.init(Cipher.ENCRYPT_MODE, rsaPublicKey);
            byte[] encryptedAesKey = rsaCipher.doFinal(aesKey.getEncoded());
            String encryptedAesKeyBase64 = Base64.getEncoder().encodeToString(encryptedAesKey);

            // === Ausgabe: Beides wird übertragen === (Sender -> Server -> Empfänger)
            System.out.println("Verschlüsselter AES-Schlüssel (Base64):\n" + encryptedAesKeyBase64);
            System.out.println("Verschlüsselte Nachricht (Base64):\n" + encryptedMessageBase64);

            // === AES-Schlüssel entschlüsseln mit RSA === (Empfänger)
            rsaCipher.init(Cipher.DECRYPT_MODE, rsaPrivateKey);
            byte[] decryptedAesKeyBytes = rsaCipher.doFinal(Base64.getDecoder().decode(encryptedAesKeyBase64));
            SecretKey originalAesKey = new SecretKeySpec(decryptedAesKeyBytes, 0, decryptedAesKeyBytes.length, "AES");

            // === Nachricht entschlüsseln mit AES === (Empfänger)
            aesCipher.init(Cipher.DECRYPT_MODE, originalAesKey);
            byte[] decryptedMessageBytes = aesCipher.doFinal(Base64.getDecoder().decode(encryptedMessageBase64));
            String decryptedMessage = new String(decryptedMessageBytes);

            System.out.println("\nEntschlüsselte Nachricht:\n" + decryptedMessage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
