package ru.alexander.stosys.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.alexander.stosys.entity.SessionToken;
import ru.alexander.stosys.entity.User;

import java.nio.ByteBuffer;

public interface TokenRepository extends JpaRepository<SessionToken, Long> {
    SessionToken findBySessionKeyAndUser(String sessionKey, User user);
    SessionToken findByToken(byte[] token);

    boolean existsByUser(User user);
    void deleteAllByUser(User user);

}
