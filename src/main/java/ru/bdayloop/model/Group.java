package ru.bdayloop.model;

public class Group {
    private int id;
    private String name;
    private Integer createdBy;

    public Group(int id, String name, Integer createdBy){
        this.id=id;
        this.name=name;
        this.createdBy = createdBy;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }
}
