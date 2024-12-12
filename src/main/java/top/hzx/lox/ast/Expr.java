package top.hzx.lox.ast;

import java.util.List;

import lombok.Getter;
import top.hzx.lox.token.Token;

@SuppressWarnings("unused")
public abstract class Expr {

    public interface Visitor<R> {
        default R  visitLiteralExpr(Literal expr) { return null; }
        default R  visitUnaryExpr(Unary expr) { return null; }
        default R  visitAssignExpr(Assign expr) { return null; }
        default R  visitBinaryExpr(Binary expr) { return null; }
        default R  visitGroupingExpr(Grouping expr) { return null; }
        default R  visitVariableExpr(Variable expr) { return null; }
    }

    @Getter
    public static class Literal extends Expr {

        private final Object value;

        public Literal(Object value) {
            this.value = value;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitLiteralExpr(this);
        }
    }

    @Getter
    public static class Unary extends Expr {

        private final Token operator;
        private final Expr right;

        public Unary(Token operator, Expr right) {
            this.operator = operator;
            this.right = right;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitUnaryExpr(this);
        }
    }

    @Getter
    public static class Assign extends Expr {

        private final Token name;
        private final Expr value;

        public Assign(Token name, Expr value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitAssignExpr(this);
        }
    }

    @Getter
    public static class Binary extends Expr {

        private final Expr left;
        private final Token operator;
        private final Expr right;

        public Binary(Expr left, Token operator, Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitBinaryExpr(this);
        }
    }

    @Getter
    public static class Grouping extends Expr {

        private final Expr expression;

        public Grouping(Expr expression) {
            this.expression = expression;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitGroupingExpr(this);
        }
    }

    @Getter
    public static class Variable extends Expr {

        private final Token name;

        public Variable(Token name) {
            this.name = name;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitVariableExpr(this);
        }
    }

    public abstract <R> R accept(Visitor<R> visitor);

}
