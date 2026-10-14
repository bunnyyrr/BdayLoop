package ru.bdayloop.model;

import jakarta.persistence.*;

@Entity
@Table(name="groups")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name= "created_by")
    private User createdBy;

    protected Group() {}

    public Group(String name, User createdBy){
        this.name=name;
        this.createdBy = createdBy;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void rename(String newName){
        this.name= newName;
    }
}
