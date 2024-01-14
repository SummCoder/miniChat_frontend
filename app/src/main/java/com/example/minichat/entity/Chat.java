package com.example.minichat.entity;

/**
 * @author SummCoder
 * @date 2024/1/14 11:50
 */
public class Chat {
    public int id;
    public String name;
    public String desc;
    public int avatar;
    public int whetherPublic;
    public Chat(int id, String name, String desc, int avatar, int whetherPublic){
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.avatar = avatar;
        this.whetherPublic = whetherPublic;
    }

    public Chat() { }
}
