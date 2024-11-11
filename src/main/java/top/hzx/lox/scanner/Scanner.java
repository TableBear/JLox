package top.hzx.lox.scanner;

import top.hzx.lox.Lox;
import top.hzx.lox.common.Token;
import top.hzx.lox.common.TokenType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 词法分析器
 */
public class Scanner {

    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("and", TokenType.AND);
        keywords.put("class", TokenType.CLASS);
        keywords.put("else", TokenType.ELSE);
        keywords.put("false", TokenType.FALSE);
        keywords.put("for", TokenType.FOR);
        keywords.put("fun", TokenType.FUN);
        keywords.put("if", TokenType.IF);
        keywords.put("nil", TokenType.NIL);
        keywords.put("or", TokenType.OR);
        keywords.put("print", TokenType.PRINT);
        keywords.put("return", TokenType.RETURN);
        keywords.put("super", TokenType.SUPER);
        keywords.put("this", TokenType.THIS);
        keywords.put("true", TokenType.TRUE);
        keywords.put("var", TokenType.VAR);
        keywords.put("while", TokenType.WHILE);

    }

    /**
     * 源码
     */
    private final String source;

    private final List<Token> tokens = new ArrayList<>();
    /**
     * 记录每个token的开始位置
     */
    private int start = 0;
    /**
     * 记录当前扫描位置
     */
    private int current = 0;
    /**
     * 行数
     */
    private int line = 1;
    /**
     * 列数
     */
    private int column = 1;

    public Scanner(String source) {
        this.source = source;
    }

    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }
        tokens.add(new Token(TokenType.EOF, "", null, line, 0));
        return tokens;
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(':
                addToken(TokenType.LEFT_PAREN);
                break;

            case ')':
                addToken(TokenType.RIGHT_PAREN);
                break;

            case '{':
                addToken(TokenType.LEFT_BRACE);
                break;

            case '}':
                addToken(TokenType.RIGHT_BRACE);
                break;

            case ',':
                addToken(TokenType.COMMA);
                break;

            case '.':
                addToken(TokenType.DOT);
                break;

            case '-':
                addToken(TokenType.MINUS);
                break;

            case '+':
                addToken(TokenType.PLUS);
                break;

            case ';':
                addToken(TokenType.SEMICOLON);
                break;

            case '/':
                if (match('/')) {
                    // A comment goes until the end of the line.
                    while (peek() != '\n' && !isAtEnd()) advance();
                } else {
                    addToken(TokenType.SLASH);
                }

                break;

            case '*':
                addToken(TokenType.STAR);
                break;

            case '!':
                addToken(match('=') ? TokenType.BANG_EQUAL : TokenType.BANG);
                break;

            case '=':
                addToken(match('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL);
                break;

            case '>':
                addToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER);
                break;

            case '<':
                addToken(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS);
                break;
            case ' ':
            case '\r':
            case '\t':
                // 忽略空白字符
                break;

            case '\n':
                line++;
                column = 1;
                break;

            case '"':
                string();
                break;
            default:
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    Lox.error(line, "Unexpected character.");
                }
                break;

        }
    }

    /**
     * 消耗下一个待扫描的字符
     * {@link #current} 指向的当前字符，并推进一格
     *
     * @return 字符
     */
    private char advance() {
        current++;
        return source.charAt(current - 1);
    }

    /**
     * 添加一个Token
     *
     * @param type 类型
     */
    private void addToken(TokenType type) {
        addToken(type, null);
    }

    /**
     * 添加一个Token
     *
     * @param type    类型
     * @param literal 值
     */
    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line, column));
    }

    /**
     * 判断是否已经扫描完源码
     *
     * @return true or false
     */
    private boolean isAtEnd() {
        return current >= source.length();
    }

    /**
     * 匹配下一个字符是否为expected
     *
     * @param expected 待匹配的字符
     * @return true or false
     */
    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;
        newLine();
        return true;
    }

    /**
     * 获取下一个字符，但是不推进current指针
     *
     * @return 下一个字符
     */
    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    private void string() {
        // 字符串
        StringBuilder sb = new StringBuilder();
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') {
                newLine();
            }
            sb.append(advance());
        }
        if (isAtEnd()) {
            //  unterminated string
            Lox.error(line, "Unterminated string.");
            return;
        }
        // The closing ".
        advance();
        addToken(TokenType.STRING, sb.toString());
    }

    /**
     * 换行
     * <p>推进去line计数器，column计数器归为1</p>
     */
    private void newLine() {
        line++;
        column = 1;
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    public void number() {
        while (isDigit(peek())) advance();
        // Look for a fractional part.
        if (peek() == '.' && isDigit(peekNext())) {
            // Consume the ".".
            advance();
            while (isDigit(peek())) advance();

        }
        addToken(TokenType.NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    /**
     * 获取下下个字符，但是不推进current指针
     *
     * @return 下下个字符
     */
    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }

    /**
     * 识别关键字/关键字
     */
    private void identifier() {
        while (isAlphaNumeric(peek())) advance();
        // See if the identifier is a reserved word.
        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
        if (type == null) {
            type = TokenType.IDENTIFIER;
        }
        addToken(type);
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }
}
