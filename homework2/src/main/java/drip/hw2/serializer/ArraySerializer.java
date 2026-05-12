package main.java.drip.hw2.serializer;

import main.java.drip.hw2.dynamicArray.DynamicArray;

public interface ArraySerializer {
    String serialize(DynamicArray array);
    DynamicArray deserialize(String data);
}
