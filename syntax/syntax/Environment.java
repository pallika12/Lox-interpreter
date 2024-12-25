package syntax;

import java.util.HashMap;
import java.util.Map;

class Environment {
    final Environment parentEnvironment; // renamed from 'enclosing'
    private final Map<String, Object> variableValues = new HashMap<>(); // renamed from 'values'

    Environment() {
        parentEnvironment = null;
    }
    
    Environment(Environment parentEnvironment) {
        this.parentEnvironment = parentEnvironment;
    }

    Object get(Token name) {
        if (variableValues.containsKey(name.lexeme)) {
            return variableValues.get(name.lexeme);
        }

        if (parentEnvironment != null) return parentEnvironment.get(name);

        throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
    }

    Object get(String name) {
        Token token = new Token(TokenType.IDENTIFIER, name, null, 1); // Create a Token for the identifier
        return get(token); // Use the existing method
    }

    void assign(Token name, Object value) {
        if (variableValues.containsKey(name.lexeme)) {
            variableValues.put(name.lexeme, value);
            return;
        }

        if (parentEnvironment != null) {
            parentEnvironment.assign(name, value);
            return;
        }

        throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
    }

    void define(String name, Object value) {
        variableValues.put(name, value);
    }
}
