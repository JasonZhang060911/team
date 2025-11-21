package data_access.user_storage;

import entity.User;
import java.util.HashMap;
import java.util.Map;
// Temporary storage for users who have signed up but haven't completed style preferences yet.
// prevents saving incomplete user profiles to the database.

public class PendingUserStorage {
    private static PendingUserStorage instance;
    private final Map<String, User> pendingUsers = new HashMap<>();

    private PendingUserStorage() {
        // Private constructor for singleton
    }

    public static PendingUserStorage getInstance() {
        if (instance == null) {
            instance = new PendingUserStorage();
        }
        return instance;
    }

    public void storePendingUser(User user) {
        pendingUsers.put(user.getName(), user);
    }

    public User getPendingUser(String username) {
        return pendingUsers.get(username);
    }

    public void removePendingUser(String username) {
        pendingUsers.remove(username);
    }

    public boolean hasPendingUser(String username) {
        return pendingUsers.containsKey(username);
    }
}

