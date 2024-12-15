package top.hzx.lox.ast;

import top.hzx.lox.env.Environment;

import java.util.List;

public class LoxFunction implements LoxCallable {

    private final Stmt.Function declaration;

    private final Environment closure;

    public LoxFunction(Stmt.Function declaration, Environment closure) {
        this.closure = closure;
        this.declaration = declaration;
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
            return returnValue.getValue();
        }

        return null;
    }

    @Override
    public String toString() {
        return "<fn " + declaration.getName().getLexeme() + ">";
    }
}
