package com.heshamapps.heshe.thedentalstore.Model;

import java.io.Serializable;

public class Users implements Serializable {

   private String username , userId;
    private int roleId; // 1 for student, 2 for admin
    public Users(String username,String userId, int roleId){
        this.username = username;
        this.roleId = roleId;
        this.userId=userId;
    }

    public Users() {
    }
    public String getUsername() {
        return this.username;
    }

    public String getUserId() {
        return this.userId;
    }

    public int getRoleId() {
        return this.roleId;
    }
}
