package main.java.drip.hw2;

import main.java.drip.hw2.expr.Expr;
import main.java.drip.hw2.expr.Expr.Addition;
import main.java.drip.hw2.expr.Expr.Constant;

public class Main {
    public static void main(String[] args) {
        var two = new Constant(2);
        var four = new Constant(4);
        var negOne = new Expr.Negate(new Constant(1));
        var sumTwoFour = new Addition(two, four);
        var mult = new Expr.Multiplication(sumTwoFour, negOne);
        var exp = new Expr.Exponent(mult, 2);
        var res = new Addition(exp, new Constant(1));

        System.out.println(res + " = " + res.evaluate());
    }
}