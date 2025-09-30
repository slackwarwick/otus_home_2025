package ru.otus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
    Полезные для демо ссылки

    // Стартовая страница
    http://localhost:8080

    // Страница пользователей
    http://localhost:8080/clients

    // REST сервис
    http://localhost:8080/api/client/3
*/
@SpringBootApplication
public class WebServerWithNoSecurity {
    public static void main(String[] args) {
        SpringApplication.run(WebServerWithNoSecurity.class, args);
    }
}
