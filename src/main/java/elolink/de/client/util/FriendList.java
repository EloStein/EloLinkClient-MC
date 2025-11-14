package elolink.de.client.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FriendList {

    public static class Friend {
        String uuid;
        String name;
        String profile_picture;
        String timestamp;

        public Friend(String uuid, String name, String profile_picture, LocalDateTime timestamp) {
            this.uuid = uuid;
            this.name = name;
            this.profile_picture = profile_picture;
            this.timestamp = String.valueOf(timestamp);
        }
    }

    static class User {
        List<Friend> friends;

        User() {
            this.friends = new ArrayList<>();
        }
    }

    public static void addFriend(Friend friend){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        User user;

        try (FileReader reader = new FileReader(ResourceLocation.friendsJson)) {
            user = gson.fromJson(reader, User.class);
            if (user.friends == null) {
                user.friends = new ArrayList<>();
            }
        } catch (IOException e) {
            user = new User();
        }

        user.friends.add(friend);

        try (FileWriter writer = new FileWriter(ResourceLocation.friendsJson)) {
            gson.toJson(user, writer);
            System.out.println("Added new Friend: " + friend.timestamp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void removeFriend(String uuid) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        User user;

        try (FileReader reader = new FileReader(ResourceLocation.friendsJson)) {
            user = gson.fromJson(reader, User.class);
            if (user.friends == null) {
                return;
            }
        } catch (IOException e) {
            return;
        }

        boolean removed = user.friends.removeIf(friend -> friend.uuid.equals(uuid));

        if (!removed) {
            return;
        }
        try (FileWriter writer = new FileWriter(ResourceLocation.friendsJson)) {
            gson.toJson(user, writer);
            System.out.println("Removed Friend with uuid: " + uuid);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static String getOwnUuid(){
        return getUuidFromName("Ich");
    }

    public static List<Friend> getFriends() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        User user;

        try (FileReader reader = new FileReader(ResourceLocation.friendsJson)) {
            user = gson.fromJson(reader, User.class);
            if (user == null || user.friends == null) {
                return new ArrayList<>();
            }
            return user.friends;
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static String getUuidFromName(String name){
        List<Friend> friends = getFriends();
        for (Friend friend : friends){
            if (Objects.equals(friend.name, name)){
                return friend.uuid;
            }
        } return null;
    }

    public static String getNameFromUuid(String uuid){
        List<Friend> friends = getFriends();
        for (Friend friend : friends){
            if (Objects.equals(friend.uuid, uuid)){
                return friend.name;
            }
        } return null;
    }

    public static List<String> getFriendNames() {
        List<Friend> friends = getFriends();
        List<String> names = new ArrayList<>();
        for (Friend f : friends) {
            names.add(f.name);
        }
        return names;
    }

    public static String getProfileFromName(String name){
        List<Friend> friends = getFriends();
        for (Friend friend : friends){
            if (Objects.equals(friend.name, name)){
                return friend.profile_picture;
            }
        }
        return null;
    }

}
