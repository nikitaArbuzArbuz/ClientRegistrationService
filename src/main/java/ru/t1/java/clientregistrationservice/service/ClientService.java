package ru.t1.java.clientregistrationservice.service;

import ru.t1.java.clientregistrationservice.model.Client;

public interface ClientService {
    Client getAuthenticatedUser();
}
