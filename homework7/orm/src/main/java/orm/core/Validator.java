package orm.core;

import orm.annotation.validation.Constraint;
import orm.core.validation.ConstraintValidator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Validator {

    public List<Violation> validate(Object entity) {
        List<Violation> violations = new ArrayList<>();
        for (Field field : entity.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            for (Annotation annotation : field.getAnnotations()) {
                Constraint constraint = annotation.annotationType().getAnnotation(Constraint.class);
                if (constraint == null) {
                    continue;
                }
                checkOneConstraint(entity, field, annotation, constraint, violations);
            }
        }
        return violations;
    }

    private void checkOneConstraint(Object entity,
                                    Field field,
                                    Annotation annotation,
                                    Constraint constraint,
                                    List<Violation> violations) {
        ConstraintValidator<Annotation, Object> validator = createValidator(constraint);

        Class<?> supportedType = getValidatedType(validator.getClass());
        Class<?> fieldType = box(field.getType());
        if (!supportedType.isAssignableFrom(fieldType)) {
            throw new OrmException("Annotation @" + annotation.annotationType().getSimpleName()
                    + " cannot be applied to field '" + field.getName()
                    + "' of type " + field.getType().getSimpleName());
        }

        Object value = readValue(entity, field);
        if (!validator.isValid(value, annotation)) {
            violations.add(new Violation(field.getName(), value, readMessage(annotation),
                    annotation.annotationType()));
        }
    }

    @SuppressWarnings("unchecked")
    private ConstraintValidator<Annotation, Object> createValidator(Constraint constraint) {
        try {
            return (ConstraintValidator<Annotation, Object>) constraint.validatedBy()
                    .getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new OrmException("Cannot create validator " + constraint.validatedBy().getName(), e);
        }
    }

    private Object readValue(Object entity, Field field) {
        try {
            return field.get(entity);
        } catch (IllegalAccessException e) {
            throw new OrmException("Cannot read field " + field.getName(), e);
        }
    }

    private String readMessage(Annotation annotation) {
        try {
            return (String) annotation.annotationType().getMethod("message").invoke(annotation);
        } catch (ReflectiveOperationException e) {
            throw new OrmException("Annotation " + annotation.annotationType().getName()
                    + " has no message()", e);
        }
    }

    private Class<?> getValidatedType(Class<?> validatorClass) {
        for (Type type : validatorClass.getGenericInterfaces()) {
            if (type instanceof ParameterizedType pt && pt.getRawType() == ConstraintValidator.class) {
                Type arg = pt.getActualTypeArguments()[1];
                if (arg instanceof Class<?> c) {
                    return c;
                }
            }
        }
        return Object.class;
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
