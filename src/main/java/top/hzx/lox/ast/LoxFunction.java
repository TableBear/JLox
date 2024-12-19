package top.hzx.lox.ast;

import top.hzx.lox.env.Environment;

import java.util.List;

public class LoxFunction implements LoxCallable {

    private final Stmt.Function declaration;

    private final Environment closure;

    private final boolean isInitializer;

    public LoxFunction(Stmt.Function declaration, Environment closure, boolean isInitializer) {
        this.closure = closure;
        this.declaration = declaration;
        this.isInitializer = isInitializer;
    }

    @Override
    public int arity() {
        return declaration.getParams().size();
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        Environment environment = new Environment(closure);
        for (int i = 0; i < declaration.getParams().size(); i++) {
            environment.define(declaration.getParams().get(i).getLexeme(), arguments.get(i));
        }

        try {
            // 使用异常拦截返回值
            interpreter.executeBlock(declaration.getBody(), environment);
        } catch (Return returnValue) {
            // 如果是初始化器没有返回值的return语句，则返回this
            if (isInitializer) return closure.getAt(0, "this");
            return returnValue.getValue();
        }
        // 如果是初始化器则返回this
        if (isInitializer) return closure.getAt(0, "this");
        return null;
    }

    @Override
    public String toString() {
        return "<fn " + declaration.getName().getLexeme() + ">";
    }

    public LoxFunction bind(LoxInstance instance) {
        Environment environment = new Environment(closure);
        environment.define("this", instance);
        return new LoxFunction(declaration, environment, isInitializer);
    }
}
