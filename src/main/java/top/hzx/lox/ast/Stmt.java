package top.hzx.lox.ast;

import java.util.List;

import lombok.Getter;
import top.hzx.lox.token.Token;

@SuppressWarnings("unused")
public abstract class Stmt {

    public interface Visitor<R> {
        default R  visitBlockStmt(Block stmt) { return null; }
        default R  visitClassStmt(Class stmt) { return null; }
        default R  visitExpressionStmt(Expression stmt) { return null; }
        default R  visitFunctionStmt(Function stmt) { return null; }
        default R  visitIfStmt(If stmt) { return null; }
        default R  visitPrintStmt(Print stmt) { return null; }
        default R  visitReturnStmt(Return stmt) { return null; }
        default R  visitVarStmt(Var stmt) { return null; }
        default R  visitWhileStmt(While stmt) { return null; }
    }

    @Getter
    public static class Block extends Stmt {

        private final List<Stmt> statements;

        public Block(List<Stmt> statements) {
            this.statements = statements;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitBlockStmt(this);
        }
    }

    @Getter
    public static class Class extends Stmt {

        private final Token name;
        private final Expr.Variable superclass;
        private final List<Stmt.Function> methods;

        public Class(Token name, Expr.Variable superclass, List<Stmt.Function> methods) {
            this.name = name;
            this.superclass = superclass;
            this.methods = methods;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitClassStmt(this);
        }
    }

    @Getter
    public static class Expression extends Stmt {

        private final Expr expression;

        public Expression(Expr expression) {
            this.expression = expression;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitExpressionStmt(this);
        }
    }

    @Getter
    public static class Function extends Stmt {

        private final Token name;
        private final List<Token> params;
        private final List<Stmt> body;

        public Function(Token name, List<Token> params, List<Stmt> body) {
            this.name = name;
            this.params = params;
            this.body = body;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitFunctionStmt(this);
        }
    }

    @Getter
    public static class If extends Stmt {

        private final Expr condition;
        private final Stmt thenBranch;
        private final Stmt elseBranch;

        public If(Expr condition, Stmt thenBranch, Stmt elseBranch) {
            this.condition = condition;
            this.thenBranch = thenBranch;
            this.elseBranch = elseBranch;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitIfStmt(this);
        }
    }

    @Getter
    public static class Print extends Stmt {

        private final Expr expression;

        public Print(Expr expression) {
            this.expression = expression;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitPrintStmt(this);
        }
    }

    @Getter
    public static class Return extends Stmt {

        private final Token keyword;
        private final Expr value;

        public Return(Token keyword, Expr value) {
            this.keyword = keyword;
            this.value = value;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitReturnStmt(this);
        }
    }

    @Getter
    public static class Var extends Stmt {

        private final Token name;
        private final Expr initializer;

        public Var(Token name, Expr initializer) {
            this.name = name;
            this.initializer = initializer;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitVarStmt(this);
        }
    }

    @Getter
    public static class While extends Stmt {

        private final Expr condition;
        private final Stmt body;

        public While(Expr condition, Stmt body) {
            this.condition = condition;
            this.body = body;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitWhileStmt(this);
        }
    }

    @SuppressWarnings("all")
    public abstract <R> R accept(Visitor<R> visitor);

}
