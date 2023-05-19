package ru.alexander.stosys.controller;

import org.springframework.web.bind.annotation.*;
import ru.alexander.stosys.entity.SessionToken;
import ru.alexander.stosys.repository.TokenRepository;

import java.io.*;

@RestController
@RequestMapping("/stosys/{token}")
public class UserStorageController {
    private final TokenRepository tokenRepository;
    private final String stoSysRoot;

    public UserStorageController(TokenRepository tokenRepository,
                                 String stoSysRoot) {
        this.tokenRepository = tokenRepository;
        this.stoSysRoot = stoSysRoot;
    }
    /*
        HTTP GET - http://localhost:8080/stosys/hvurkhgu575=65/files
        Получить список файлов пользователя

        Вид выходных данных:
            [
                "data.txt",
                "file.png",
                "module.db"
            ]
    */
    @GetMapping("/files")
    public String[] getStorages(@PathVariable("token")  byte[] token) {
        SessionToken tkn = tokenRepository.findByToken(token);
        if (tkn == null) return null;

        String username = tkn.getUser().getUsername();
        File file = new File(stoSysRoot, username);
        return file.list();
    }

    /*
        HTTP POST - http://localhost:8080/stosys/{token}/files/{file}
        Сохранить файл в личное пространство пользователя
        !Внимание! Данные в бинарном виде передаются в теле запроса

        Если успешное выполнение:
            Data saved!
        Если токен неверен(может быть такого пользователя нет или токен не обновляли в новой сессии):
            Incorrect token!
        Если возникла ошибка в процессе сохранения:
            Database save error!
    */
    @PostMapping("files/{storage}")
    public String saveDatabase(@PathVariable("token")  byte[] token,
                             @PathVariable("storage") String name,
                             @RequestBody byte[] binary) {
        SessionToken tkn = tokenRepository.findByToken(token);
        if (tkn == null) return "Incorrect token!";

        String username = tkn.getUser().getUsername();

        try {
            FileOutputStream writer = new FileOutputStream(stoSysRoot + "/" + username + "/" + name);
            writer.write(binary);
            writer.flush();
            writer.close();
            return "Data saved!";
        } catch (IOException e) {
            return "Database save error!";
        }
    }

    /*
        HTTP GET - http://localhost:8080/stosys/{token}/files/{file}
        Загрузить файл из личного пространства пользователя

        Вид выходных данных:
            rt78795uhjgu4487ujjrhkpoo9390-l;lekjf766whdh r4ht7b 7rusikcvjv (Данные в бинарном виде)
    */
    @GetMapping("files/{storage}")
    public byte[] loadDatabase(@PathVariable("token")  byte[] token,
                             @PathVariable("storage") String name) {
        SessionToken tkn = tokenRepository.findByToken(token);
        if (tkn == null) return null;

        String username = tkn.getUser().getUsername();

        try {
            FileInputStream writer = new FileInputStream(stoSysRoot + "/" + username + "/" + name);
            byte[] bytes = writer.readAllBytes();
            writer.close();
            return bytes;
        } catch (IOException e) {
            return null;
        }
    }

    /*
        HTTP DELETE - http://localhost:8080/stosys/{token}/files/{file}
        Удалить файл из личного пространства пользователя

        Если успешное выполнение:
            Data deleted!
        Если токен неверен(может быть такого пользователя нет или токен не обновляли в новой сессии):
            Incorrect token!
        Если возникла ошибка в процессе удаления:
            Database deleting error!
    */
    @DeleteMapping ("files/{storage}")
    public String deleteDatabase(@PathVariable("token")  byte[] token,
                               @PathVariable("storage") String name) {
        SessionToken tkn = tokenRepository.findByToken(token);
        if (tkn == null) return "Incorrect token!";

        String username = tkn.getUser().getUsername();
        if (new File(stoSysRoot + "/" + username, name).delete())
            return "Data deleted!";
        return "Database deleting error!";
    }
}
