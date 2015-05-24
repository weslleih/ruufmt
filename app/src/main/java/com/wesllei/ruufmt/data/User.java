package com.wesllei.ruufmt.data;

/**
 * Created by wesllei on 20/05/15.
 */
public class User {
    int id;
    String nick;

    public User(int id, String nick) {
        this.id = id;
        this.nick = nick;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }
}
