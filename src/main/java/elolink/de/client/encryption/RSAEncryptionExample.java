package elolink.de.client.encryption;

import javax.crypto.Cipher;
import java.security.*;
import java.util.Base64;

public class RSAEncryptionExample {

// Empfänger: Schlüsselpaar erzeugen und teilen mit Server.
// Sender: Nachricht verschlüsseln mit Public Key von Empfänger.
// Empfänger: Nachricht entschlüsseln mit dem eigenen Private Key.





    public static void main(String[] args) {
        try {
            // === 1. Schlüsselpaar erzeugen (Empfänger) ===
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048); // Schlüsselgröße in Bit
            KeyPair keyPair = keyGen.generateKeyPair();
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();
            System.out.println("|||" + publicKey + "|||");
            System.out.println("öffentlicher Schlüssel:" + publicKey + "; privater Schlüssel:" + privateKey);
            System.out.println(publicKey);


            // === 2. Nachricht zum Verschlüsseln (kommt vom Sender) ===
            String originalMessage = "Ein Text ist eine zusammenhängende sprachliche Äußerung, die eine Information oder Geschichte vermittelt und sowohl schriftlich (z. B. ein Buch, ein Artikel) als auch mündlich (z. B. eine Rede, ein Interview) sein kann. Er besteht aus mehreren Sätzen, die inhaltlich und formal miteinander verbunden sind und eine abgeschlossene kommunikative Handlung darstellen. ";
            System.out.println("Originale Nachricht:\n" + originalMessage);

            // === 3. Nachricht verschlüsseln mit dem öffentlichen Schlüssel (Sender) ===
            Cipher encryptCipher = Cipher.getInstance("RSA");
            encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedBytes = encryptCipher.doFinal(originalMessage.getBytes());
            String encryptedBase64 = Base64.getEncoder().encodeToString(encryptedBytes);
            System.out.println("\nVerschlüsselte Nachricht (Base64):\n" + encryptedBase64);

            // === 4. Nachricht entschlüsseln mit dem privaten Schlüssel (Empfänger) ===
            Cipher decryptCipher = Cipher.getInstance("RSA");
            decryptCipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decryptedBytes = decryptCipher.doFinal(Base64.getDecoder().decode(encryptedBase64));
            String decryptedMessage = new String(decryptedBytes);
            System.out.println("\nEntschlüsselte Nachricht:\n" + decryptedMessage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    PublicKey publicKey;
    PrivateKey privateKey;

    public void initKeys() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048); // Schlüsselgröße in Bit
        KeyPair keyPair = keyGen.generateKeyPair();
        publicKey = keyPair.getPublic();
        privateKey = keyPair.getPrivate();
    }


    public PublicKey getPublicKey(){
        return this.publicKey;
    }
}





/*


            KeyPairGenerator keyGen = null;
            try {
                keyGen = KeyPairGenerator.getInstance("RSA");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
            keyGen.initialize(2048); // Schlüsselgröße in Bit
            KeyPair keyPair = keyGen.generateKeyPair();
            PublicKey publicKey = keyPair.getPublic();

            try {
                Objectout.writeObject(publicKey);
                Objectout.flush();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }




ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());

        // PublicKey empfangen
        PublicKey receivedKey = (PublicKey) in.readObject();
        System.out.println("Public Key empfangen: " + receivedKey);

*/
