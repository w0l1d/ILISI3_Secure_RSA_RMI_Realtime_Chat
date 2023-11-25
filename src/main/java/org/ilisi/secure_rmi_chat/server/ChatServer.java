package org.ilisi.secure_rmi_chat.server;

import javafx.scene.control.Alert;
import org.ilisi.secure_rmi_chat.client.IChatClient;
import org.ilisi.secure_rmi_chat.client.User;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChatServer extends UnicastRemoteObject implements IChatServer {
    
    Map<String, Chatter> chatters;
    protected ChatServer() throws RemoteException {
        System.out.println("Server started");
        chatters = new Hashtable<>();
    }

    @Override
    public void sendMessage(String message, String receiverUserId, String senderUserId) throws RemoteException {
        System.out.println("Message received : " + message);
        AtomicBoolean isSent = new AtomicBoolean(false);
        chatters.get(receiverUserId).client().receiveMessage(message, chatters.get(senderUserId).user());
    }

    @Override
    public void registerUser(User user) throws RemoteException {
        System.out.println(String.format("User %s registered with username %s", user.getId(), user.getUsername()));
        try {
            IChatClient client = (IChatClient) Naming.lookup("rmi://localhost/" + user.getId());
            Chatter chatter = new Chatter(user, client);
            client.updateActiveUsersList(chatters.values().stream().map(Chatter::user).toList());
            List<User> usersToRemove = new Vector<>();
            chatters.values().forEach(chatter1 -> {
                try {
                    chatter1.client().addActiveUser(user);
                } catch (RemoteException e) {
                    usersToRemove.add(chatter1.user());
                    System.err.println("User " + chatter1.user().getUsername() + " is not available anymore");
                }
            });
            chatters.put(user.getId(), chatter);
            usersToRemove.forEach(user1 -> {
                try {
                    unregisterUser(user1);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            });

        } catch (NotBoundException | MalformedURLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Getting new chatter client problem");
            alert.setHeaderText(String.format("The user %s may not be registered", user.getId()));
            alert.setContentText(String.format("Exception type : %s\nException message : %s", e.getClass().getName(), e.getMessage()));
            alert.showAndWait();
        }
    }

    @Override
    public void unregisterUser(User user) throws RemoteException {
        System.out.printf("User %s unregistered with username %s%n", user.getId(), user.getUsername());
        chatters.remove(user.getId());
        chatters.values().forEach(chatter1 -> {
            if (chatter1.user().equals(user)) return;
            try {
                chatter1.client().removeActiveUser(user);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public List<User> getActiveUsers() throws RemoteException {
        return chatters.values().stream().map(Chatter::user).toList();
    }

    @Override
    public void leaveChat(User user) throws RemoteException {
        unregisterUser(user);
    }

    public static void main(String[] args) throws RemoteException, MalformedURLException {
        startRMIRegistry();
        String hostName = "localhost";
        String serviceName = "GroupChatService";

        if(args.length == 2){
            hostName = args[0];
            serviceName = args[1];
        }

        try {
            ChatServer hello = new ChatServer();
            Naming.rebind("rmi://" + hostName + "/" + serviceName, hello);
            System.out.println("Group Chat RMI Server is running...");
        }
        catch(Exception e) {
            System.out.println("Server had problems starting");
        }
    }
    /**
     * Start the RMI Registry
     */
    public static void startRMIRegistry() {
        try{
            LocateRegistry.createRegistry(1099);
            System.out.println("RMI Server ready");
        }
        catch(RemoteException e) {
            e.printStackTrace();
        }
    }
}
