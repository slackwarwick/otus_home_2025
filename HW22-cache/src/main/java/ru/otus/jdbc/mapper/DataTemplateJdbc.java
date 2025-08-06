package ru.otus.jdbc.mapper;

import ru.otus.jdbc.repository.DataTemplate;
import ru.otus.jdbc.repository.DataTemplateException;
import ru.otus.jdbc.repository.executor.DbExecutor;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Сохратяет объект в базу, читает объект из базы */
@SuppressWarnings("java:S1068")
public class DataTemplateJdbc<T> implements DataTemplate<T> {

    private final DbExecutor dbExecutor;
    private final EntitySQLMetaData entitySQLMetaData;
    private final EntityClassMetaData<T> entityClassMetaData;

    public DataTemplateJdbc(DbExecutor dbExecutor, EntitySQLMetaData entitySQLMetaData, EntityClassMetaData<T> entityClassMetaData) {
        this.dbExecutor = dbExecutor;
        this.entitySQLMetaData = entitySQLMetaData;
        this.entityClassMetaData = entityClassMetaData;
    }

    @Override
    public Optional<T> findById(Connection connection, long id) {
        return dbExecutor.executeSelect(connection, entitySQLMetaData.getSelectByIdSql(), List.of(id), rs -> {
            try {
                T object = null;
                if (rs.next()) {
                    object = entityClassMetaData.getConstructor().newInstance();
                    for (Field field : object.getClass().getDeclaredFields()) {
                        field.setAccessible(true);
                        field.set(object, rs.getObject(field.getName()));
                    }
                }
                return object;
            } catch (SQLException | IllegalAccessException | InvocationTargetException | InstantiationException |
                     NoSuchMethodException e) {
                throw new DataTemplateException(e);
            }
        });
    }

    @Override
    public List<T> findAll(Connection connection) {
        return dbExecutor.executeSelect(connection, entitySQLMetaData.getSelectAllSql(), List.of(), rs -> {
            try {
                List<T> objects = new ArrayList<>();
                while (rs.next()) {
                    T object = entityClassMetaData.getConstructor().newInstance();
                    for (Field field : object.getClass().getDeclaredFields()) {
                        field.setAccessible(true);
                        field.set(object, rs.getObject(field.getName()));
                    }
                    objects.add(object);
                }
                return objects;
            } catch (SQLException | IllegalAccessException | InvocationTargetException | InstantiationException |
                     NoSuchMethodException e) {
                throw new DataTemplateException(e);
            }
        }).get();
    }

    @Override
    public long insert(Connection connection, T object) {
        List<Object> parameters = new ArrayList<>();
        try {
            for (Field field : entityClassMetaData.getFieldsWithoutId()) {
                field.setAccessible(true);
                parameters.add(field.get(object));
            }
        } catch (IllegalAccessException e) {
            throw new DataTemplateException(e);
        }
        return dbExecutor.executeStatement(connection, entitySQLMetaData.getInsertSql(), parameters);
    }

    @Override
    public void update(Connection connection, T object) {
        List<Object> parameters = new ArrayList<>();
        try {
            for (Field field : entityClassMetaData.getFieldsWithoutId()) {
                field.setAccessible(true);
                parameters.add(field.get(object));
            }
            parameters.add(entityClassMetaData.getIdField().get(object));
        } catch (IllegalAccessException e) {
            throw new DataTemplateException(e);
        }
        dbExecutor.executeStatement(connection, entitySQLMetaData.getUpdateSql(), parameters);
    }
}
