package syntax;

import syntax.Expr.Call;
import syntax.Expr.Dynamic;
import syntax.Expr.Logical;
import syntax.Expr.VarAssign;

public class AstPrinter implements Expr.Visitor<String> {

    // Converts an expression into a string representation
    String print(Expr expr) {
        return expr.accept(this);
    }

    // Visit a binary expression and return its string representation
    @Override
    public String visitBinaryExpr(Expr.Binary expr) {
        return parenthesize(expr.operator.lexeme, expr.left, expr.right);
    }

    // Visit a grouping expression and return its string representation
    @Override
    public String visitGroupingExpr(Expr.Grouping expr) {
        return parenthesize("group", expr.expression);
    }

    // Visit a literal expression and return its string value or "nil"
    @Override
    public String visitLiteralExpr(Expr.Literal expr) {
        if (expr.value == null) return "nil";
        return expr.value.toString();
    }

    // Visit a unary expression and return its string representation
    @Override
    public String visitUnaryExpr(Expr.Unary expr) {
        return parenthesize(expr.operator.lexeme, expr.right);
    }

    // Visit a variable expression and return its name
    @Override
    public String visitVariableExpr(Expr.Variable expr) {
        return expr.name;
    }

    // Visit a `read` expression and return "READ"
    @Override
    public String visitReadExpr(Expr.Read expr) {
        return "READ";
    }

    // Visit a `rand` expression and return "RAND"
    @Override
    public String visitRandExpr(Expr.Rand expr) {
        return "RAND";
    }

    // Helper method to format expressions in a parenthesized format
    private String parenthesize(String name, Expr... exprs) {
        StringBuilder builder = new StringBuilder();
        builder.append('(').append(name);
        for (Expr expr : exprs) {
            builder.append(' ').append(expr.accept(this));
        }
        builder.append(')');
        return builder.toString();
    }

    @Override
    public String visitLogicalExpr(Logical expr) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitLogicalExpr'");
    }

    @Override
    public String visitVarAssignExpr(VarAssign expr) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitVarAssignExpr'");
    }

    @Override
    public String visitCallExpr(Call expr) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitCallExpr'");
    }

    @Override
    public String visitDynamicExpr(Dynamic expr) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitDynamicExpr'");
    }
}
