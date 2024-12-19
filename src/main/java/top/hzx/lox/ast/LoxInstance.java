package top.hzx.lox.ast;

import top.hzx.lox.err.RuntimeError;
import top.hzx.lox.token.Token;

import java.util.Map;
import java.util.HashMap;

public class LoxInstance {

    private final LoxClass klass;

    private final Map<String, Object> fields = new HashMap<>();

    public LoxInstance(LoxClass klass) {
        this.klass = klass;
    }

    @Override
    public String toString() {
        return klass.getName() + " instance";
    }

    public Object get(Token name) {
        if (fields.containsKey(name.getLexeme())) {
            return fields.get(name.getLexeme());
        }
        LoxFunction method = klass.findMethod(name.getLexeme()).bind(this);
        if (method != null) return method;

        throw new RuntimeError(name, "Undefined property '" + name.getLexeme() + "'.");
    }

    public void set(Token name, Object value) {
        fields.put(name.getLexeme(), value);
    }
}
