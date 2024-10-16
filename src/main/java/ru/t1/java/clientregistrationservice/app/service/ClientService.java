package ru.t1.java.clientregistrationservice.app.service;

import ru.t1.java.clientregistrationservice.app.domain.entity.Client;

public interface ClientService {
    Client getAuthenticatedUser();
}
