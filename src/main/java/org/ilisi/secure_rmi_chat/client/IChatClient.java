package org.ilisi.secure_rmi_chat.client;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IChatClient extends Remote {
    // receive message from server
    void receiveMessage(String message, User sender) throws RemoteException;

    // receive list of active users from server
    void updateActiveUsersList(List<User> activeUsers) throws RemoteException;

    // receive notification from server that a new user has joined
    void addActiveUser(User user) throws RemoteException;

    // receive notification from server that a user has left
    void removeActiveUser(User user) throws RemoteException;
}
