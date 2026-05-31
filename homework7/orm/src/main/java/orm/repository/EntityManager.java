package orm.repository;

import orm.core.EntityMetadata;
import orm.core.EntityMetadata.ColumnMeta;
import orm.core.OrmException;
import orm.core.ValidationException;
import orm.core.Validator;
import orm.core.Violation;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;

public class EntityManager {

    private final Connection connection;
    private final Map<Class<?>, EntityMetadata> metadataCache = new HashMap<>();
    private final Validator validator = new Validator();

    public EntityManager(Connection connection) {
        this.connection = connection;
    }

    public void createTable(Class<?> clazz) {
        EntityMetadata meta = getMetadata(clazz);

        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE IF NOT EXISTS ").append(meta.getTableName()).append(" (\n");
        sql.append("    ").append(meta.getId().columnName()).append(" BIGINT AUTO_INCREMENT PRIMARY KEY");
        for (ColumnMeta column : meta.getColumns()) {
            sql.append(",\n    ")
                    .append(column.columnName())
                    .append(" ")
                    .append(sqlType(column.field().getType()));
            if (!column.nullable()) {
                sql.append(" NOT NULL");
            }
        }
        sql.append("\n)");

        try (Statement statement = connection.createStatement()) {
            statement.execute(sql.toString());
        } catch (SQLException e) {
            throw new OrmException("Cannot create table " + meta.getTableName(), e);
        }
    }

    public Long save(Object entity) {
        EntityMetadata meta = getMetadata(entity.getClass());
        validate(entity);
        checkNotNullColumns(meta, entity);

        String sql = buildInsertSql(meta);
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            int index = 1;
            for (ColumnMeta column : meta.getColumns()) {
                setParameter(ps, index++, getFieldValue(column.field(), entity));
            }
            ps.executeUpdate();

            Long id = readGeneratedKey(ps);
            setFieldValue(meta.getId().field(), entity, id);
            return id;
        } catch (SQLException e) {
            throw new OrmException("Cannot save entity into " + meta.getTableName(), e);
        }
    }

    public <T> Optional<T> findById(Class<T> clazz, Long id) {
        EntityMetadata meta = getMetadata(clazz);
        String sql = buildSelectSql(meta) + " WHERE " + meta.getId().columnName() + " = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            setParameter(ps, 1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(clazz, meta, rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new OrmException("Cannot find by id in " + meta.getTableName(), e);
        }
    }

    public <T> List<T> findAll(Class<T> clazz) {
        EntityMetadata meta = getMetadata(clazz);
        String sql = buildSelectSql(meta);

        List<T> result = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(mapRow(clazz, meta, rs));
            }
        } catch (SQLException e) {
            throw new OrmException("Cannot read all from " + meta.getTableName(), e);
        }
        return result;
    }

    public int update(Object entity) {
        EntityMetadata meta = getMetadata(entity.getClass());
        validate(entity);
        checkNotNullColumns(meta, entity);

        StringJoiner setPart = new StringJoiner(", ");
        for (ColumnMeta column : meta.getColumns()) {
            setPart.add(column.columnName() + " = ?");
        }
        String sql = "UPDATE " + meta.getTableName() + " SET " + setPart
                + " WHERE " + meta.getId().columnName() + " = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int index = 1;
            for (ColumnMeta column : meta.getColumns()) {
                setParameter(ps, index++, getFieldValue(column.field(), entity));
            }
            setParameter(ps, index, getFieldValue(meta.getId().field(), entity));
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new OrmException("Cannot update entity in " + meta.getTableName(), e);
        }
    }

    public int delete(Object entity) {
        EntityMetadata meta = getMetadata(entity.getClass());
        Long id = (Long) getFieldValue(meta.getId().field(), entity);
        return deleteById(entity.getClass(), id);
    }

    public int deleteById(Class<?> clazz, Long id) {
        EntityMetadata meta = getMetadata(clazz);
        String sql = "DELETE FROM " + meta.getTableName() + " WHERE " + meta.getId().columnName() + " = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            setParameter(ps, 1, id);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new OrmException("Cannot delete by id in " + meta.getTableName(), e);
        }
    }

    public void saveAll(List<?> entities) {
        if (entities.isEmpty()) {
            return;
        }

        Class<?> clazz = null;
        for (Object entity : entities) {
            if (entity == null) {
                throw new OrmException("saveAll: list contains a null element");
            }
            if (clazz == null) {
                clazz = entity.getClass();
            } else if (entity.getClass() != clazz) {
                throw new OrmException("saveAll: all elements must be of the same class");
            }
        }

        EntityMetadata meta = getMetadata(clazz);

        for (Object entity : entities) {
            validate(entity);
            checkNotNullColumns(meta, entity);
        }

        String sql = buildInsertSql(meta);
        try {
            connection.setAutoCommit(false);
            try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                for (Object entity : entities) {
                    int index = 1;
                    for (ColumnMeta column : meta.getColumns()) {
                        setParameter(ps, index++, getFieldValue(column.field(), entity));
                    }
                    ps.addBatch();
                }
                ps.executeBatch();
                connection.commit();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    int i = 0;
                    while (keys.next() && i < entities.size()) {
                        setFieldValue(meta.getId().field(), entities.get(i), keys.getLong(1));
                        i++;
                    }
                }
            } catch (SQLException e) {
                connection.rollback();
                throw new OrmException("saveAll failed, transaction rolled back", e);
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new OrmException("saveAll: transaction error", e);
        }
    }

    public <T> List<T> findAllWhere(Class<T> clazz, String fieldName, Object value) {
        EntityMetadata meta = getMetadata(clazz);
        ColumnMeta column = meta.findByFieldName(fieldName);
        if (column == null) {
            throw new OrmException("Unknown field '" + fieldName + "' in " + clazz.getName());
        }
        if (value != null && !box(column.field().getType()).isInstance(value)) {
            throw new OrmException("Value of type " + value.getClass().getSimpleName()
                    + " is not compatible with field '" + fieldName + "'");
        }

        String sql = buildSelectSql(meta) + " WHERE " + column.columnName() + " = ?";
        List<T> result = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            setParameter(ps, 1, value);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(clazz, meta, rs));
                }
            }
        } catch (SQLException e) {
            throw new OrmException("findAllWhere failed on " + meta.getTableName(), e);
        }
        return result;
    }

    public <T> Optional<T> findOneWhere(Class<T> clazz, String fieldName, Object value) {
        List<T> found = findAllWhere(clazz, fieldName, value);
        if (found.size() > 1) {
            throw new OrmException("findOneWhere: more than one row matched " + fieldName + " = " + value);
        }
        return found.isEmpty() ? Optional.empty() : Optional.of(found.get(0));
    }

    public long count(Class<?> clazz) {
        EntityMetadata meta = getMetadata(clazz);
        String sql = "SELECT COUNT(*) FROM " + meta.getTableName();
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getLong(1);
        } catch (SQLException e) {
            throw new OrmException("count failed on " + meta.getTableName(), e);
        }
    }

    public boolean existsById(Class<?> clazz, Long id) {
        EntityMetadata meta = getMetadata(clazz);
        String sql = "SELECT 1 FROM " + meta.getTableName()
                + " WHERE " + meta.getId().columnName() + " = ? LIMIT 1";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            setParameter(ps, 1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new OrmException("existsById failed on " + meta.getTableName(), e);
        }
    }

    private EntityMetadata getMetadata(Class<?> clazz) {

        return metadataCache.computeIfAbsent(clazz, EntityMetadata::of);
    }

    private void validate(Object entity) {
        List<Violation> violations = validator.validate(entity);
        if (!violations.isEmpty()) {
            throw new ValidationException(violations);
        }
    }

    private void checkNotNullColumns(EntityMetadata meta, Object entity) {
        for (ColumnMeta column : meta.getColumns()) {
            if (!column.nullable() && getFieldValue(column.field(), entity) == null) {
                throw new OrmException("Column '" + column.columnName() + "' must not be null");
            }
        }
    }

    private String buildInsertSql(EntityMetadata meta) {
        StringJoiner columns = new StringJoiner(", ");
        StringJoiner placeholders = new StringJoiner(", ");
        for (ColumnMeta column : meta.getColumns()) {
            columns.add(column.columnName());
            placeholders.add("?");
        }
        return "INSERT INTO " + meta.getTableName() + " (" + columns + ") VALUES (" + placeholders + ")";
    }

    private String buildSelectSql(EntityMetadata meta) {
        StringJoiner columns = new StringJoiner(", ");
        for (ColumnMeta column : meta.getAllColumns()) {
            columns.add(column.columnName());
        }
        return "SELECT " + columns + " FROM " + meta.getTableName();
    }

    private <T> T mapRow(Class<T> clazz, EntityMetadata meta, ResultSet rs) {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();
            for (ColumnMeta column : meta.getAllColumns()) {
                Object value = readResultValue(rs, column.columnName(), column.field().getType());
                setFieldValue(column.field(), instance, value);
            }
            return instance;
        } catch (ReflectiveOperationException e) {
            throw new OrmException("Cannot create instance of " + clazz.getName(), e);
        } catch (SQLException e) {
            throw new OrmException("Cannot read row for " + clazz.getName(), e);
        }
    }

    private Object readResultValue(ResultSet rs, String column, Class<?> type) throws SQLException {
        if (type == long.class || type == Long.class) {
            return rs.getLong(column);
        }
        if (type == int.class || type == Integer.class) {
            return rs.getInt(column);
        }
        if (type == double.class || type == Double.class) {
            return rs.getDouble(column);
        }
        if (type == boolean.class || type == Boolean.class) {
            return rs.getBoolean(column);
        }
        if (type == String.class) {
            return rs.getString(column);
        }
        if (type == LocalDate.class) {
            Date date = rs.getDate(column);
            return date == null ? null : date.toLocalDate();
        }
        throw new OrmException("Unsupported type: " + type.getName());
    }

    private void setParameter(PreparedStatement ps, int index, Object value) throws SQLException {
        if (value == null) {
            ps.setObject(index, null);
        } else if (value instanceof LocalDate date) {
            ps.setDate(index, Date.valueOf(date));
        } else {
            ps.setObject(index, value);
        }
    }

    private Long readGeneratedKey(PreparedStatement ps) throws SQLException {
        try (ResultSet keys = ps.getGeneratedKeys()) {
            if (keys.next()) {
                return keys.getLong(1);
            }
            throw new OrmException("No generated id was returned");
        }
    }

    private String sqlType(Class<?> type) {
        if (type == Long.class || type == long.class) {
            return "BIGINT";
        }
        if (type == Integer.class || type == int.class) {
            return "INT";
        }
        if (type == Double.class || type == double.class) {
            return "DOUBLE";
        }
        if (type == Boolean.class || type == boolean.class) {
            return "BOOLEAN";
        }
        if (type == String.class) {
            return "VARCHAR(255)";
        }
        if (type == LocalDate.class) {
            return "DATE";
        }
        throw new OrmException("Unsupported type: " + type.getName());
    }

    private Object getFieldValue(Field field, Object entity) {
        try {
            return field.get(entity);
        } catch (IllegalAccessException e) {
            throw new OrmException("Cannot read field " + field.getName(), e);
        }
    }

    private void setFieldValue(Field field, Object entity, Object value) {
        try {
            field.set(entity, value);
        } catch (IllegalAccessException e) {
            throw new OrmException("Cannot set field " + field.getName(), e);
        }
    }

    private Class<?> box(Class<?> type) {
        if (type == int.class) {
            return Integer.class;
        }
        if (type == long.class) {
            return Long.class;
        }
        if (type == double.class) {
            return Double.class;
        }
        if (type == boolean.class) {
            return Boolean.class;
        }
        return type;
    }
}
