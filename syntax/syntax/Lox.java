package syntax;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lox {
    static boolean hadError = false;
    static boolean hadRuntimeError = false;
    static String phase = "parse";
    private static final Interpreter interpreter = new Interpreter(); // interpreter instance

    public static void main(String[] args) throws IOException {
        if (args.length > 2) {
            System.err.println("Usage: java Lox [scan|parse] [file]");
            System.exit(64);
        } else if (args.length == 2) {
            phase = args[0];
            runFile(args[1]);
        } else if (args.length == 1) {
            runPrompt();
        } else {
            System.err.println("Usage: java Lox [scan|parse] [file]");
            System.exit(64);
        }
    }

    private static void runFile(String path) {  // Run Lox from a file
        byte[] bytes;
        try {
            bytes = Files.readAllBytes(Paths.get(path));
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(64);
            return;
        }

        String source = new String(bytes);

        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        if (phase.equals("scan")) {
            for (Token token : tokens) {
                System.out.println(token);
            }
        } else if (phase.equals("parse")) {
            Parser parser = new Parser(tokens);
            Expr expression = parser.parse();
            if (hadError) return;
            System.out.println(new AstPrinter().print(expression));
        }
    }

    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in); // create input reader
        BufferedReader reader = new BufferedReader(input); // buffer reader for input

        for (;;) { // infinite loop for prompt
            System.out.print("> "); // prompt for input
            String line = reader.readLine(); // read line from input
            if (line == null) break; // exit on EOF
            run(line); // run the input line
            hadError = false; // reset error flag
        }
    }

    static void error(int line, String message) {
        report(line, "", message);
    }

    private static void report(int line, String where, String message) {
        System.err.printf("[line %d] Error%s: %s%n", line, where, message);
        hadError = true;
    }

    private static void run(String source) {
        Scanner scanner = new Scanner(source); // create scanner for source
        List<Token> tokens = scanner.scanTokens(); // scan tokens from source
        Parser parser = new Parser(tokens); // create parser with tokens
        @SuppressWarnings("unchecked")
        List<Stmt> statements = (List<Stmt>) parser.parse(); // parse statements
        if (hadError) return; // exit if there was a parsing error

        interpreter.interpret(statements); // interpret the parsed statements
    }

    public static void scan(String source, Scanner scanner) {
        scanner.scanTokens().forEach(token -> System.out.println(token)); // print scanned tokens
    }

    static void error(Token token, String message) {
        // report error based on token type
        if (token.type == TokenType.EOF) {
            report(token.line, " at end", message); // error at end of file
        } else {
            report(token.line, " at '" + token.lexeme + "'", message); // error at specific token
        }
    }

    static void runtimeError(RuntimeError error) {
        // print runtime error message with line number
        System.err.println(error.getMessage() + "\n[line " + error.token.line + "]");
        hadRuntimeError = true; // set runtime error flag
    }
}