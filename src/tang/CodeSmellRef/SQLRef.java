package tang.CodeSmellRef;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import tang.Smells.CodeSmell;
import tang.Smells.SQLSmell;

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
        Annotation annotation = ast.newNormalAnnotation();
        annotation.setTypeName(ast.newSimpleName("newSQLStatement"));
        ListRewrite listRewrite = astRewrite.getListRewrite(methodInvocation, MethodDeclaration.MODIFIERS2_PROPERTY);
        listRewrite.insertLast(annotation, null);
        return astRewrite;
    }
}
