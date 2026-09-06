package ru.bdayloop.model;

public class Gift {
    private int id;
    private int userId;
    private String title;

    public Gift(int id, int userId, String title){
        this.id =id;
        this.userId=userId;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }
}
