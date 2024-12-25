package syntax;

import syntax.Expr.Assign;
import syntax.Expr.Call;
import syntax.Expr.Dynamic;
import syntax.Expr.Floor;
import syntax.Expr.Logical;
import syntax.Expr.Variable;
import syntax.Expr.Rand; // Make sure to import Rand if necessary
import syntax.Expr.Read;
import syntax.Expr.Substring;

class AstPrinter implements Expr.Visitor<String> {
    String print(Expr expr) {
        return expr.accept(this);
    }

    @Override
    public String visitBinaryExpr(Expr.Binary expr) {
        return parenthesize(expr.operator.lexeme, expr.left, expr.right);
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping expr) {
        return parenthesize("group", expr.expression);
    }

    @Override
    public String visitLiteralExpr(Expr.Literal expr) {
        if (expr.value == null) return "nil";
        return expr.value.toString();
    }

    @Override
    public String visitUnaryExpr(Expr.Unary expr) {
        return parenthesize(expr.operator.lexeme, expr.right);
    }

    @Override
    public String visitRandExpr(Rand expr) { 
        return "rand"; 
    }

    private String parenthesize(String name, Expr... exprs) {
        StringBuilder builder = new StringBuilder();
        builder.append("(").append(name);
        for (Expr expr : exprs) {
            builder.append(" ");
            builder.append(expr.accept(this));
        }
        builder.append(")");
        return builder.toString();
    }

    @Override
    public String visitAssignExpr(Assign expr) {
        throw new UnsupportedOperationException("Unimplemented method 'visitAssignExpr'");
    }

    @Override
    public String visitVariableExpr(Variable expr) {
        throw new UnsupportedOperationException("Unimplemented method 'visitVariableExpr'");
    }

    @Override
    public String visitLogicalExpr(Logical expr) {
        throw new UnsupportedOperationException("Unimplemented method 'visitLogicalExpr'");
    }

    @Override
    public String visitCallExpr(Call expr) {
        throw new UnsupportedOperationException("Unimplemented method 'visitCallExpr'");
    }

    @Override
    public String visitDynamicExpr(Dynamic expr) {
        throw new UnsupportedOperationException("Unimplemented method 'visitDynamicExpr'");
    }

    @Override
    public String visitReadExpr(Read expr) {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Unimplemented method 'visitReadExpr'");
    }

    @Override
    public String visitFloorExpr(Floor expr) {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Unimplemented method 'visitFloorExpr'");
    }

    @Override
    public String visitSubstringExpr(Substring expr) {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Unimplemented method 'visitSubstringExpr'");
    }
}