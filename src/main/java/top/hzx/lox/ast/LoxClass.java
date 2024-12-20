package top.hzx.lox.ast;

import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class LoxClass implements LoxCallable {

    private final String name;

    private final LoxClass superclass;

    private final Map<String, LoxFunction> methods;

    public LoxClass(String name, LoxClass superclass, Map<String, LoxFunction> methods) {
        this.superclass = superclass;
        this.name = name;
        this.methods = methods;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public int arity() {
        LoxFunction initializer = findMethod("init");
        if (initializer == null) return 0;
        // 如果用户自己定义了init方法，则返回该方法的arity
        return initializer.arity();
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        LoxInstance instance = new LoxInstance(this);
        LoxFunction initializer = findMethod("init");
        if (initializer != null) {
            // 调用用户定义的init方法
            initializer.bind(instance).call(interpreter, arguments);
        }
        return instance;
    }

    public LoxFunction findMethod(String name) {
        if (methods.containsKey(name)) {
            return methods.get(name);
        }
        if (superclass != null) {
            return superclass.findMethod(name);
        }
        return null;
    }
}
