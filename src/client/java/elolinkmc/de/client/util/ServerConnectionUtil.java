package elolinkmc.de.client.util;

import com.google.common.collect.Queues;
import elolink.de.client.server.HTTPConnection;
import elolink.de.client.util.FriendList;
import elolinkmc.de.client.ElolinkMcClient;
import elolinkmc.de.util.ExceptionUtil;
import io.netty.channel.Channel;
import net.minecraft.network.ClientConnection;

import java.io.*;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServerConnectionUtil {
    private static final Queue<Consumer<ClientConnection>> QUED_TASKS = Queues.<Consumer<ClientConnection>>newConcurrentLinkedQueue();
    private static Channel channel;
    private static PublicKey publicKey;

    private static String serverUrl() {
        return ElolinkMcClientConfig.SERVER_URL.getValue();
    }



    public static void sendPPBase64(String base64) {
        int sendReconnectDelay = 1000;

        // Set the Sender and The Addressee of the Message
        String sender = FriendList.getOwnUuid();
        String message = "§" + sender + ">:" + base64;

        //TODO: Encrypt Message

        // Send Message
        try {
            boolean sent = false;
            while (!sent) {
                try {
                    // Connect to Server and send Message
                    HttpURLConnection conn = (HttpURLConnection) new URL(serverUrl() + "/spp").openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    OutputStream os = conn.getOutputStream();
                    os.write(message.getBytes(StandardCharsets.UTF_8));
                    os.flush();
                    os.close();

                    // Wait for Server Feedback
                    int responseCode = conn.getResponseCode();
                    if (responseCode == 200) {
                        sent = true;
                        sendReconnectDelay = 1000;
                        System.out.println("#Sended Message successfully: " + message);
                    } else {
                        throw new IOException("ServerResponse: " + responseCode);
                    }

                } catch (Exception e) {
                    ElolinkMcClient.LOGGER.warn("Could not send Message: {}\nException: {}", message, ExceptionUtil.makePretty(e));
                    System.out.println("#Reconnecting in " + (sendReconnectDelay / 1000) + "s ...");
                    Thread.sleep(sendReconnectDelay);
                    sendReconnectDelay = Math.min(sendReconnectDelay * 2, 10000);
                }
            }

        } catch (Exception ignored) {}
    }


    public static void sendMessage(String addresseeUuid, String msg) {
        int sendReconnectDelay = 1000;

        // Set the Sender and The Addressee of the Message
        String sender = FriendList.getOwnUuid();
        String message = "§" + sender + ">" + addresseeUuid + ":" + msg; //add password

        //TODO: Encrypt Message

        // Send Message
        try {
            boolean sent = false;
            while (!sent) {
                try {
                    // Connect to Server and send Message
                    HttpURLConnection conn = (HttpURLConnection) new URL(serverUrl() + "/send").openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    OutputStream os = conn.getOutputStream();
                    os.write(message.getBytes(StandardCharsets.UTF_8));
                    os.flush();
                    os.close();

                    // Wait for Server Feedback
                    int responseCode = conn.getResponseCode();
                    if (responseCode == 200) {
                        sent = true;
                        sendReconnectDelay = 1000;
                        System.out.println("#Sended Message successfully: " + message);
                    } else {
                        throw new IOException("ServerResponse: " + responseCode);
                    }

                } catch (Exception e) {
                    System.out.println("#Couldn't send Message: " + message + "; " + e.getMessage());
                    System.out.println("#Reconnecting in " + (sendReconnectDelay / 1000) + "s ...");
                    Thread.sleep(sendReconnectDelay);
                    sendReconnectDelay = Math.min(sendReconnectDelay * 2, 10000);
                }
            }

        } catch (Exception ignored) {}
    }

    public static String getPPBase64(String uuid){
        try {

            URL url = new URL(serverUrl() + "/pp?uuid=" + uuid);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));

                StringBuilder s = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    s.append(line);
                }
                in.close();

                // Info
                System.out.println("Profile Picture Base64 saved: " + uuid);

                return s.toString();
            } else {
                System.out.println("Error getting Profile Picture. Serverfeedback: " + responseCode);
            }

        } catch (Exception e) {
            System.out.println("Error getting Profile Picture: " + e.getMessage());
        }
        return null;
    }

    public static void getChatJson(String uuid) {
        try {
            if (uuid == null){
                return;
            }
            URL url = new URL(serverUrl() + "/sendJson?uuid=" + uuid);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder jsonBuilder = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    jsonBuilder.append(line);
                }
                in.close();

                File folder = new File("chats/");
                if (!folder.exists()) {
                    folder.mkdirs();
                }

                // Write the updated JsonFile
                File outputFile = new File(folder, uuid + ".json");
                FileWriter writer = new FileWriter(outputFile);
                writer.write(jsonBuilder.toString());
                writer.close();

                // Info
                System.out.println("JSON-File saved as: " + outputFile.getAbsolutePath());
            } else {
                System.out.println("Error getting JsonFile. Serverfeedback: " + responseCode);
            }

        } catch (Exception e) {
            System.out.println("Error getting JsonFile: " + e.getMessage());
        }
    }

    public static void getPublicKeyFromServer(String uuid){
        try {
            // Build Connection to Server
            HttpURLConnection conn = (HttpURLConnection) new URL(serverUrl() + "/keys").openConnection();
            conn.setRequestMethod("POST");


            // Write the uuid to the Server
            conn.setDoOutput(true);
            OutputStream os = conn.getOutputStream();
            os.write(uuid.getBytes());
            os.flush();
            os.close();

            int responseCode = conn.getResponseCode();
            // Check the ResponseCode
            if (responseCode == 200) {
                // Get the Public Key from the Server
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder keyBuilder = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    keyBuilder.append(line);
                }
                in.close();
                ServerConnectionUtil.setAddresseePublicKey(parseStringToPublicKey(keyBuilder.toString()));
            } else {
                throw new IOException("ServerResponse: " + responseCode);
            }
        }catch (Exception e){

        }

    }

    public static void setAddresseePublicKey(PublicKey publicKey) {
        ServerConnectionUtil.publicKey = publicKey;
    }

    public static PublicKey getAddresseePublicKey(){
        return ServerConnectionUtil.publicKey;
    }

    public static PublicKey parseStringToPublicKey(String rawKeyString) throws Exception {
        // Regex für Modulus und Exponent
        Pattern modulusPattern = Pattern.compile("modulus:\\s*(\\d+)");
        Pattern exponentPattern = Pattern.compile("public exponent:\\s*(\\d+)");

        Matcher modMatcher = modulusPattern.matcher(rawKeyString);
        Matcher expMatcher = exponentPattern.matcher(rawKeyString);

        if (!modMatcher.find() || !expMatcher.find()) {
            throw new IllegalArgumentException("Couldn't find Modulus or Exponent of PublicKey");
        }

        BigInteger modulus = new BigInteger(modMatcher.group(1));
        BigInteger exponent = new BigInteger(expMatcher.group(1));

        RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, exponent);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        return factory.generatePublic(spec);
    }
}
