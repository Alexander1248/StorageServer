package ru.alexander.stosys.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import ru.alexander.stosys.entity.SessionToken;
import ru.alexander.stosys.entity.User;
import ru.alexander.stosys.repository.TokenRepository;
import ru.alexander.stosys.repository.UserRepository;

import java.io.File;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final BCryptPasswordEncoder encoder;
    private final MessageDigest hash;
    private final String stoSysRoot;

    public UserService(UserRepository userRepository,
                       TokenRepository tokenRepository,
                       BCryptPasswordEncoder encoder,
                       MessageDigest hash,
                       String stoSysRoot) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.encoder = encoder;
        this.hash = hash;
        this.stoSysRoot = stoSysRoot;
    }


    @Transactional
    public User loadUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    @Transactional
    public byte[] loadUserTokenByUsername(String username) {
        User user = userRepository.findUserByUsername(username);
        if (user == null) return null;
        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();

        SessionToken token = tokenRepository.findBySessionKeyAndUser(sessionId, user);
        if (token != null) return token.getToken();

        tokenRepository.deleteAllByUser(user);

        byte[] digest = hash.digest((sessionId + user.getId()).getBytes());
        digest = Base64.getUrlEncoder().encode(digest);
        tokenRepository.save(new SessionToken(user, sessionId, digest));

        return digest;
    }

    @Transactional
    public User findUserById(Long userId) {
        Optional<User> userFromDb = userRepository.findById(userId);
        return userFromDb.orElse(new User());
    }

    @Transactional
    public List<User> allUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public String addUser(User user) {
        if (userRepository.existsUserByUsername(user.getUsername()))
            return "User with this username already exists!";
        if (userRepository.existsUserByMail(user.getMail()))
            return "User with this mail already exists!";


        //User saving
        user.setPassword(encoder.encode(user.getPassword()));
        userRepository.save(user);

        //Stosys path adding
        new File(stoSysRoot, user.getUsername()).mkdir();
        return "User added!";
    }
    @Transactional
    public String deleteUser(String username) {
        User user = userRepository.findUserByUsername(username);
        if (user == null) return "User not exists!";

        //Directory deleting
        File file = new File(stoSysRoot, user.getUsername());
        File[] files = file.listFiles();
        if (files != null)
            for (int i = 0; i < files.length; i++)
                files[i].delete();
        file.delete();

        //Database user data cleaning
        tokenRepository.deleteAllByUser(user);
        userRepository.deleteById(user.getId());
        return "User deleted!";
    }

    @Transactional
    public boolean confirmPassword(String username, String password) {
        User user = userRepository.findUserByUsername(username);
        if (user == null) return true;

        return encoder.matches(password, user.getPassword());
    }

}
