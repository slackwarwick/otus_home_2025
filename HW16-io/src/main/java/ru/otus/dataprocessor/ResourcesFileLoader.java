package ru.otus.dataprocessor;

import java.util.ArrayList;
import java.util.List;

import jakarta.json.*;
import ru.otus.model.Measurement;

public class ResourcesFileLoader implements Loader {
    private final String fileName;

    public ResourcesFileLoader(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public List<Measurement> load() {
        // читает файл, парсит и возвращает результат
        ArrayList<Measurement> measurements = new ArrayList<>();
        try (var jsonReader = Json.createReader(ResourcesFileLoader.class.getClassLoader().getResourceAsStream(fileName))) {
            JsonStructure json = jsonReader.read();
            if (json.getValueType() == JsonValue.ValueType.ARRAY) {
                fillFromArray(json, measurements);
            }
        }
        return measurements;
    }

    private void fillFromArray(JsonStructure json, ArrayList<Measurement> measurements) {
        for (JsonValue jsonValue : json.asJsonArray()) {
            if (jsonValue.getValueType() == JsonValue.ValueType.OBJECT) {
                measurements.add(createMeasurement(jsonValue.asJsonObject()));
            }
        }
    }

    private Measurement createMeasurement(JsonObject jsonObject) {
        JsonString name = jsonObject.getJsonString("name");
        JsonNumber value = jsonObject.getJsonNumber("value");
        return new Measurement(name.getString(), value.bigDecimalValue().doubleValue());
    }
}
