package ru.otus.jdbc.mapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EntityClassMetaDataImpl<T> implements EntityClassMetaData<T> {
    private final Class<T> clazz;
    private String tableName;
    Constructor<T> constructor;
    private Field idField;
    private List<Field> fieldsWithoutId;

    public EntityClassMetaDataImpl(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public String getName() {
        if (tableName == null) {
            tableName = clazz.getSimpleName().toLowerCase();
        }
        return tableName;
    }

    @Override
    public Constructor<T> getConstructor() throws NoSuchMethodException {
        if (constructor == null) {
            constructor = clazz.getConstructor();
        }
        return constructor;
    }

    @Override
    public Field getIdField() {
        if (idField == null) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.getAnnotation(Id.class) != null)
                    idField = field;
            }
        }
        return idField;
    }

    @Override
    public List<Field> getAllFields() {
        List<Field> result = new ArrayList<>();
        result.add(getIdField());
        result.addAll(getFieldsWithoutId());
        return result;
    }

    @Override
    public List<Field> getFieldsWithoutId() {
        if (fieldsWithoutId == null) {
            fieldsWithoutId = Arrays.stream(clazz.getDeclaredFields())
                    .filter(f -> f.getAnnotation(Id.class) == null)
                    .toList();
        }
        return fieldsWithoutId;
    }
}
