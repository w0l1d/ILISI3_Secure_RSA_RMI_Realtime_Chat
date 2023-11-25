package org.ilisi.secure_rmi_chat.client;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IChatClient extends Remote {
    public void receiveMessage(String message, User sender) throws RemoteException;

    public void updateActiveUsersList(List<User> activeUsers) throws RemoteException;

    public void addActiveUser(User user) throws RemoteException;
    public void removeActiveUser(User user) throws RemoteException;
}
