package orm.core;

import java.util.ArrayList;
import java.util.List;

public class ValidationException extends OrmException {

    private final List<Violation> violations;

    public ValidationException(List<Violation> violations) {
        super(buildMessage(violations));
        this.violations = new ArrayList<>(violations);
    }

    public ValidationException(String message) {
        super(message);
        this.violations = new ArrayList<>();
    }

    public List<Violation> getViolations() {
        return violations;
    }

    private static String buildMessage(List<Violation> violations) {
        StringBuilder sb = new StringBuilder("Validation failed:");
        for (Violation v : violations) {
            sb.append("\n  - ")
                    .append(v.field())
                    .append(": ")
                    .append(v.message())
                    .append(" (value = ")
                    .append(v.invalidValue())
                    .append(")");
        }
        return sb.toString();
    }
}
