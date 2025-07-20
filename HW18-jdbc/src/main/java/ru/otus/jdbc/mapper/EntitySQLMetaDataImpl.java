package ru.otus.jdbc.mapper;


import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

public class EntitySQLMetaDataImpl implements EntitySQLMetaData {

    private final EntityClassMetaData<?> metaData;

    public EntitySQLMetaDataImpl(EntityClassMetaData<?> metaData) {
        this.metaData = metaData;
    }

    @Override
    public String getSelectAllSql() {
        return getSelectFrom();
    }

    @Override
    public String getSelectByIdSql() {
        Field idField = metaData.getIdField();
        return String.format("%s where %s = ?", getSelectFrom(), idField.getName());
    }

    @Override
    public String getInsertSql() {
        List<Field> fieldsWithoutId = metaData.getFieldsWithoutId();
        String fieldsStr = fieldsWithoutId.stream().map(Field::getName).collect(Collectors.joining(", "));
        String parametersStr = fieldsWithoutId.stream().map(f -> "?").collect(Collectors.joining(", "));
        return String.format("insert into %s (%s) values (%s)", metaData.getName(), fieldsStr, parametersStr);
    }

    @Override
    public String getUpdateSql() {
        List<Field> fieldsWithoutId = metaData.getFieldsWithoutId();
        String fieldsStr = fieldsWithoutId.stream().map(f -> f.getName() + " = ?").collect(Collectors.joining(", "));
        String conditionStr = metaData.getIdField().getName() + " = ?";
        return String.format("update %s set %s where %s", metaData.getName(), fieldsStr, conditionStr);
    }

    private String getSelectFrom() {
        String fieldsStr = metaData.getAllFields().stream().map(Field::getName).collect(Collectors.joining(", "));
        return String.format("select %s from %s", fieldsStr, metaData.getName());
    }
}
