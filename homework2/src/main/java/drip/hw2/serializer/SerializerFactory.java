package main.java.drip.hw2.serializer;

public class SerializerFactory {
    public static ArraySerializer forFormat(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            throw new IllegalArgumentException("Невозможно определить формат файла: " + fileName);
        }
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();

        return switch (extension) {
            case "json" -> new JsonArraySerializer();
            case "xml" -> new XmlArraySerializer();
            case "csv" -> new CsvArraySerializer();
            case "bin", "dat" -> new BinaryArraySerializer();
            default -> throw new IllegalArgumentException("Неподдерживаемый формат файла: " + extension);
        };
    }
}
