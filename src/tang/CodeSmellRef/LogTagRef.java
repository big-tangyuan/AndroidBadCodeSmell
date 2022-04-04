package tang.CodeSmellRef;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import tang.Smells.CodeSmell;
import tang.Smells.LogTagSmell;

/**
 * @Author TangZT
 */
public class LogTagRef extends CodeRef {
    @Override
    public ASTRewrite codeRef(CodeSmell codeSmell){
        LogTagSmell logTagSmell = (LogTagSmell)codeSmell;
        ExpressionStatement expressionStatement = logTagSmell.getExpressionStatement();
        ASTRewrite astRewrite = ASTRewrite.create(expressionStatement.getAST());
        // 添加tag参数
        astRewrite.replace(expressionStatement, addTag(expressionStatement), null);
        // 添加tag声明
        ListRewrite listRewrite = astRewrite.getListRewrite(logTagSmell.getType(), TypeDeclaration.BODY_DECLARATIONS_PROPERTY);
        VariableDeclarationFragment varDecFrag = expressionStatement.getAST().newVariableDeclarationFragment();
        varDecFrag.setName(expressionStatement.getAST().newSimpleName("TAG"));
        StringLiteral stringLiteral = expressionStatement.getAST().newStringLiteral();
        stringLiteral.setLiteralValue("Change to your own tag");
        varDecFrag.setInitializer(stringLiteral);
        FieldDeclaration newFieldDec = expressionStatement.getAST().newFieldDeclaration(varDecFrag);
        newFieldDec.setType(expressionStatement.getAST().newSimpleType(expressionStatement.getAST().newSimpleName("String")));
        newFieldDec.modifiers().add(expressionStatement.getAST().newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
        newFieldDec.modifiers().add(expressionStatement.getAST().newModifier(Modifier.ModifierKeyword.STATIC_KEYWORD));
        newFieldDec.modifiers().add(expressionStatement.getAST().newModifier(Modifier.ModifierKeyword.FINAL_KEYWORD));
        listRewrite.insertFirst(newFieldDec, null);
        return astRewrite;
    }

    private static ExpressionStatement addTag(ExpressionStatement expressionStatement){
        AST ast = expressionStatement.getAST();
        ExpressionStatement newExpStat = (ExpressionStatement)ASTNode.copySubtree(ast, expressionStatement);
        MethodInvocation newMethodInvocation = (MethodInvocation)ASTNode.copySubtree(ast, expressionStatement.getExpression());
        newMethodInvocation.arguments().set(0,ast.newSimpleName("TAG"));
        newExpStat.setExpression((Expression)newMethodInvocation);
        return newExpStat;
    }
}
