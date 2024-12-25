package syntax;

import java.util.List;

abstract class Expr {
  
  interface Visitor<R> {
    R visitAssignExpr(Assign expr);
    R visitBinaryExpr(Binary expr);
    R visitCallExpr(Call expr);
    R visitGroupingExpr(Grouping expr);
    R visitLiteralExpr(Literal expr);
    R visitLogicalExpr(Logical expr);
    R visitUnaryExpr(Unary expr);
    R visitVariableExpr(Variable expr);
    R visitDynamicExpr(Dynamic expr);
    R visitReadExpr(Read expr);
    R visitRandExpr(Rand expr);
    R visitFloorExpr(Floor expr);
    R visitSubstringExpr(Substring expr);
  }

  // Top-level Literal class
  static class Literal extends Expr {
    public final Object value;

    public Literal(Object value) {
        this.value = value;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
        return visitor.visitLiteralExpr(this);
    }
  }

  public static class Floor extends Expr {
    public final Token operator;
    public final Expr argument;

    public Floor(Token operator, Expr argument) {
      this.operator = operator;
        this.argument = argument;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitFloorExpr(this);
    }
}



public static class Substring extends Expr {
    public final Token operator;
    public final Expr stringExpr;
    public final Expr startIndex;
    public final Expr endIndex;

    public Substring(Token operator, Expr stringExpr, Expr startIndex, Expr endIndex) {
        this.operator = operator;
        this.stringExpr = stringExpr;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitSubstringExpr(this);
    }
}

// public static class StringLoop extends Stmt {
//   public final Token name;
//   public final Expr stringExpr;
//   public final Stmt body;

//   public StringLoop(Token name, Expr stringExpr, Stmt body) {
//       this.name = name;
//       this.stringExpr = stringExpr;
//       this.body = body;
//   }

//   @Override
//   public <R> R accept(Visitor<R> visitor) {
//       return visitor.visitStringLoopStmt(this);
//   }
// }




  // Top-level Read class
  public static class Read extends Expr {
    @Override
    <R> R accept(Visitor<R> visitor) {
        return visitor.visitReadExpr(this);
    }
  }

  // Top-level Rand class
  public static class Rand extends Expr {
    @Override
    <R> R accept(Visitor<R> visitor) {
        return visitor.visitRandExpr(this);
    }
  }

  static class Assign extends Expr {
    Assign(Token name, Expr value) {
      this.name = name;
      this.value = value;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitAssignExpr(this);
    }

    final Token name;
    final Expr value;
  }

  static class Binary extends Expr {
    Binary(Expr left, Token operator, Expr right) {
      this.left = left;
      this.operator = operator;
      this.right = right;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitBinaryExpr(this);
    }

    final Expr left;
    final Token operator;
    final Expr right;
  }

  static class Call extends Expr {
    Call(Expr callee, Token paren, List<Expr> arguments) {
      this.callee = callee;
      this.paren = paren;
      this.arguments = arguments;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitCallExpr(this);
    }

    final Expr callee;
    final Token paren;
    final List<Expr> arguments;
  }

  static class Grouping extends Expr {
    Grouping(Expr expression) {
      this.expression = expression;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitGroupingExpr(this);
    }

    final Expr expression;
  }

  static class Logical extends Expr {
    Logical(Expr left, Token operator, Expr right) {
      this.left = left;
      this.operator = operator;
      this.right = right;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitLogicalExpr(this);
    }

    final Expr left;
    final Token operator;
    final Expr right;
  }

  static class Unary extends Expr {
    Unary(Token operator, Expr right) {
      this.operator = operator;
      this.right = right;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitUnaryExpr(this);
    }

    final Token operator;
    final Expr right;
  }

  static class Variable extends Expr {
    Variable(Token name) {
      this.name = name;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitVariableExpr(this);
    }

    final Token name;
  }

  static class Dynamic extends Expr {
    Dynamic(Token token) {
      this.token = token;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitDynamicExpr(this);
    }

    final Token token;
  }

  abstract <R> R accept(Visitor<R> visitor);
}