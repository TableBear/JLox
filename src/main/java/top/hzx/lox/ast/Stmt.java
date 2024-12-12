package top.hzx.lox.ast;

import java.util.List;

import lombok.Getter;
import top.hzx.lox.token.Token;

@SuppressWarnings("unused")
public abstract class Stmt {

    public interface Visitor<R> {
        default R  visitBlockStmt(Block stmt) { return null; }
        default R  visitExpressionStmt(Expression stmt) { return null; }
        default R  visitPrintStmt(Print stmt) { return null; }
        default R  visitVarStmt(Var stmt) { return null; }
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

    public abstract <R> R accept(Visitor<R> visitor);

}
