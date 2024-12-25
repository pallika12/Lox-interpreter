package syntax;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import syntax.Expr.Dynamic;

class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {
    final Environment globals = new Environment();
    private Environment environment = globals;
    private static final List<Integer> RAND_NUMBERS = Arrays.asList(57, 97, 28, 7, 71, 1, 79, 83, 64, 82, 89, 24);
    private static int randIndex = 0;

    @Override
    public Object visitReadExpr(Expr.Read expr) {
        System.out.print("input required > ");
        Scanner scanner = new Scanner(System.in);  // Correct constructor
        return scanner.nextLine().trim();  // Read user input and trim newline
    }

    @Override
    public Object visitRandExpr(Expr.Rand expr) {
        int result = RAND_NUMBERS.get(randIndex);
        randIndex = (randIndex + 1) % RAND_NUMBERS.size();  // Cycle through list
        return result;
    }

    Interpreter() {
        globals.define("clock", new LoxCallable() {
          @Override
          public int arity() { return 0; }
    
          @Override
          public Object call(Interpreter interpreter,
                             List<Object> arguments) {
            return (double)System.currentTimeMillis() / 1000.0;
          }
    
          @Override
          public String toString() { return "<native fn>"; }
        });
    }

    void interpret(List<Stmt> statements) {
        try {
            for (Stmt statement : statements) {
                execute(statement);
            }
        } catch (RuntimeError error) {
            Lox.runtimeError(error);
        }
    }


    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return expr.value;
    }


@Override
public Object visitFloorExpr(Expr.Floor expr) {
    Object value = evaluate(expr.argument);
    if (value instanceof Double) {
        return Math.floor((Double) value);
    }
    throw new RuntimeError(expr.operator, "Argument must be a number.");
}
 

@Override
public Object visitSubstringExpr(Expr.Substring expr) {
    Object stringValue = evaluate(expr.stringExpr);
    Object startValue = evaluate(expr.startIndex);
    Object endValue = evaluate(expr.endIndex);

    if (stringValue instanceof String && startValue instanceof Double && endValue instanceof Double) {
        String str = (String) stringValue;
        int start = (int) Math.floor((Double) startValue);
        int end = (int) Math.floor((Double) endValue);

        if (start >= 0 && end >= 0 && start <= str.length() && end <= str.length() && start <= end) {
            return str.substring(start, end);
        }
        throw new RuntimeError(expr.operator, "substring error");
    }
    throw new RuntimeError(expr.operator, "Arguments must be a string and two numbers.");
}



@Override
public Void visitStringLoopStmt(Stmt.StringLoop stmt) {
    Object stringValue = evaluate(stmt.stringExpr);

    if (!(stringValue instanceof String)) {
        throw new RuntimeError(stmt.name, "Loop expression must evaluate to a string.");
    }

    String str = (String) stringValue;
    for (int i = 0; i < str.length(); i++) {
        environment.define(stmt.name.lexeme, String.valueOf(str.charAt(i)));
        execute(stmt.body);
    }
    return null;
}

    @Override
    public Object visitLogicalExpr(Expr.Logical expr) {
      Object left = evaluate(expr.left);
  
      if (expr.operator.type == TokenType.OR) {
        if (isTruthy(left)) return left;
      } else {
        if (!isTruthy(left)) return left;
      }
  
      return evaluate(expr.right);
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        Object right = evaluate(expr.right);
  
        switch (expr.operator.type) {
            case BANG:
                return !isTruthy(right);
            case MINUS:
                checkNumberOperand(expr.operator, right);
                return -(double)right;
            }
  
        // Unreachable.
        return null;
    }

    @Override
    public Object visitVariableExpr(Expr.Variable expr) {
      return environment.get(expr.name);
    }

    private void checkNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double) return;
        throw new RuntimeError(operator, "Operand must be a number.");
    }

    private void checkNumberOperands(Token operator, Object left, Object right) {
        if (left instanceof Double && right instanceof Double) return;
        throw new RuntimeError(operator, "Operands must be numbers.");
    }

    private boolean isTruthy(Object object) {
        if (object == null) return false;
        if (object instanceof Boolean) return (boolean)object;
        return true;
    }

    private boolean isEqual(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null) return false;
    
        return a.equals(b);
    }

    private String stringify(Object object) {
        if (object == null) return "nil";
    
        if (object instanceof Double) {
            String text = object.toString();
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }
        return object.toString();
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        return evaluate(expr.expression);
    }

    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    private void execute(Stmt stmt) {
        stmt.accept(this);
    }

    void executeBlock(List<Stmt> statements, Environment environment) {
        Environment previous = this.environment;
        try {
            this.environment = environment;

            for (Stmt statement : statements) {
                execute(statement);
            }   
        } finally {
        this.environment = previous;
        }
    }

    @Override
    public Void visitBlockStmt(Stmt.Block stmt) {
      executeBlock(stmt.statements, new Environment(environment));
      return null;
    }

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        evaluate(stmt.expression);
        return null;
    }

    @Override
    public Void visitFunctionStmt(Stmt.Function stmt) {
        LoxFunction function = new LoxFunction(stmt, environment);
      environment.define(stmt.name.lexeme, function);
      return null;
    }

    @Override
    public Void visitIfStmt(Stmt.If stmt) {
      if (isTruthy(evaluate(stmt.condition))) {
        execute(stmt.thenBranch);
      } else if (stmt.elseBranch != null) {
        execute(stmt.elseBranch);
      }
      return null;
    }

    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
        Object value = evaluate(stmt.expression);
        System.out.println(stringify(value));
        return null;
    }

    @Override
    public Void visitReturnStmt(Stmt.Return stmt) {
      Object value = null;
      if (stmt.value != null) value = evaluate(stmt.value);
  
      throw new Return(value);
    }

    @Override
    public Void visitVarStmt(Stmt.Var stmt) {
      Object value = null;
      if (stmt.initializer != null) {
        value = evaluate(stmt.initializer);
      }
  
      environment.define(stmt.name.lexeme, value);
      return null;
    }

    @Override
    public Void visitWhileStmt(Stmt.While stmt) {
      while (isTruthy(evaluate(stmt.condition))) {
        execute(stmt.body);
      }
      return null;
    }

    @Override
    public Object visitAssignExpr(Expr.Assign expr) {
      Object value = evaluate(expr.value);
      environment.assign(expr.name, value);
      return value;
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right); 
  
        switch (expr.operator.type) {
            case GREATER:
                checkNumberOperands(expr.operator, left, right);
                return (double)left > (double)right;
            case GREATER_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double)left >= (double)right;
            case LESS:
                checkNumberOperands(expr.operator, left, right);
                return (double)left < (double)right;
            case LESS_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double)left <= (double)right;
            case MINUS:
                checkNumberOperands(expr.operator, left, right);
                return (double)left - (double)right;
            case PLUS:
                if (left instanceof Double && right instanceof Double) {
                    return (double)left + (double)right;
                } 
                if (left instanceof String && right instanceof String) {
                return (String)left + (String)right;
                }
            throw new RuntimeError(expr.operator, "Operands must be two numbers or two strings.");
            case SLASH:
                checkNumberOperands(expr.operator, left, right);
                return (double)left / (double)right;
            case STAR:
                checkNumberOperands(expr.operator, left, right);
                return (double)left * (double)right;
            case BANG_EQUAL:
                return !isEqual(left, right);
            case EQUAL_EQUAL:
                return isEqual(left, right);
        }
  
        // Unreachable.
        return null;
    }

    @Override
    public Object visitCallExpr(Expr.Call expr) {
      Object callee = evaluate(expr.callee);
  
      List<Object> arguments = new ArrayList<>();
      for (Expr argument : expr.arguments) { 
        arguments.add(evaluate(argument));
      }

      if (!(callee instanceof LoxCallable)) {
        throw new RuntimeError(expr.paren,
            "Can only call functions and classes.");
      }
  
      LoxCallable function = (LoxCallable)callee;

      if (arguments.size() != function.arity()) {
        throw new RuntimeError(expr.paren, "Expected " +
            function.arity() + " arguments but got " +
            arguments.size() + ".");
      }

      return function.call(this, arguments);
    }

    @Override
    public Object visitDynamicExpr(Dynamic expr) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitDynamicExpr'");
    }
} 