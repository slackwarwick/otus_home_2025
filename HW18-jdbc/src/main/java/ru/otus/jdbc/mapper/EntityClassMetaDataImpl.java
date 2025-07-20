package ru.otus.jdbc.mapper;

import ru.otus.jdbc.model.Client;
import ru.otus.jdbc.repository.DataTemplateException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EntityClassMetaDataImpl<T> implements EntityClassMetaData<T> {
    private Class<T> clazz;

    public EntityClassMetaDataImpl(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public String getName() {
        return clazz.getSimpleName().toLowerCase();
    }

    @Override
    public Constructor<T> getConstructor() {
        try {
            return clazz.getConstructor();
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    @Override
    public Field getIdField() {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.getAnnotation(Id.class) != null)
                return field;
        }
        return null;
    }

    @Override
    public List<Field> getAllFields() {
        return Arrays.stream(clazz.getDeclaredFields()).toList();
    }

    @Override
    public List<Field> getFieldsWithoutId() {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(f -> f.getAnnotation(Id.class) == null)
                .toList();
    }
}
