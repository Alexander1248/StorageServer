package ru.alexander.stosys.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;



@Entity
@Table(name = "users")
@NoArgsConstructor
public class User {
    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(name = "mail", unique = true, nullable = false)
    private String mail;

    @Column(name = "username", unique = true, nullable = false)
    private String username;


    @JsonIgnore
    @Column(name = "pass", nullable = false)
    private String password;


    @Column(name = "permission", nullable = false)
    private String permission;

    public User(String mail, String username, String password, String permission) {
        this.mail = mail;
        this.username = username;
        this.password = password;
        this.permission = permission;
    }

    public long getId() {
        return id;
    }

    public String getMail() {
        return mail;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPermission() {
        return permission;
    }
}
