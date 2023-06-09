package io.github.moremcmeta.animationplugin;

import io.github.moremcmeta.moremcmeta.api.client.metadata.MetadataView;
import io.github.moremcmeta.moremcmeta.api.client.metadata.NegativeKeyIndexException;

import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

/**
 * Mock implementation of {@link MetadataView} that contains the given data.
 * @author soir20
 */
public class MockMetadataView implements MetadataView {
    private final Map<String, Object> SECTION_TO_VALUE;

    public MockMetadataView(Map<String, Object> sectionToValue) {
        SECTION_TO_VALUE = sectionToValue;
    }

    @Override
    public int size() {
        return SECTION_TO_VALUE.size();
    }

    @Override
    public Iterable<String> keys() {
        return SECTION_TO_VALUE.keySet();
    }

    @Override
    public boolean hasKey(String key) {
        return SECTION_TO_VALUE.containsKey(key);
    }

    @Override
    public boolean hasKey(int index) {
        checkNegativeIndex(index);
        return SECTION_TO_VALUE.size() > index;
    }

    @Override
    public Optional<String> stringValue(String key) {
        return getValueIfPresent(key, String.class);
    }

    @Override
    public Optional<String> stringValue(int index) {
        checkNegativeIndex(index);
        return getValueIfPresent(index, String.class);
    }

    @Override
    public Optional<Integer> integerValue(String key) {
        return getValueIfPresent(key, Integer.class);
    }

    @Override
    public Optional<Integer> integerValue(int index) {
        checkNegativeIndex(index);
        return getValueIfPresent(index, Integer.class);
    }

    @Override
    public Optional<Long> longValue(String key) {
        return getValueIfPresent(key, Long.class);
    }

    @Override
    public Optional<Long> longValue(int index) {
        checkNegativeIndex(index);
        return getValueIfPresent(index, Long.class);
    }

    @Override
    public Optional<Float> floatValue(String key) {
        return getValueIfPresent(key, Float.class);
    }

    @Override
    public Optional<Float> floatValue(int index) {
        checkNegativeIndex(index);
        return getValueIfPresent(index, Float.class);
    }

    @Override
    public Optional<Double> doubleValue(String key) {
        return getValueIfPresent(key, Double.class);
    }

    @Override
    public Optional<Double> doubleValue(int index) {
        checkNegativeIndex(index);
        return getValueIfPresent(index, Double.class);
    }

    @Override
    public Optional<Boolean> booleanValue(String key) {
        return getValueIfPresent(key, Boolean.class);
    }

    @Override
    public Optional<Boolean> booleanValue(int index) {
        checkNegativeIndex(index);
        return getValueIfPresent(index, Boolean.class);
    }

    @Override
    public Optional<InputStream> byteStreamValue(String key) {
        return getValueIfPresent(key, InputStream.class);
    }

    @Override
    public Optional<InputStream> byteStreamValue(int index) {
        checkNegativeIndex(index);
        return getValueIfPresent(index, InputStream.class);
    }

    @Override
    public Optional<MetadataView> subView(String key) {
        return getValueIfPresent(key, MetadataView.class);
    }

    @Override
    public Optional<MetadataView> subView(int index) {
        checkNegativeIndex(index);
        return getValueIfPresent(index, MetadataView.class);
    }

    private <T> Optional<T> getValueIfPresent(String key, Class<T> expectedClass) {
        if (!SECTION_TO_VALUE.containsKey(key)) {
            return Optional.empty();
        }

        Object value = SECTION_TO_VALUE.get(key);
        return expectedClass.isInstance(value) ? Optional.of(expectedClass.cast(value)) : Optional.empty();
    }

    private <T> Optional<T> getValueIfPresent(int index, Class<T> expectedClass) {
        if (index >= SECTION_TO_VALUE.size()) {
            return Optional.empty();
        }

        Object value = SECTION_TO_VALUE.values().toArray()[index];
        return expectedClass.isInstance(value) ? Optional.of(expectedClass.cast(value)) : Optional.empty();
    }

    private void checkNegativeIndex(int index) {
        if (index < 0) {
            throw new NegativeKeyIndexException(index);
        }
    }
}
