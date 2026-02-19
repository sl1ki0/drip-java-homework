package main.java.drip.hw2.serializer;

import main.java.drip.hw2.dynamicArray.DynamicArray;
import main.java.drip.hw2.dynamicArray.DynamicArrayImpl;

public class XmlArraySerializer implements ArraySerializer {
    @Override
    public String serialize(DynamicArray array) {
        StringBuilder builder = new StringBuilder("<array>\n");
        for (int i = 0; i < array.size(); i++) {
            builder
                    .append("<item>")
                    .append(array.get(i))
                    .append("</item>\n");
        }
        builder.append("</array>");
        return builder.toString();
    }

    @Override
    public DynamicArray deserialize(String data) {
        DynamicArrayImpl result = new DynamicArrayImpl();
        if (data == null || data.trim().isEmpty()) {
            return result;
        }

        int openTagsCount = data.split("<item>", -1).length - 1;
        int closeTagsCount = data.split("</item>", -1).length - 1;

        if (openTagsCount != closeTagsCount) {
            throw new IllegalArgumentException("Невалидный XML");
        }

        int currentIndex = 0;
        for (int i = 0; i < openTagsCount; i++) {
            int startIndex = data.indexOf("<item>", currentIndex) + 6;
            int endIndex = data.indexOf("</item>", startIndex);

            String valueStr = data.substring(startIndex, endIndex).trim();
            result.add(Integer.parseInt(valueStr));

            currentIndex = endIndex + 7;
        }

        return result;
    }
}