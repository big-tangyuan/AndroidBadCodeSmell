package tang.CodeSmellRef;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import tang.Smells.CodeSmell;
import tang.Smells.LogSmell;

import java.util.List;

/**
 * @Author TangZT
 */
public class LogRef extends CodeRef {
    @Override
    public ASTRewrite codeRef(CodeSmell codeSmell){
        LogSmell logSmell = (LogSmell)codeSmell;
        ExpressionStatement expressionStatement = logSmell.getExpressionStatement();
        ASTRewrite astRewrite = ASTRewrite.create(expressionStatement.getAST());
        //添加SparseArray所需的imports
        List<ImportDeclaration> importDeclarations = ((CompilationUnit)expressionStatement.getRoot()).imports();
        boolean hasLogImport = false;
        for(ImportDeclaration importDeclaration : importDeclarations){
            if("android.util.Log".equals(importDeclaration.getName().getFullyQualifiedName())){
                hasLogImport = true;
            }
        }
        if(!hasLogImport){
            ListRewrite listRewrite = astRewrite.getListRewrite((CompilationUnit)expressionStatement.getRoot(), CompilationUnit.IMPORTS_PROPERTY);
            ImportDeclaration logImport = expressionStatement.getAST().newImportDeclaration();
            logImport.setName(expressionStatement.getAST().newName("android.util.Log"));
            listRewrite.insertFirst(logImport, null);
        }
        // 将System.out.println()方法改为Log.d(,)方法
        astRewrite.replace(expressionStatement, changePrintlnToLogd(expressionStatement), null);
        // 添加tag声明
        ListRewrite listRewrite = astRewrite.getListRewrite(logSmell.getType(), TypeDeclaration.BODY_DECLARATIONS_PROPERTY);
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

    private static ExpressionStatement changePrintlnToLogd(ExpressionStatement expressionStatement){

        AST ast = expressionStatement.getAST();
        ExpressionStatement newExpStat = (ExpressionStatement)ASTNode.copySubtree(ast, expressionStatement);
        //MethodInvocation methodInvocation = (MethodInvocation) expressionStatement.getExpression();
        MethodInvocation newMethodInvocation = ast.newMethodInvocation();
        newMethodInvocation.setName(ast.newSimpleName("d"));
        newMethodInvocation.setExpression(ast.newSimpleName("Log"));
        //SimpleType logType = ast.newSimpleType(ast.newSimpleName("log"));
        //newMethodInvocation.typeArguments().add(logType);
        newMethodInvocation.arguments().add(ast.newSimpleName("TAG"));
        //ASTNode astNode = ASTNode.copySubtree(ast, (ASTNode) ((MethodInvocation)expressionStatement.getExpression()).arguments().get(0));
        newMethodInvocation.arguments().add(ASTNode.copySubtree(ast, (ASTNode) ((MethodInvocation)expressionStatement.getExpression()).arguments().get(0)));
        newExpStat.setExpression((Expression)newMethodInvocation);
        return newExpStat;
    }
}
