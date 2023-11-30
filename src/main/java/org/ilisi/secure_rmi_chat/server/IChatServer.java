package org.ilisi.secure_rmi_chat.server;

import org.ilisi.secure_rmi_chat.client.User;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IChatServer extends Remote {

    // send message from `senderUserId` to `receiverUserId` with content `message`
    void sendMessage(String message, String receiverUserId, String senderUserId) throws RemoteException;

    // register user to chat
    void registerUser(User user) throws RemoteException;

    // unregister user from chat
    void unregisterUser(User user) throws RemoteException;

    // user leaves chat
    void leaveChat(User user) throws RemoteException;
}
