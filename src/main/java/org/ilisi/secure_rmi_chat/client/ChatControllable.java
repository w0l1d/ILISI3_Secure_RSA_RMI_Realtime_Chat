package org.ilisi.secure_rmi_chat.client;

import java.util.List;

public interface ChatControllable {
    // receive message from server with sender `sender`
    // this method is called when the client receives a message
    void receiveMessage(String message, User sender);

    // receive list of active users from server
    // this method is called when the client joins the chat
    void updateActiveUsersList(List<User> activeUsers);

    // receive notification from server that a new user has joined
    // this method is called when a new user joins the chat
    void addActiveUser(User user);

    // receive notification from server that a user has left
    // this method is called when a user leaves the chat
    void removeActiveUser(User user);

    // leave chat and close client
    // this method is called when the client leaves the chat
    void leaveChat();
}
