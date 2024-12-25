package syntax;

import java.util.*;

public abstract class Expr {
    interface Visitor<R> {
        // Visitor interface for implementing different behavior for each expression type
        R visitBinaryExpr(Binary expr);
        R visitGroupingExpr(Grouping expr);
        R visitLiteralExpr(Literal expr);
        R visitUnaryExpr(Unary expr);
        R visitVariableExpr(Variable expr);
        R visitReadExpr(Read expr);
        R visitRandExpr(Rand expr);
        R visitLogicalExpr(Logical expr);
        R visitVarAssignExpr(VarAssign expr);
        R visitCallExpr(Call expr);
        R visitDynamicExpr(Dynamic expr);
    }

    public abstract <R> R accept(Visitor<R> visitor);

    // Binary expression class, representing two expressions and an operator
    public static class Binary extends Expr {
        final Expr left;
        final Token operator;
        final Expr right;

        Binary(Expr left, Token operator, Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitBinaryExpr(this);
        }
    }

    // Grouping expression class, representing a parenthesized expression
    public static class Grouping extends Expr {
        final Expr expression;

        Grouping(Expr expression) {
            this.expression = expression;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitGroupingExpr(this);
        }
    }

    // Literal expression class, representing constant values
    public static class Literal extends Expr {
        final Object value;

        Literal(Object value) {
            this.value = value;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitLiteralExpr(this);
        }
    }

    // Unary expression class, representing an operator and a right-hand expression
    public static class Unary extends Expr {
        final Token operator;
        final Expr right;

        Unary(Token operator, Expr right) {
            this.operator = operator;
            this.right = right;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitUnaryExpr(this);
        }
    }

    // Variable expression class, representing variable identifiers
    public static class Variable extends Expr {
        final String name;

        Variable(String name) {
            this.name = name;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitVariableExpr(this);
        }
    }

    // `read` expression class, representing the `read` keyword
    public static class Read extends Expr {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitReadExpr(this);
        }
    }

    // `rand` expression class, representing the `rand` keyword
    public static class Rand extends Expr {
        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitRandExpr(this);
        }
    }

    public static class Logical extends Expr {
        Logical(Expr left, Token operator, Expr right) {
          this.left = left;
          this.operator = operator;
          this.right = right;
        }
    
        @Override
        public
        <R> R accept(Visitor<R> visitor) {
          return visitor.visitLogicalExpr(this);
        }
    
        final Expr left;
        final Token operator;
        final Expr right;
      }

    public static class VarAssign extends Expr {
        VarAssign(Token name, Expr value) {
            this.name = name;
            this.value = value;
          }
      
          @Override
        public
          <R> R accept(Visitor<R> visitor) {
            return visitor.visitVarAssignExpr(this);
          }
      
          final Token name;
          final Expr value;
    }

    public static class Call extends Expr {
        Call(Expr callee, Token paren, List<Expr> argument) {
          this.callee = callee;
          this.paren = paren;
          this.argument = argument;
        }
    
        @Override
        public
        <R> R accept(Visitor<R> visitor) {
          return visitor.visitCallExpr(this);
        }
    
        final Expr callee;
        final Token paren;
        final List<Expr> argument;
    }

    public static class Dynamic extends Expr {
        Dynamic(Token token, Object value) {
          this.token = token;
          this.value = value;
        }
    
        @Override
        public
        <R> R accept(Visitor<R> visitor) {
          return visitor.visitDynamicExpr(this);
        }
    
        final Token token;
        final Object value;
      }
}