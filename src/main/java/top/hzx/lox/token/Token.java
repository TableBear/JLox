package top.hzx.lox.token;

public class Token {
    /**
     * Token 类型
     */
    private final TokenType type;
    /**
     * Token 内容
     */
    private final String lexeme;
    /**
     * Token 值
     */
    private final Object literal;
    /**
     * Token 所在行
     */
    private final int line;
    /**
     * Token 所在列
     */
    private final int column;

    public Token(TokenType type, String lexeme, Object literal, int line, int column) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
        this.column = column;
    }

    public TokenType getType() {
        return type;
    }

    public String getLexeme() {
        return lexeme;
    }

    public Object getLiteral() {
        return literal;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }
}
