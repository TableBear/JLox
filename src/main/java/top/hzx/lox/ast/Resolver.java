package top.hzx.lox.ast;

import top.hzx.lox.Lox;
import top.hzx.lox.token.Token;

import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * 对变量的作用域进行一次扫描
 *
 * @author zhuox
 */
public class Resolver implements Expr.Visitor<Void>, Stmt.Visitor<Void> {

    private final Interpreter interpreter;

    private final Stack<Map<String, Boolean>> scopes = new Stack<>();

    private FunctionType currentFunction = FunctionType.NONE;

    private ClassType currentClass = ClassType.NONE;

    public Resolver(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    private enum ClassType {
        NONE,
        CLASS,
        SUBCLASS,
    }

    private enum FunctionType {
        NONE,
        FUNCTION,
        INITIALIZER,
        METHOD,
    }

    private void beginScope() {
        scopes.push(new java.util.HashMap<>());
    }

    private void endScope() {
        scopes.pop();
    }

    public void resolve(List<Stmt> statements) {
        for (Stmt statement : statements) {
            resolve(statement);
        }
    }

    private void resolve(Stmt stmt) {
        stmt.accept(this);
    }

    private void resolve(Expr expr) {
        expr.accept(this);
    }

    @Override
    public Void visitBlockStmt(Stmt.Block stmt) {
        beginScope();
        resolve(stmt.getStatements());
        endScope();
        return null;
    }

    @Override
    public Void visitClassStmt(Stmt.Class stmt) {
        ClassType enclosingClass = currentClass;
        currentClass = ClassType.CLASS;
        declare(stmt.getName());
        define(stmt.getName());
        if (stmt.getSuperclass() != null && stmt.getName().getLexeme().equals(stmt.getSuperclass().getName().getLexeme())) {
            Lox.error(stmt.getSuperclass().getName(), "A class can't inherit from itself.");
        }
        if (stmt.getSuperclass() != null) {
            currentClass = ClassType.SUBCLASS;
            resolve(stmt.getSuperclass());
        }
        if (stmt.getSuperclass() != null) {
            beginScope();
            scopes.peek().put("super", true);
        }
        beginScope();
        scopes.peek().put("this", true);
        for (Stmt.Function method : stmt.getMethods()) {
            FunctionType declaration = FunctionType.METHOD;
            if (method.getName().getLexeme().equals("init")) {
                declaration = FunctionType.INITIALIZER;
            }
            resolveFunction(method, declaration);
        }
        endScope();
        if (stmt.getSuperclass() != null) {
            endScope();
        }
        currentClass = enclosingClass;
        return null;
    }

    @Override
    public Void visitVarStmt(Stmt.Var stmt) {
        declare(stmt.getName());
        if (stmt.getInitializer() != null) {
            resolve(stmt.getInitializer());
        }
        define(stmt.getName());
        return null;
    }

    private void declare(Token name) {
        if (scopes.isEmpty()) return;

        Map<String, Boolean> scope = scopes.peek();
        if (scope.containsKey(name.getLexeme())) {
            Lox.error(name, "Already variable with this name in this scope.");
        }
        scope.put(name.getLexeme(), false);
    }

    private void define(Token name) {
        if (scopes.isEmpty()) return;
        scopes.peek().put(name.getLexeme(), true);
    }

    @Override
    public Void visitVariableExpr(Expr.Variable expr) {
        if (!scopes.isEmpty() && scopes.peek().get(expr.getName().getLexeme()) == Boolean.FALSE) {
            Lox.error(expr.getName(), "Can't read local variable in its own initializer.");
        }

        resolveLocal(expr, expr.getName());
        return null;
    }

    private void resolveLocal(Expr expr, Token name) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).containsKey(name.getLexeme())) {
                interpreter.resolve(expr, scopes.size() - 1 - i);
                return;
            }
        }
    }

    @Override
    public Void visitAssignExpr(Expr.Assign expr) {
        resolve(expr.getValue());
        resolveLocal(expr, expr.getName());
        return null;
    }

    @Override
    public Void visitFunctionStmt(Stmt.Function stmt) {
        declare(stmt.getName());
        define(stmt.getName());
        resolveFunction(stmt, FunctionType.FUNCTION);
        return null;
    }

    private void resolveFunction(Stmt.Function stmt, FunctionType type) {
        FunctionType enclosingFunction = currentFunction;
        currentFunction = type;
        beginScope();
        for (Token param : stmt.getParams()) {
            declare(param);
            define(param);
        }
        resolve(stmt.getBody());
        endScope();
        currentFunction = enclosingFunction;
    }

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        resolve(stmt.getExpression());
        return null;
    }

    @Override
    public Void visitIfStmt(Stmt.If stmt) {
        resolve(stmt.getCondition());
        resolve(stmt.getThenBranch());
        if (stmt.getElseBranch() != null) resolve(stmt.getElseBranch());
        return null;
    }

    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
        resolve(stmt.getExpression());
        return null;
    }

    @Override
    public Void visitReturnStmt(Stmt.Return stmt) {
        if (currentFunction == FunctionType.NONE) {
            Lox.error(stmt.getKeyword(), "Can't return from top-level code.");
        }
        if (stmt.getValue() != null) {
            if (currentFunction == FunctionType.INITIALIZER) {
                Lox.error(stmt.getKeyword(), "Can't return a value from an initializer.");
            }
            resolve(stmt.getValue());
        }
        return null;
    }

    @Override
    public Void visitWhileStmt(Stmt.While stmt) {
        resolve(stmt.getCondition());
        resolve(stmt.getBody());
        return null;
    }

    @Override
    public Void visitBinaryExpr(Expr.Binary expr) {
        resolve(expr.getLeft());
        resolve(expr.getRight());
        return null;
    }

    @Override
    public Void visitCallExpr(Expr.Call expr) {
        resolve(expr.getCallee());

        for (Expr argument : expr.getArguments()) {
            resolve(argument);
        }

        return null;
    }

    @Override
    public Void visitGetExpr(Expr.Get expr) {
        resolve(expr.getObject());
        return null;
    }

    @Override
    public Void visitGroupingExpr(Expr.Grouping expr) {
        resolve(expr.getExpression());
        return null;
    }

    @SuppressWarnings("all")
    @Override
    public Void visitLiteralExpr(Expr.Literal expr) {
        return null;
    }

    @Override
    public Void visitLogicalExpr(Expr.Logical expr) {
        resolve(expr.getLeft());
        resolve(expr.getRight());
        return null;
    }

    @Override
    public Void visitSetExpr(Expr.Set expr) {
        resolve(expr.getValue());
        resolve(expr.getObject());
        return null;
    }

    @Override
    public Void visitSuperExpr(Expr.Super expr) {
        if (currentClass == ClassType.NONE) {
            Lox.error(expr.getKeyword(), "Can't use 'super' outside of a class.");
        } else if (currentClass != ClassType.SUBCLASS) {
            Lox.error(expr.getKeyword(), "Can't use 'super' in a class with no superclass.");
        }
        resolveLocal(expr, expr.getKeyword());
        return null;
    }

    @Override
    public Void visitThisExpr(Expr.This expr) {
        if (currentClass == ClassType.NONE) {
            Lox.error(expr.getKeyword(), "Can't use 'this' outside of a class.");
            return null;
        }
        resolveLocal(expr, expr.getKeyword());
        return null;
    }

    @Override
    public Void visitUnaryExpr(Expr.Unary expr) {
        resolve(expr.getRight());
        return null;
    }

}
