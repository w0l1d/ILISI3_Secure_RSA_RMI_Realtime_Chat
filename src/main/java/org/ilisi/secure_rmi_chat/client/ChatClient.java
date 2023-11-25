package org.ilisi.secure_rmi_chat.client;

import javafx.scene.control.Alert;

import java.net.MalformedURLException;
import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class ChatClient extends UnicastRemoteObject implements IChatClient {


    private static ChatClient instance;
    private boolean connectionProblem = false;

    private ChatControllable chatControllable;

    protected ChatClient(String clientServiceName, ChatControllable chatControllable) throws RemoteException {
        registerClientToRMIServer(clientServiceName);
        this.chatControllable = chatControllable;
        instance = this;
    }

    public static ChatClient getInstance() {
        return instance;
    }


    private void registerClientToRMIServer(String clientServiceName) throws RemoteException {
        try {
            Naming.rebind("rmi://localhost/" + clientServiceName, this);
        } catch (ConnectException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Connection problem");
            alert.setContentText("The server seems to be unavailable\nPlease try later");
            connectionProblem = true;
            e.printStackTrace();
        } catch (MalformedURLException me) {
            connectionProblem = true;
            me.printStackTrace();
        }

        System.out.println("Client Listen RMI Server is running...\n");
    }

    public boolean isConnectionProblem() {
        return connectionProblem;
    }

    @Override
    public void receiveMessage(String cryptedMessage, User sender) {
        System.out.println("Crypted message received : " + cryptedMessage);

        chatControllable.receiveMessage(cryptedMessage, sender);
    }

    @Override
    public void updateActiveUsersList(List<User> activeUsers) {
        System.out.println("Active users list updated");
        activeUsers.forEach(user -> System.out.println(user.getUsername()));
        chatControllable.updateActiveUsersList(activeUsers);
    }

    @Override
    public void addActiveUser(User user) {
        System.out.println("User added : " + user.getUsername());
        chatControllable.addActiveUser(user);

    }

    @Override
    public void removeActiveUser(User user) {
        System.out.println("User removed : " + user.getUsername());
        chatControllable.removeActiveUser(user);
    }

    public void leaveChat() throws RemoteException {
        chatControllable.leaveChat();
    }
}
