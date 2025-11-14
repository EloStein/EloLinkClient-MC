package elolink.de.client.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import elolink.de.client.encryption.RSADecryption;
import elolink.de.client.errors.Error;
import elolink.de.client.errors.Errors;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class ChatReader {

    static class UnifiedMessage {
        String transmitterUUID;
        String addresseeUUID;
        String message;
        LocalDateTime timestamp;

        public UnifiedMessage(String transmitterUUID, String addresseeUUID, String message, String timestamp) {
            this.transmitterUUID = transmitterUUID;
            this.addresseeUUID = addresseeUUID;
            this.message = message;
            this.timestamp = LocalDateTime.parse(timestamp);
        }
    }

    static class File1 {
        String uuid;
        String publicKey;
        List<Entry1> entries;

        static class Entry1 {
            String addresseeUUID;
            String message_encrypted_with_own_public_key;
            String timestamp;
        }
    }

    static class File2 {
        String uuid;
        String publicKey;
        List<Entry2> entries;

        static class Entry2 {
            String addresseeUUID;
            String message_encrypted_with_addressee_public_key;
            String timestamp;
        }
    }

    public static boolean mergeMessagesToChatList(String  AddresseeUUID, String uuid){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        List<UnifiedMessage> allMessages = new ArrayList<>();


        try {
            File1 file1 = gson.fromJson(new FileReader(ResourceLocation.chatsFolder + FriendList.getOwnUuid() +".json"), File1.class);
            for (File1.Entry1 entry : file1.entries) {
                if (Objects.equals(entry.addresseeUUID, AddresseeUUID)) {
                    allMessages.add(new UnifiedMessage(
                            file1.uuid,
                            entry.addresseeUUID,
                            entry.message_encrypted_with_own_public_key,
                            entry.timestamp
                    ));
                }
            }
        }catch (Exception e) {
            Error.withCode(Errors.CouldNotReadMyChatJson,uuid+".json");
        }

        try {
            File2 file2 = gson.fromJson(new FileReader(ResourceLocation.chatsFolder + AddresseeUUID + ".json"), File2.class);
            for (File2.Entry2 entry : file2.entries) {
                allMessages.add(new UnifiedMessage(
                        file2.uuid,
                        entry.addresseeUUID,
                        entry.message_encrypted_with_addressee_public_key,
                        entry.timestamp
                ));
            }
        }catch (Exception e) {
            Error.withCode(Errors.CouldNotReadCounterPartChatJson,AddresseeUUID + ".json");
        }

            allMessages.sort(Comparator.comparing(msg -> msg.timestamp));

            // Returns if no Messages are Present
            if (allMessages.isEmpty()) {
                return false;
            }

            // If the Json does NOT equal the current Chat
                //if (ChatsPane.getLastChatTime() == null || !allMessages.getLast().timestamp.toString().equals(ChatsPane.getLastChatTime().toString())) {

                    // Info
                    //System.out.println(allMessages.getLast().timestamp + "  /  " + chatWindow.getTimeOfLastChat() + "  - Saved New chats");

                    // Remove all chats to add the new Ones
                    //ChatsPane.removeAllChats();

                    // Add all Messages in correct Order to the Chat

                    for (UnifiedMessage msg : allMessages) {
                        MessageWriter writer;
                        if (Objects.equals(msg.transmitterUUID, FriendList.getOwnUuid())) {
                            writer = MessageWriter.MYSELF;
                        } else {
                            writer = MessageWriter.COUNTERPART;
                        }

                        // Add the new Messages
//                        chatWindow.addChatElement(msg.message, msg.timestamp, writer, ColorTheme.getDefaultChatColor());
//                        chatWindow.setTimeOfLastChat(msg.timestamp);
                        //ChatsPane.addChatElementAndRefresh(msg.message, msg.timestamp, writer, ColorTheme.getDefaultChatColor());

                    }

                    //WindowUtil.scrollToBottom(ChatsPane.getInstance());

                //} else {
                //    return false;
                //}

//            if (chatWindow.getTimeOfLastChat() != null) {
//                chatWindow.setTimeOfLastChat(allMessages.getLast().timestamp);
//            }
            return true;
    }

    public static PublicKey getPublicKeyFromUUIDUsingChatJson(String uuid){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            File1 file = gson.fromJson(new FileReader("src/client/chats/" + uuid + ".json"), File1.class);
            String publicKey = file.publicKey;
            try {
                return RSADecryption.parseAndLoadPublicKey(publicKey);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
