package com.example.minichat.entity;

/**
 * @author SummCoder
 * @date 2024/1/13 16:12
 */
public class LoginInfo {
    public int id;
    public String username;
    public String password;
    public boolean remember = false;

    public LoginInfo(){}

    public LoginInfo(String username, String password, boolean remember) {
        this.username = username;
        this.password = password;
        this.remember = remember;
    }

    @Override
    public String toString() {
        return "LoginInfo{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", remember=" + remember +
                '}';
    }
}
