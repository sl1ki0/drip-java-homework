package main.java.drip.hw2.serializer;

import main.java.drip.hw2.dynamicArray.DynamicArray;
import main.java.drip.hw2.dynamicArray.DynamicArrayImpl;

import java.nio.ByteBuffer;
import java.util.Base64;

public class BinaryArraySerializer implements ArraySerializer {
    @Override
    public String serialize(DynamicArray array) {
        ByteBuffer buffer = ByteBuffer.allocate(array.size() * 4);
        for (int i = 0; i < array.size(); i++) {
            buffer.putInt(array.get(i));
        }
        return Base64.getEncoder().encodeToString(buffer.array());
    }

    @Override
    public DynamicArray deserialize(String data) {
        DynamicArrayImpl result = new DynamicArrayImpl();
        if (data == null || data.trim().isEmpty()) {
            return result;
        }

        byte[] bytes = Base64.getDecoder().decode(data);
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        while (buffer.hasRemaining()) {
            result.add(buffer.getInt());
        }
        return result;
    }
}