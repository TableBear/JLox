package top.hzx.lox.ast;

import lombok.Getter;

/**
 * 使用异常包装返回值
 */
@Getter
public class Return extends RuntimeException {
    private final Object value;

    public Return(Object value) {
        super(null, null, false, false);
        this.value = value;
    }
}
