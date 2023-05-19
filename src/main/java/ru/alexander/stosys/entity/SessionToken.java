package ru.alexander.stosys.entity;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tokens")
@NoArgsConstructor
public class SessionToken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @OneToOne
    private User user;

    @Column(name = "session", nullable = false)
    private String sessionKey;

    @Column(name = "token", nullable = false)
    private byte[] token;

    public SessionToken(User user, String sessionKey, byte[] token) {
        this.user = user;
        this.sessionKey = sessionKey;
        this.token = token;
    }

    public long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public byte[] getToken() {
        return token;
    }
}
