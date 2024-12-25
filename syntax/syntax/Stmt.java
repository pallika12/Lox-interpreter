package syntax;

import java.util.List;

// abstract class representing a statement in the language
abstract class Stmt {
    // interface for the visitor pattern to handle different statement types
    interface Visitor<R> {
        R visitBlockStmt(Block stmt); // visit a block statement
        R visitExpressionStmt(Expression stmt); // visit an expression statement
        R visitFunctionStmt(Function stmt); // visit a function statement
        R visitIfStmt(If stmt); // visit an if statement
        R visitPrintStmt(Print stmt); // visit a print statement
        R visitReturnStmt(Return stmt); // visit a return statement
        R visitVarStmt(Var stmt); // visit a variable statement
        R visitWhileStmt(While stmt); // visit a while statement
        R visitStringLoopStmt(StringLoop stmt); // visit a string loop statement (added)
    }

    // class representing a block of statements
    static class Block extends Stmt {
        final List<Stmt> statements; // list of statements in the block

        Block(List<Stmt> statements) {
            this.statements = statements; // initialize statements
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitBlockStmt(this); // accept visitor for block statement
        }

        @Override
        protected void accept(Interpreter interpreter) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'accept'");
        }
    }

    // class representing an expression statement
    static class Expression extends Stmt {
        final Expr expression; // the expression being evaluated

        Expression(Expr expression) {
            this.expression = expression; // initialize expression
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitExpressionStmt(this); // accept visitor for expression statement
        }

        @Override
        protected void accept(Interpreter interpreter) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'accept'");
        }
    }

    // class representing a function statement
    static class Function extends Stmt {
        final Token name; // name of the function
        final List<Token> params; // parameters of the function
        final List<Stmt> body; // body of the function

        Function(Token name, List<Token> params, List<Stmt> body) {
            this.name = name; // initialize name
            this.params = params; // initialize parameters
            this.body = body; // initialize body
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitFunctionStmt(this); // accept visitor for function statement
        }

        @Override
        protected void accept(Interpreter interpreter) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'accept'");
        }
    }

    // class representing an if statement
    static class If extends Stmt {
        final Expr condition; // condition for the if statement
        final Stmt thenBranch; // branch to execute if condition is true
        final Stmt elseBranch; // branch to execute if condition is false

        If(Expr condition, Stmt thenBranch, Stmt elseBranch) {
            this.condition = condition; // initialize condition
            this.thenBranch = thenBranch; // initialize then branch
            this.elseBranch = elseBranch; // initialize else branch
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitIfStmt(this); // accept visitor for if statement
        }

        @Override
        protected void accept(Interpreter interpreter) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'accept'");
        }
    }

    // class representing a print statement
    static class Print extends Stmt {
        final Expr expression; // expression to print

        Print(Expr expression) {
            this.expression = expression; // initialize expression
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitPrintStmt(this); // accept visitor for print statement
        }

        @Override
        protected void accept(Interpreter interpreter) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'accept'");
        }
    }

    // class representing a return statement
    static class Return extends Stmt {
        final Token keyword; // keyword for return statement
        final Expr value; // value to return

        Return(Token keyword, Expr value) {
            this.keyword = keyword; // initialize keyword
            this.value = value; // initialize value
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitReturnStmt(this); // accept visitor for return statement
        }

        @Override
        protected void accept(Interpreter interpreter) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'accept'");
        }
    }

    // class representing a variable declaration statement
    static class Var extends Stmt {
        final Token name; // name of the variable
        final Expr initializer; // initializer expression

        Var(Token name, Expr initializer) {
            this.name = name; // initialize name
            this.initializer = initializer; // initialize initializer
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitVarStmt(this); // accept visitor for variable statement
        }

        @Override
        protected void accept(Interpreter interpreter) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'accept'");
        }
    }

    // class representing a while statement
    static class While extends Stmt {
        final Expr condition; // condition for the while loop
        final Stmt body; // body of the loop

        While(Expr condition, Stmt body) {
            this.condition = condition; // initialize condition
            this.body = body; // initialize body
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitWhileStmt(this); // accept visitor for while statement
        }

        @Override
        protected void accept(Interpreter interpreter) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'accept'");
        }
    }

    // class representing a string loop statement
    static class StringLoop extends Stmt {
        final Token name; // name for the loop variable
        final Expr iterable; // expression to iterate over
        final Stmt body; // body of the loop

        StringLoop(Token name, Expr iterable, Stmt body) {
            this.name = name; // initialize name
            this.iterable = iterable; // initialize iterable expression
            this.body = body; // initialize body
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitStringLoopStmt(this); // accept visitor for string loop statement
        }

        @Override
        protected void accept(Interpreter interpreter) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'accept'");
        }
    }

    // abstract method for accepting a visitor
    abstract <R> R accept(Visitor<R> visitor);

    protected abstract void accept(Interpreter interpreter);
}
