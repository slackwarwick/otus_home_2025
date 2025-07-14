package ru.otus.dataprocessor;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.model.Measurement;

public class ResourcesFileLoader implements Loader {
    private static final Logger logger = LoggerFactory.getLogger(ResourcesFileLoader.class);
    private final String fileName;

    public ResourcesFileLoader(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public List<Measurement> load() {
        // читает файл, парсит и возвращает результат
        var mapper = JsonMapper.builder().build();
        CollectionType javaType = mapper.getTypeFactory().constructCollectionType(List.class, Measurement.class);
        try (InputStream resource = ResourcesFileLoader.class.getClassLoader().getResourceAsStream(fileName)) {
            return mapper.readValue(resource, javaType);
        } catch (IOException e) {
            logger.error("Cannot load", e);
            throw new RuntimeException(e);
        }
    }
}
