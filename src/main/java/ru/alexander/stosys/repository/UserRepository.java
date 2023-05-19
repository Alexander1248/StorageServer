package ru.alexander.stosys.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.alexander.stosys.entity.User;


public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsUserByUsername(String username);
    boolean existsUserByMail(String mail);

    User findUserByUsername(String username);

    void deleteUserByUsername(String username);
}
