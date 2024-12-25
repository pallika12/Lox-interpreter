package syntax;

import static syntax.TokenType.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;
    private static final Map<String, TokenType> keywords; // map to store reserved keywords


    static {
        // initializing the keywords map with reserved words and their token types
        keywords = new HashMap<>();
        keywords.put("and", AND);
        keywords.put("class", CLASS);
        keywords.put("else", ELSE);
        keywords.put("false", FALSE);
        keywords.put("for", FOR);
        keywords.put("fun", FUN);
        keywords.put("if", IF);
        keywords.put("nil", NIL);
        keywords.put("or", OR);
        keywords.put("print", PRINT);
        keywords.put("return", RETURN);
        keywords.put("super", SUPER);
        keywords.put("this", THIS);
        keywords.put("true", TRUE);
        keywords.put("var", VAR);
        keywords.put("while", WHILE);
        keywords.put("read", READ); 
        keywords.put("rand", RAND); 
        keywords.put("floor", FLOOR);
        keywords.put("substring", SUBSTRING);
        keywords.put("loop", LOOP);
        keywords.put("in", IN);
    }

    public Scanner(String source) {
        this.source = source;
    }

    public List<Token> scanTokens() {  // Scan each character in the source until the end is reached.
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }

        tokens.add(new Token(TokenType.EOF, "", null, line)); // Add EOF token at the end of the input.
        return tokens;
    }

    // method to scan a single token based on the current character
    private void scanToken() {
        char c = advance(); // advance to the next character
        switch (c) {
            case '(': addToken(LEFT_PAREN); break; // handle left parenthesis
            case ')': addToken(RIGHT_PAREN); break; // handle right parenthesis
            case '{': addToken(LEFT_BRACE); break; // handle left brace
            case '}': addToken(RIGHT_BRACE); break; // handle right brace
            case ',': addToken(COMMA); break; // handle comma
            case '.': addToken(DOT); break; // handle dot
            case '-': addToken(MINUS); break; // handle minus
            case '+': addToken(PLUS); break; // handle plus
            case ';': addToken(SEMICOLON); break; // handle semicolon
            case '*': addToken(STAR); break; // handle star
            case '!':
                if (match('!')) {
                    addToken(RAND); // handle double bang for rand
                } else if (match('=')) {
                    addToken(BANG_EQUAL); // handle not equal
                } else {
                    addToken(BANG); // handle bang
                }
                break;
            case '=':
                addToken(match('=') ? EQUAL_EQUAL : EQUAL); // handle assignment and equality
                break;
            case '<':
                if (match('-')) {
                    addToken(READ); // handle read operator
                } else {
                    addToken(LESS); // handle less than
                }
                break;
            case '>':
                addToken(match('=') ? GREATER_EQUAL : GREATER); // handle greater than
                break;
            case '/':
                if (match('/')) {
                    // handle comments until the end of the line
                    while (peek() != '\n' && !isAtEnd()) advance();
                } else {
                    addToken(SLASH); // handle slash
                }
                break;
            // ignore whitespace characters
            case ' ':
            case '\r':
            case '\t':
                break;
            case '\n':
                line++; // increment line number on new line
                break;
            // handle string literals
            case '"': string(); break;
            default:
                if (isDigit(c)) {
                    number(); // handle numeric literals
                } else if (c == 'o' && isOctDigit(peek())) {
                    octal(); // handle octal numbers
                } else if (isAlpha(c)) {
                    identifier(); // handle identifiers
                } else {
                    Lox.error(line, "Unexpected character."); // handle unexpected character
                }
                break;
        }
    }

    private void number() {  // Parse a number and add it as a token.
        while (isDigit(peek())) advance();

        if (peek() == '.' && isDigit(peekNext())) {  // Handle decimal point if present
            advance();
            while (isDigit(peek())) advance();
        }

        // Convert number string to double and add token
        addToken(TokenType.NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    private void identifier() {
        while (isAlphaNumeric(peek())) advance();

        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
        if (type == null) type = IDENTIFIER; // if not, it's an identifier
        addToken(type); // add the token
        switch (text) {  // Check for reserved words like `read` or `rand`
            case "read": type = TokenType.READ; break;
            case "rand": type = TokenType.RAND; break;
            default: type = TokenType.IDENTIFIER; break;
        }

        addToken(type, null);
    }

    // Move forward in the source string and return the current character
    private char advance() {
        return source.charAt(current++);
    }

    // Peek at the next character without consuming it
    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    // Peek two characters ahead without consuming them
    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    // Check if the next character matches the expected one
    private boolean match(char expected) {
        if (isAtEnd() || source.charAt(current) != expected) return false;
        current++;
        return true;
    }

    // Check if a character is a digit
    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    // Check if a character is a letter or underscore
    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }

    // Check if a character is alphanumeric
    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    // Add a token without a literal value
    private void addToken(TokenType type) {
        addToken(type, null);
    }

    // Add a token with a literal value
    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

    private boolean isAtEnd() {
        return isAtEnd(0); // default check
    }

    // check if the current index has reached the end of the source with a given offset
    private boolean isAtEnd(int howFar) {
        return current + howFar >= source.length();
    }

    // method to handle string literals
    private void string() {
        // read characters until the closing quote or end of source
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++; // increment line number if new line is found
            advance(); // advance to next character
        }

        if (isAtEnd()) {
            Lox.error(line, "Unterminated string."); // report error for unterminated string
            return;
        }

        advance(); // consume the closing quote

        // trim the surrounding quotes and add the string token
        String value = source.substring(start + 1, current - 1);
        addToken(STRING, value);
    }

    private boolean isOctDigit(char c) {
        return c >= '0' && c <= '7'; // return true if character is an octal digit
    }

    // private void number() {
    //     // read digits for the integer part
    //     while (isDigit(peek())) advance();

    //     // look for a fractional part
    //     if (peek() == '.' && isDigit(peek(1))) {
    //         advance(); // consume the "."
    //         while (isDigit(peek())) advance(); // read the fractional digits
    //     }

    //     // add the number token with parsed value
    //     addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
    // }

    // method to handle octal numbers
    private void octal() {
        // read octal digits
        while (isOctDigit(peek())) advance();
        addToken(NUMBER, Integer.parseInt(source.substring(start + 1, current), 8)); // add the octal token
    }

}