package ru.otus;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.cache.DummyCache;
import ru.otus.cache.HwCache;
import ru.otus.cache.MyCache;
import ru.otus.jdbc.datasource.DriverManagerDataSource;
import ru.otus.jdbc.mapper.*;
import ru.otus.jdbc.model.Client;
import ru.otus.jdbc.model.Manager;
import ru.otus.jdbc.repository.executor.DbExecutorImpl;
import ru.otus.jdbc.service.DbServiceClientImpl;
import ru.otus.jdbc.service.DbServiceManagerImpl;
import ru.otus.jdbc.sessionmanager.TransactionRunnerJdbc;

import javax.sql.DataSource;
import java.util.function.Supplier;

@SuppressWarnings({"java:S125", "java:S1481"})
public class HomeWork {
    private static final String URL = "jdbc:postgresql://localhost:5430/demoDB";
    private static final String USER = "usr";
    private static final String PASSWORD = "pwd";

    private static final Logger log = LoggerFactory.getLogger(HomeWork.class);

    public static void main(String[] args) {
        long t = System.currentTimeMillis();

        runWork(MyCache::new, MyCache::new);
        //runWork(DummyCache::new, DummyCache::new);

        log.info("--------------------");
        log.info("TOTAL TIME: {}", System.currentTimeMillis() - t);

        checkDeletion();
    }

    private static void checkDeletion() {
        log.info("Test deletion after GC is working");
        HwCache<Long, String> cache = new MyCache<>();
        for (int i = 0; i < 100; ++i) {
            cache.put((long) i, String.valueOf(i));
        }
        log.info("Before GC: key=1 value={}", cache.get(1L));
        System.gc();
        log.info("After GC: key=1 value={}", cache.get(1L));
    }

    private static void runWork(Supplier<HwCache<Long, Client>> clientCacheSupplier,
                                Supplier<HwCache<Long, Manager>> managerCacheSupplier) {
        // Общая часть
        var dataSource = new DriverManagerDataSource(URL, USER, PASSWORD);
        flywayMigrations(dataSource);
        var transactionRunner = new TransactionRunnerJdbc(dataSource);
        var dbExecutor = new DbExecutorImpl();

        EntityClassMetaData<Client> entityClassMetaDataClient = new EntityClassMetaDataImpl<>(Client.class);
        EntitySQLMetaData entitySQLMetaDataClient = new EntitySQLMetaDataImpl(entityClassMetaDataClient);
        var dataTemplateClient = new DataTemplateJdbc<Client>(
                dbExecutor, entitySQLMetaDataClient, entityClassMetaDataClient); // реализация DataTemplate, универсальная
        HwCache<Long, Client> clientCache = clientCacheSupplier.get();
        clientCache.addListener((key, value, action) ->
                log.info("Client cache {}: {} -> {}", action, key, value));
        var dbServiceClient = new DbServiceClientImpl(transactionRunner, dataTemplateClient, clientCache);

        for (int i = 0; i < 1000; ++i) {
            var client = dbServiceClient.saveClient(new Client("dbService" + i));
            var clientSelected = dbServiceClient
                    .getClient(client.getId())
                    .orElseThrow(() -> new RuntimeException("Client not found, id:" + client.getId()));
            log.info("client:{}", clientSelected);
        }
        // Сделайте тоже самое с классом Manager (для него надо сделать свою таблицу)

        EntityClassMetaData<Manager> entityClassMetaDataManager = new EntityClassMetaDataImpl<>(Manager.class);
        EntitySQLMetaData entitySQLMetaDataManager = new EntitySQLMetaDataImpl(entityClassMetaDataManager);
        var dataTemplateManager = new DataTemplateJdbc<Manager>(dbExecutor, entitySQLMetaDataManager, entityClassMetaDataManager);

        HwCache<Long, Manager> managerCache = managerCacheSupplier.get();
        managerCache.addListener((key, value, action) ->
                log.info("Manager cache {}: {} -> {}", action, key, value));
        var dbServiceManager = new DbServiceManagerImpl(transactionRunner, dataTemplateManager, managerCache);

        for (int i = 0; i < 1000; ++i) {
            var manager = dbServiceManager.saveManager(new Manager("Manager" + i));
            var managerSelected = dbServiceManager
                    .getManager(manager.getNo())
                    .orElseThrow(() -> new RuntimeException("Manager not found, id:" + manager.getNo()));
            log.info("managerSelected:{}", managerSelected);
        }
    }

    private static void flywayMigrations(DataSource dataSource) {
        log.info("db migration started...");
        var flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:/db/migration")
                .load();
        flyway.migrate();
        log.info("db migration finished.");
        log.info("***");
    }
}
