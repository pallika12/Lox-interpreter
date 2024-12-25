package syntax;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import syntax.Expr.Dynamic;
import syntax.Expr.VarAssign;

class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {
    // environment to hold global variables
    final Environment globals = new Environment();
    private Environment environment = globals;

    private static final List<Integer> predefinedNumbers = List.of(57, 97, 28, 7, 71, 1, 79, 83, 64, 82, 89, 24);
    private final BufferedReader reader;  // current index for predefined numbers
    private int index;  // index for predefined numbers


    public Interpreter() {
        this.reader = new BufferedReader(new InputStreamReader(System.in));

        //built-in function: substring
        globals.define("substring", new LoxCallable() {
            @Override
            public int arity() { return 3; }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                // check if arguments are of the correct types
                if (!(arguments.get(0) instanceof String) || !(arguments.get(1) instanceof Double) || !(arguments.get(2) instanceof Double)) {
                    throw new RuntimeError(new Token(TokenType.IDENTIFIER, "substring", null, 1), "Arguments must be a string and two numbers.");
                }
                String string = (String) arguments.get(0);
                int first = (int) Math.floor((Double) arguments.get(1));
                int last = (int) Math.floor((Double) arguments.get(2));

                // check if indices are valid
                if (first < 0 || last < 0 || first >= string.length() || last > string.length()) {
                    throw new RuntimeError(new Token(TokenType.IDENTIFIER, "substring", null, 1), "Substring Error");
                }

                // return substring if valid
                if (last <= first) {
                    return "";
                }

                return string.substring(first, last);
            }
        });

        //built-in function: floor
        globals.define("floor", new LoxCallable() {
            @Override
            public int arity() { return 1; }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                // check if the argument is a number
                if (!(arguments.get(0) instanceof Double)) {
                    throw new RuntimeError(new Token(TokenType.IDENTIFIER, "substring", null,1), "Arguments must be a string and two numbers.");
                }
                double temp = (Double) arguments.get(0);
                // return the floored value
                return (temp >= 0) ? Math.floor(temp) : Math.ceil(temp);
            }

            @Override
            public String toString() { return "<native fn>"; } // string representation of the function
        });
    }

    // method to get the next predefined number
    private double getNextPredefinedNumber() {
        double number = predefinedNumbers.get(index);
        index = (index + 1) % predefinedNumbers.size();
        return number;
    }
    
    // method to interpret a list of statements
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
    public Object visitCallExpr(Expr.Call expr) {
        Object callee = evaluate(expr.callee); // Evaluate the function being called
    
        List<Object> arguments = new ArrayList<>();
        for (Expr argument : expr.argument) { 
            arguments.add(evaluate(argument)); // Evaluate each argument
        }
    
        // Check if the callee is a callable function
        if (!(callee instanceof LoxCallable)) {
            throw new RuntimeError(expr.paren, "Can only call functions and classes at line " + expr.paren.line);
        }
    
        LoxCallable function = (LoxCallable) callee;
    
        // Check if the number of arguments matches the function's arity
        if (arguments.size() != function.arity()) {
            throw new RuntimeError(expr.paren, "Expected " + function.arity() + " arguments but got " + arguments.size() + " at line " + expr.paren.line + ".");
        }
    
        return function.call(this, arguments);  // Call the function with evaluated arguments
    }

    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return expr.value;
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
    public Object visitReadExpr(Expr.Read expr) {
        System.out.print("input required > ");  // prompt for input
        System.out.flush();
        try {
            String input = reader.readLine(); // read input line
            System.out.flush(); // return input
            return input;
        } catch (IOException e) {
            e.printStackTrace();
            return ""; // return empty string on error
        }
    }

    @Override
    public Object visitRandExpr(Expr.Rand expr) {
        return getNextPredefinedNumber(); // return next predefined number
    }

    @Override
    public Object visitVariableExpr(Expr.Variable expr) {
        return environment.get(expr.name);
    }

    private void checkNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double) return;
        throw new RuntimeError(operator, "Operand must be a number at line " + operator.line);
    }

    private void checkNumberOperands(Token operator, Object left, Object right) {
        if (left instanceof Double && right instanceof Double) return;
        throw new RuntimeError(operator, "Operands must be numbers at line " + operator.line);
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
    public Void visitStringLoopStmt(Stmt.StringLoop stmt) {
        Object iterable = evaluate(stmt.iterable);
        if (!(iterable instanceof String)) {
            throw new RuntimeError(stmt.name, "Loop expression must be a string.");
        }

        String str = (String) iterable;
        for (int i = 0; i < str.length(); i++) {
            environment.define(stmt.name.lexeme, String.valueOf(str.charAt(i)));
            execute(stmt.body);
        }

        return null;
    }

    @Override
    public Object visitVarAssignExpr(Expr.VarAssign expr) {
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
                return (double) left > (double) right;
            case GREATER_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double) left >= (double) right;
            case LESS:
                checkNumberOperands(expr.operator, left, right);
                return (double) left < (double) right;
            case LESS_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double) left <= (double) right;
            case MINUS:
                checkNumberOperands(expr.operator, left, right);
                return (double) left - (double) right;
            case PLUS:
                if (left instanceof Double && right instanceof Double) {
                    return (double) left + (double) right;
                }
                if (left instanceof String && right instanceof String) {
                    return (String) left + (String) right;
                }
                throw new RuntimeError(expr.operator, "Operands must be two numbers or two strings at line " + expr.operator.line);
            case SLASH:
                checkNumberOperands(expr.operator, left, right);
                return (double) left / (double) right;
            case STAR:
                checkNumberOperands(expr.operator, left, right);
                return (double) left * (double) right;
            case BANG_EQUAL:
                return !isEqual(left, right);
            case EQUAL_EQUAL:
                return isEqual(left, right);
        }

        // Unreachable.
        return null;
    }

    @Override
    public Object visitDynamicExpr(Dynamic expr) {
        // Handle the dynamic expression, e.g., return the value of the read input
        return expr.value;
    }

}






