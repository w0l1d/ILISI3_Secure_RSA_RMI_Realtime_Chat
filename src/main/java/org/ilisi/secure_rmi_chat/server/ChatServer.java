package org.ilisi.secure_rmi_chat.server;

import org.ilisi.secure_rmi_chat.client.IChatClient;
import org.ilisi.secure_rmi_chat.client.User;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class ChatServer extends UnicastRemoteObject implements IChatServer {

    // map of chatters (user id, chatter)
    Map<String, Chatter> chatters;

    protected ChatServer() throws RemoteException {
        System.out.println("Server started");
        chatters = new Hashtable<>();
    }

    public static void main(String[] args) throws RemoteException, MalformedURLException {
        // start rmi registry
        startRMIRegistry();
        // host name and service name for rmi registry binding (default localhost, GroupChatService)
        String hostName = "localhost";
        String serviceName = "GroupChatService";

        // get host name and service name from args if provided
        if (args.length == 2) {
            hostName = args[0];
            serviceName = args[1];
        }

        try {
            // create server instance
            ChatServer hello = new ChatServer();
            // bind server to rmi registry
            Naming.rebind("rmi://" + hostName + "/" + serviceName, hello);
            System.out.println("Group Chat RMI Server is running...");
        } catch (Exception e) {
            System.out.println("Server had problems starting");
        }
    }

    /**
     * Start the RMI Registry
     */
    public static void startRMIRegistry() {
        try {
            LocateRegistry.createRegistry(1099);
            System.out.println("RMI Server ready");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendMessage(String message, String receiverUserId, String senderUserId) throws RemoteException {
        System.out.println("Message received : " + message);
        // send message to receiver
        chatters.get(receiverUserId).client().receiveMessage(message, chatters.get(senderUserId).user());
    }

    @Override
    public void registerUser(User user) throws RemoteException {
        System.out.println(String.format("User %s registered with username %s", user.getId(), user.getUsername()));
        try {
            // get new Chatter instance for the user and register it
            IChatClient client = (IChatClient) Naming.lookup("rmi://localhost/" + user.getId());
            Chatter chatter = new Chatter(user, client);
            // update active users list
            client.updateActiveUsersList(chatters.values().stream().map(Chatter::user).toList());
            List<User> usersToRemove = new Vector<>();
            // notify other users
            chatters.values().forEach(chatter1 -> {
                try {
                    // notify other users
                    chatter1.client().addActiveUser(user);
                } catch (RemoteException e) {
                    // add user to remove list
                    usersToRemove.add(chatter1.user());
                    System.err.println("User " + chatter1.user().getUsername() + " is not available anymore");
                }
            });
            // add user to chatters
            chatters.put(user.getId(), chatter);
            // remove users that are not available anymore
            usersToRemove.forEach(user1 -> {
                try {
                    // unregister user
                    unregisterUser(user1);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            });

        } catch (NotBoundException | MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void unregisterUser(User user) throws RemoteException {
        System.out.printf("User %s unregistered with username %s%n", user.getId(), user.getUsername());
        // remove user from chatters
        chatters.remove(user.getId());
        // notify other users
        chatters.values().forEach(chatter1 -> {
            // don't notify the user himself
            if (chatter1.user().equals(user)) return;
            try {
                // notify other users
                chatter1.client().removeActiveUser(user);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void leaveChat(User user) throws RemoteException {
        unregisterUser(user);
    }
}
