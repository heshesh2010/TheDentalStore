package com.heshamapps.heshe.thedentalstore.Model;

import java.io.Serializable;

public class Users implements Serializable {

   private String username , userId , name , phone , address;
    private int roleId; // 1 for student, 2 for admin

    public String getName() {
        return this.name;
    }

    public String getPhone() {
        return this.phone;
    }

    public String getAddress() {
        return this.address;
    }

    public Users(String username, String userId, int roleId, String name, String phone, String address){
        this.username = username;
        this.roleId = roleId;
        this.userId=userId;
        this.address=address;
        this.name=name;
        this.phone=phone;
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
