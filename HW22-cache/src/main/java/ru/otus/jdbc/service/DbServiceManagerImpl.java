package ru.otus.jdbc.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.cache.HwCache;
import ru.otus.jdbc.model.Client;
import ru.otus.jdbc.model.Manager;
import ru.otus.jdbc.repository.DataTemplate;
import ru.otus.jdbc.sessionmanager.TransactionRunner;

import java.util.List;
import java.util.Optional;

public class DbServiceManagerImpl implements DBServiceManager {
    private static final Logger log = LoggerFactory.getLogger(DbServiceManagerImpl.class);

    private final DataTemplate<Manager> managerDataTemplate;
    private final TransactionRunner transactionRunner;
    private final HwCache<Long, Manager> cache;

    public DbServiceManagerImpl(TransactionRunner transactionRunner, DataTemplate<Manager> managerDataTemplate, HwCache<Long, Manager> cache) {
        this.transactionRunner = transactionRunner;
        this.managerDataTemplate = managerDataTemplate;
        this.cache = cache;
    }

    @Override
    public Manager saveManager(Manager manager) {
        return transactionRunner.doInTransaction(connection -> {
            if (manager.getNo() == null) {
                var managerNo = managerDataTemplate.insert(connection, manager);
                var createdManager = new Manager(managerNo, manager.getLabel(), manager.getParam1());
                log.info("created manager: {}", createdManager);
                addToCache(createdManager);
                return createdManager;
            }
            managerDataTemplate.update(connection, manager);
            log.info("updated manager: {}", manager);
            addToCache(manager);
            return manager;
        });
    }

    @Override
    public Optional<Manager> getManager(long no) {
        Manager manager = cache.get(no);
        if (manager != null) {
            return Optional.of(manager);
        }
        return transactionRunner.doInTransaction(connection -> {
            var managerOptional = managerDataTemplate.findById(connection, no);
            log.info("manager: {}", managerOptional);
            managerOptional.ifPresent(this::addToCache);
            return managerOptional;
        });
    }

    @Override
    public List<Manager> findAll() {
        return transactionRunner.doInTransaction(connection -> {
            var managerList = managerDataTemplate.findAll(connection);
            log.info("managerList:{}", managerList);
            for (Manager manager : managerList) {
                addToCache(manager);
            }
            return managerList;
        });
    }

    private void addToCache(Manager manager) {
        if (manager.getNo() == null) {
            throw new IllegalArgumentException("Manager NO is null!");
        }
        cache.put(manager.getNo(), manager);
    }
}
