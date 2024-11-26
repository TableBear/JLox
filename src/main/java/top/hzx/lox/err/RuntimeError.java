package top.hzx.lox.err;

import lombok.Getter;
import top.hzx.lox.token.Token;

@Getter
public class RuntimeError extends RuntimeException {

    private final Token token;

    public RuntimeError(Token token, String message) {
        super(message);
        this.token = token;
    }

}
