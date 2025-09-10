package ru.otus.services;

import org.eclipse.jetty.security.AbstractLoginService;
import org.eclipse.jetty.security.RolePrincipal;
import org.eclipse.jetty.security.UserPrincipal;
import org.eclipse.jetty.util.security.Password;
import ru.otus.crm.model.Client;
import ru.otus.crm.service.DBServiceClient;

import java.util.List;
import java.util.Optional;

public class InMemoryLoginServiceImpl extends AbstractLoginService {

    private final DBServiceClient serviceClient;

    public InMemoryLoginServiceImpl(DBServiceClient serviceClient) {
        this.serviceClient = serviceClient;
    }

    @Override
    protected List<RolePrincipal> loadRoleInfo(UserPrincipal userPrincipal) {
        return List.of(new RolePrincipal("client"));
    }

    @Override
    protected UserPrincipal loadUserInfo(String login) {
        Optional<Client> dbClient = serviceClient.getClientByLogin(login);
        return dbClient.map(u -> new UserPrincipal(u.getLogin(), new Password(u.getPassword())))
                .orElse(null);
    }
}
