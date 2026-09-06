package ru.bdayloop.model;
import java.time.LocalDate;

public class User {
    private int id;
    private String name;
    private LocalDate birthday;
    private String username;
    private String passwordHash;
    public enum Role {USER, ADMIN}
    private Role role;

    public User (int id, String name, LocalDate birthday,
                 String username, String passwordHash, Role role){
        this.id =id;
        this.name =name;
        this.birthday= birthday;
        this.username =username;
        this.passwordHash =passwordHash;
        this.role =role;
    }

    public int getId() {
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
}
