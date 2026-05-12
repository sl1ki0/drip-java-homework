package main.java.drip.hw2.serializer;

import main.java.drip.hw2.dynamicArray.DynamicArray;
import main.java.drip.hw2.dynamicArray.DynamicArrayImpl;

import java.util.StringJoiner;

public class CsvArraySerializer implements ArraySerializer {
    @Override
    public String serialize(DynamicArray array) {
        StringJoiner joiner = new StringJoiner(",");
        for (int i = 0; i < array.size(); i++) {
            joiner.add(String.valueOf(array.get(i)));
        }
        return joiner.toString();
    }

    @Override
    public DynamicArray deserialize(String data) {
        if (data == null || data.trim().isEmpty()) {
            return new DynamicArrayImpl();
        }
        String[] parts = data.split(",");
        DynamicArrayImpl array = new DynamicArrayImpl(parts.length);
        for (String part : parts) {
            array.add(Integer.parseInt(part.trim()));
        }
        return  array;
    }
}
