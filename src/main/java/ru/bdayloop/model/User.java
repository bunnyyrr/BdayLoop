package ru.bdayloop.model;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name ="users")
public class User {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private LocalDate birthday;
    private String username;

    @Column(name="password_hash")
    private String passwordHash;

    public enum Role {USER, ADMIN}

    @Enumerated(EnumType.STRING)
    private Role role;

    protected User(){}

    public User (Integer id, String name, LocalDate birthday,
                 String username, String passwordHash, Role role){
        this.id =id;
        this.name =name;
        this.birthday= birthday;
        this.username =username;
        this.passwordHash =passwordHash;
        this.role =role;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getUsername() {
        return username;
    }

    public Role getRole() {
        return role;
    }
    public LocalDate getBirthday(){
        return birthday;
    }

    public void updateProfile(String name, LocalDate birthday, String username){
        this.name=name;
        this.birthday= birthday;
        this.username= username;
    }
}
