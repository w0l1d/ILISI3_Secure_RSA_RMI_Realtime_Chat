package org.ilisi.secure_rmi_chat.client;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Objects;
import java.util.UUID;

public class User implements Serializable {
    private String id;
    private String username;

    private String publicKey;


    public User(String username, String publicKey) throws RemoteException {
        this.id = UUID.randomUUID().toString();
        this.username = username;
        this.publicKey = publicKey;
    }

    public String toString() {
        return this.username + "\n---(" + this.id + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, publicKey);
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPublicKey() {
        return publicKey;
    }

}
