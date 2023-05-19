package ru.alexander.stosys.controller;

import org.springframework.web.bind.annotation.*;
import ru.alexander.stosys.entity.User;
import ru.alexander.stosys.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    /*
        HTTP GET - http://localhost:8080/users/all
        Получить данные всех пользователей в виде списка

        Вид выходных данных:
            [
                {
                    "mail": "sasha.izmyloff@mail.ru",
                    "username": "Alexander1248",
                    "permission": "admin"
                },
                {
                    "mail": "mail",
                    "username": "Alex",
                    "permission": "user"
                }
            ]
    */
    @GetMapping("/all")
    public List<User> getAll() {
        return service.allUsers();
    }


    /*
        HTTP GET - http://localhost:8080/users/data/{username}
        Получить данные о пользователе

        Вид выходных данных:
            {
                "mail": "mail",
                "username": "Alex",
                "permission": "user"
            }
    */
    @GetMapping("/data/{username}")
    public User getData(@PathVariable("username") String username) {
        return service.loadUserByUsername(username);
    }

    /*
        HTTP GET - http://localhost:8080/users/token/{username}?password={password}
        Получить токен пользователя для текущей сессии

        Вид выходных данных:
            D7g+o3xciw3p2t7v02Fy1syJU2OYUSAYQTAX3y1kxaw=
    */
    @GetMapping("/token/{username}")
    public byte[] getToken(@PathVariable("username") String username,
                           @RequestParam("password") String password) {
        if (service.confirmPassword(username, password)) {
            return service.loadUserTokenByUsername(username);
        }
        return null;
    }
    @GetMapping("/token/{username}/admin")
    public byte[] getToken(@PathVariable("username") String username,
                           @RequestParam("admin") String account,
                           @RequestParam("password") String password) {
        User user = service.loadUserByUsername(account);
        if (user != null && user.getPermission().equals("admin") && service.confirmPassword(account, password)){
            return service.loadUserTokenByUsername(username);
        }
        return null;
    }
    /*
        HTTP POST - http://localhost:8080/users/reg?username={username}&mail={mail}&password={password}
        Зарегистрировать пользователя

        Если успешное выполнение:
            User added!
        Если пользователь с таким именем уже есть:
            User with this username already exists!
        Если пользователь с такой почтой уже есть:
            User with this mail already exists!
    */
    @PostMapping("/reg")
    public String registrate(@RequestParam("username") String username,
                           @RequestParam("mail") String mail,
                           @RequestParam("password") String password) {
        return service.addUser(new User(mail, username, password, "user"));
    }
    /*
        HTTP DELETE - http://localhost:8080/users/delete/{username}?password={password}
        Удалить пользователя

        Если успешное выполнение:
            User deleted!
        Если пароль неверен:
            Wrong password!
        Если такого пользователя нет:
            User not exists!
    */
    @DeleteMapping("/delete/{username}")
    public String delete(@PathVariable("username") String username,
                         @RequestParam("password") String password) {
        if (service.confirmPassword(username, password))
            return service.deleteUser(username);
        return "Wrong password!";
    }

    @DeleteMapping("/delete/{username}/admin")
    public String delete(@PathVariable("username") String username,
                         @RequestParam("admin") String account,
                         @RequestParam("password") String password) {
        User user = service.loadUserByUsername(account);
        if (user != null && user.getPermission().equals("admin") && service.confirmPassword(account, password))
            return service.deleteUser(username);
        return "Wrong password!";
    }
//  keytool -genkeypair -alias StosysServerEncryption -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore stosysHTTPS.p12 -validity 3650
//  select * from users;
//  select * from tokens;
}
