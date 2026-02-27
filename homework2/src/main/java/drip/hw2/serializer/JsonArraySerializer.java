package main.java.drip.hw2.serializer;

import main.java.drip.hw2.dynamicArray.DynamicArray;
import main.java.drip.hw2.dynamicArray.DynamicArrayImpl;

import java.util.StringJoiner;

public class JsonArraySerializer implements ArraySerializer {
    @Override
    public String serialize(DynamicArray array) {
        StringJoiner joiner = new StringJoiner(", ", "[", "]");
        for (int i = 0; i < array.size(); i++) {
            joiner.add(String.valueOf(array.get(i)));
        }
        return joiner.toString();
    }

    @Override
    public DynamicArray deserialize(String data) {
        if (data == null || data.trim().equals("[]")) {
            return new DynamicArrayImpl();
        }
        String content = data.substring(data.indexOf('[') + 1, data.lastIndexOf(']')).trim();
        if (content.isEmpty()) return new DynamicArrayImpl();

        String[] parts = content.split(",");

        DynamicArrayImpl array = new DynamicArrayImpl(parts.length);
        for (String part : parts) {
            array.add(Integer.parseInt(part.trim()));
        }
        return array;
    }
}
