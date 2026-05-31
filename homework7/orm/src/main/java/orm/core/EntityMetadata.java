package orm.core;

import orm.annotation.Column;
import orm.annotation.Id;
import orm.annotation.Table;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class EntityMetadata {

    public record ColumnMeta(Field field, String columnName, boolean nullable) {
    }

    private final Class<?> type;
    private final String tableName;
    private final ColumnMeta id;
    private final List<ColumnMeta> columns;

    private EntityMetadata(Class<?> type, String tableName, ColumnMeta id, List<ColumnMeta> columns) {
        this.type = type;
        this.tableName = tableName;
        this.id = id;
        this.columns = columns;
    }

    public static EntityMetadata of(Class<?> clazz) {
        Table table = clazz.getAnnotation(Table.class);
        if (table == null) {
            throw new OrmException("Class " + clazz.getName() + " is not annotated with @Table");
        }

        String tableName = table.name().isEmpty()
                ? clazz.getSimpleName().toLowerCase()
                : table.name();

        ColumnMeta id = null;
        List<ColumnMeta> columns = new ArrayList<>();

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            Id idAnnotation = field.getAnnotation(Id.class);
            Column columnAnnotation = field.getAnnotation(Column.class);

            if (idAnnotation != null) {
                if (field.getType() != Long.class && field.getType() != long.class) {
                    throw new OrmException("@Id field must be Long or long: " + field.getName());
                }
                String name = (columnAnnotation != null && !columnAnnotation.name().isEmpty())
                        ? columnAnnotation.name()
                        : field.getName();
                id = new ColumnMeta(field, name, false);
            } else if (columnAnnotation != null) {
                String name = columnAnnotation.name().isEmpty()
                        ? field.getName()
                        : columnAnnotation.name();
                columns.add(new ColumnMeta(field, name, columnAnnotation.nullable()));
            }

        }

        if (id == null) {
            throw new OrmException("Class " + clazz.getName() + " has no @Id field");
        }

        return new EntityMetadata(clazz, tableName, id, columns);
    }

    public Class<?> getType() {
        return type;
    }

    public String getTableName() {
        return tableName;
    }

    public ColumnMeta getId() {
        return id;
    }

    public List<ColumnMeta> getColumns() {
        return columns;
    }

    public List<ColumnMeta> getAllColumns() {
        List<ColumnMeta> all = new ArrayList<>();
        all.add(id);
        all.addAll(columns);
        return all;
    }

    public ColumnMeta findByFieldName(String fieldName) {
        for (ColumnMeta column : getAllColumns()) {
            if (column.field().getName().equals(fieldName)) {
                return column;
            }
        }
        return null;
    }
}
