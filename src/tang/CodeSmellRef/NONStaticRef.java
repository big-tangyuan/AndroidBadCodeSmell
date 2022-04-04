package tang.CodeSmellRef;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import tang.Smells.CodeSmell;
import tang.Smells.NONStaticSmell;

/**
 * @Author TangZT
 */
public class NONStaticRef extends CodeRef {
    @Override
    public ASTRewrite codeRef(CodeSmell codeSmell){
        NONStaticSmell nonStaticSmell = (NONStaticSmell)codeSmell;
        MethodDeclaration methodDeclaration = nonStaticSmell.getMethodDeclaration();
        ASTRewrite astRewrite = ASTRewrite.create(methodDeclaration.getAST());
        AST ast = methodDeclaration.getAST();
        ListRewrite listRewrite = astRewrite.getListRewrite(methodDeclaration, MethodDeclaration.MODIFIERS2_PROPERTY);
        listRewrite.insertLast(ast.newModifier(Modifier.ModifierKeyword.STATIC_KEYWORD), null);
        return astRewrite;
    }
}
