package util;

import java.io.IOException;
import java.util.Objects;

import com.fasterxml.jackson.databind.ObjectMapper;

public final class TestDataReader<T> {
    private final ObjectMapper mapper;

    private final Class<T> testDataClass;

    public TestDataReader(Class<T> testDataClass) {
        this.mapper = new ObjectMapper();
        this.testDataClass = testDataClass;
    }

    public T readTestData(String jsonResourcePath) {
        try {
            return mapper.readValue(Objects.requireNonNull(getClass().getResourceAsStream("../%s".formatted(jsonResourcePath))), testDataClass);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read JSON from file: %s".formatted(e));
        }
    }
}
