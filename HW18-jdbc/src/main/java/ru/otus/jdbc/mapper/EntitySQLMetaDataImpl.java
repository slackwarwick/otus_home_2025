package ru.otus.jdbc.mapper;


import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

public class EntitySQLMetaDataImpl implements EntitySQLMetaData {

    private final EntityClassMetaData<?> metaData;
    private String selectAllSql;
    private String selectByIdSql;
    private String insertSql;
    private String updateSql;

    public EntitySQLMetaDataImpl(EntityClassMetaData<?> metaData) {
        this.metaData = metaData;
    }

    @Override
    public String getSelectAllSql() {
        return getSelectFrom();
    }

    @Override
    public String getSelectByIdSql() {
        if (selectByIdSql == null) {
            Field idField = metaData.getIdField();
            selectByIdSql = String.format("%s where %s = ?", getSelectFrom(), idField.getName());
        }
        return selectByIdSql;
    }

    @Override
    public String getInsertSql() {
        if (insertSql == null) {
            List<Field> fieldsWithoutId = metaData.getFieldsWithoutId();
            String fieldsStr = fieldsWithoutId.stream().map(Field::getName).collect(Collectors.joining(", "));
            String parametersStr = fieldsWithoutId.stream().map(f -> "?").collect(Collectors.joining(", "));
            insertSql = String.format("insert into %s (%s) values (%s)", metaData.getName(), fieldsStr, parametersStr);
        }
        return insertSql;
    }

    @Override
    public String getUpdateSql() {
        if (updateSql == null) {
            List<Field> fieldsWithoutId = metaData.getFieldsWithoutId();
            String fieldsStr = fieldsWithoutId.stream().map(f -> f.getName() + " = ?").collect(Collectors.joining(", "));
            String conditionStr = metaData.getIdField().getName() + " = ?";
            updateSql = String.format("update %s set %s where %s", metaData.getName(), fieldsStr, conditionStr);
        }
        return updateSql;
    }

    private String getSelectFrom() {
        if (selectAllSql == null) {
            String fieldsStr = metaData.getAllFields().stream().map(Field::getName).collect(Collectors.joining(", "));
            selectAllSql = String.format("select %s from %s", fieldsStr, metaData.getName());
        }
        return selectAllSql;
    }
}
