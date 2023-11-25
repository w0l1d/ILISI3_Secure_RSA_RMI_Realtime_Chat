package org.ilisi.secure_rmi_chat.client;

import java.util.List;

public interface ChatControllable {
    public void receiveMessage(String message, User sender);

    public void updateActiveUsersList(List<User> activeUsers);

    public void addActiveUser(User user);

    public void removeActiveUser(User user);

    public void leaveChat();
}
