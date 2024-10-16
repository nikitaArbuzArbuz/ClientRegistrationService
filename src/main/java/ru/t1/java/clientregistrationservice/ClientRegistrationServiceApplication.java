package ru.t1.java.clientregistrationservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class ClientRegistrationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClientRegistrationServiceApplication.class, args);
    }
}
