package top.hzx.tool;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class GenerateAst {

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Usage: generate_ast <output directory>");
            System.exit(64);
        }
        String outputDir = args[0];
        defineAst(outputDir, "Expr", Arrays.asList(
                "Literal    : Object value",
                "Unary      : Token operator, Expr right",
                "Binary     : Expr left, Token operator, Expr right",
                "Grouping   : Expr expression"
        ));
        defineAst(outputDir, "Stmt", Arrays.asList(
                "Expression : Expr expression",
                "Print      : Expr expression"
        ));
    }

    private static void defineAst(String outputDir, String baseName, List<String> types) throws Exception {
        String path = outputDir + "/" + baseName + ".java";
        PrintWriter writer = new PrintWriter(path, "UTF-8");
        writer.println("package top.hzx.lox.ast;");
        writer.println();
        writer.println("import java.util.List;");
        writer.println();
        writer.println("import lombok.Getter;");
        writer.println("import top.hzx.lox.token.Token;");
        writer.println();
        writer.println("@SuppressWarnings(\"unused\")");
        writer.println("public abstract class " + baseName + " {");
        writer.println();
        // 生成 Visitor 接口
        defineVisitor(writer, baseName, types);
        writer.println();
        // 生成 Ast 子类开始
        for (String type : types) {
            String[] split = type.split(":");
            String className = split[0].trim();
            String fields = split[1].trim();
            defineType(writer, baseName, className, fields);
            writer.println();
        }
        // 基类生成 accept 方法
        writer.println("    public abstract <R> R accept(Visitor<R> visitor);");
        writer.println();
        // 生成 Ast 子类结束
        writer.println("}");
        writer.close();
    }

    private static void defineType(PrintWriter writer, String baseName, String className, String fields) {
        writer.println("    @Getter");
        writer.println("    public static class " + className + " extends " + baseName + " {");
        writer.println();
        String[] fieldList = fields.split(", ");
        // 字段
        for (String field : fieldList) {
            writer.println("        private final " + field + ";");
        }
        writer.println();
        // 构造函数
        writer.println("        public " + className + "(" + fields + ") {");
        for (String field : fieldList) {
            String name = field.split(" ")[1];
            writer.println("            this." + name + " = " + name + ";");
        }
        writer.println("        }");
        writer.println();
        writer.println("        @Override");
        writer.println("        public <R> R accept(Visitor<R> visitor) {");
        writer.println("            return visitor.visit" + className + baseName + "(this);");
        writer.println("        }");
        writer.println("    }");
        // 访问者模式
    }

    /**
     * 生成 Visitor 接口
     *
     * @param writer   打印流
     * @param baseName 基类名
     * @param types    子类列表
     */
    private static void defineVisitor(PrintWriter writer, String baseName, List<String> types) {
        writer.println("    public interface Visitor<R> {");
        for (String type : types) {
            String typeName = type.split(":")[0].trim();
            writer.println("        R visit" + typeName + baseName + "(" + typeName + " " + baseName.toLowerCase() + ");");
        }
        writer.println("    }");
    }
}
