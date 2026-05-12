package main.java.drip.hw2.expr;

public sealed interface Expr {
    double evaluate();

    public record Constant(double a) implements Expr {
        @Override
        public double evaluate() {
            return a;
        }
    }
    public record Negate(Expr value) implements Expr {
        @Override
        public double evaluate() {
            return -value.evaluate();
        }
    }
    public record Exponent(Expr value, double power) implements Expr {
        @Override
        public double evaluate() {
            return Math.pow(value.evaluate(), power);
        }
    }
    public record Addition(Expr firstValue, Expr secondValue) implements Expr {
        @Override
        public double evaluate() {
            return firstValue.evaluate() + secondValue.evaluate();
        }
    }
    public record Multiplication(Expr firstValue, Expr secondValue) implements Expr {
        @Override
        public double evaluate() {
            return firstValue.evaluate() * secondValue.evaluate();
        }
    }
}
