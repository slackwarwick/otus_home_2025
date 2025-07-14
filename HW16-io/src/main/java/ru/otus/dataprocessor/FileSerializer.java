package ru.otus.dataprocessor;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class FileSerializer implements Serializer {
    private static final Logger logger = LoggerFactory.getLogger(FileSerializer.class);
    private final String fileName;

    public FileSerializer(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void serialize(Map<String, Double> data) {
        // формирует результирующий json и сохраняет его в файл
        var builder = Json.createObjectBuilder();
        for (Map.Entry<String, Double> entry : data.entrySet()) {
            builder.add(entry.getKey(), entry.getValue());
        }
        JsonObject object = builder.build();
        try (JsonWriter jsonWriter = Json.createWriter(new FileWriter(fileName))) {
            jsonWriter.write(object);
        } catch (IOException e) {
            logger.error("Cannot serialize", e);
            throw new RuntimeException(e);
        }
    }
}
