package ru.bdayloop.model;

import jakarta.persistence.*;

@Entity
@Table(name= "gifts")
public class Gift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch= FetchType.LAZY, optional = false)
    @JoinColumn(name= "user_id", nullable = false)
    private User owner;

    private String title;

    protected Gift() {}

    public Gift(User owner, String title){
        this.owner=owner;
        this.title = title;
    }

    public Integer getId() {
        return id;
    }

    public User getOwner() {
        return owner;
    }

    public String getTitle() {
        return title;
    }
}
