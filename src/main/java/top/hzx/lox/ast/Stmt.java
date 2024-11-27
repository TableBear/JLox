package top.hzx.lox.ast;

import java.util.List;

import lombok.Getter;
import top.hzx.lox.token.Token;

@SuppressWarnings("unused")
public abstract class Stmt {

    public interface Visitor<R> {
        R visitExpressionStmt(Expression stmt);
        R visitPrintStmt(Print stmt);
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

    public abstract <R> R accept(Visitor<R> visitor);

}
