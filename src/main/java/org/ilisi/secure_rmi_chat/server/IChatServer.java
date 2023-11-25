package org.ilisi.secure_rmi_chat.server;

import org.ilisi.secure_rmi_chat.client.User;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IChatServer extends Remote {

    public void sendMessage(String message, String receiverUserId, String senderUserId) throws RemoteException;

    public void registerUser(User user) throws RemoteException;

    public void unregisterUser(User user) throws RemoteException;

    public List<User> getActiveUsers() throws RemoteException;

    public void leaveChat(User user) throws RemoteException;
}
