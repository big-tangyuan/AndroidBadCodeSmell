package tang.CodeSmellRef;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import tang.Smells.CodeSmell;
import tang.Smells.SQLSmell;

import java.util.List;

/**
 * @Author TangZT
 */
public class SQLRef extends CodeRef {
    @Override
    public ASTRewrite codeRef(CodeSmell codeSmell){
        SQLSmell sqlSmell = (SQLSmell)codeSmell;
        MethodInvocation methodInvocation = sqlSmell.getMethodInvocation();
        AST ast = methodInvocation.getAST();
        ASTRewrite astRewrite = ASTRewrite.create(ast);
        List<ASTNode> expressions = methodInvocation.arguments();
        Expression sql = (Expression)ASTNode.copySubtree(ast, expressions.get(0));
        MethodInvocation newMethodInvocation = ast.newMethodInvocation();
        newMethodInvocation.setName(ast.newSimpleName("d"));
        newMethodInvocation.setExpression(ast.newSimpleName("Log"));
        StringLiteral stringLiteral = ast.newStringLiteral();
        stringLiteral.setLiteralValue("请使用防SQL注入的方法执行SQL语句：");
        newMethodInvocation.arguments().add(stringLiteral);
        newMethodInvocation.arguments().add(ASTNode.copySubtree(ast, sql));
        astRewrite.replace(methodInvocation, newMethodInvocation, null);
        return astRewrite;
    }
}
