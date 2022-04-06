package tang.CodeSmellRef;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import tang.Smells.CodeSmell;
import tang.Smells.FileDirSmell;

/**
 * @Author TangZT
 */
public class FileDirRef extends CodeRef {
    @Override
    public ASTRewrite codeRef(CodeSmell codeSmell){
        FileDirSmell fileDirSmell = (FileDirSmell)codeSmell;
        ClassInstanceCreation classInstanceCreation = fileDirSmell.getClassInstanceCreation();
        ASTRewrite astRewrite = ASTRewrite.create(classInstanceCreation.getAST());
        classInstanceCreation.arguments().get(0);
        astRewrite.replace(classInstanceCreation, changeCICreation(classInstanceCreation), null);
        ListRewrite listRewrite = astRewrite.getListRewrite(fileDirSmell.getType(), TypeDeclaration.BODY_DECLARATIONS_PROPERTY);
        VariableDeclarationFragment varDecFrag = classInstanceCreation.getAST().newVariableDeclarationFragment();
        varDecFrag.setName(classInstanceCreation.getAST().newSimpleName("FILE_PATH"));
        StringLiteral stringLiteral = classInstanceCreation.getAST().newStringLiteral();
        stringLiteral.setLiteralValue("Please use relevant API interface to obtain file directory.");
        varDecFrag.setInitializer(stringLiteral);
        FieldDeclaration newFieldDec = classInstanceCreation.getAST().newFieldDeclaration(varDecFrag);
        newFieldDec.setType(classInstanceCreation.getAST().newSimpleType(classInstanceCreation.getAST().newSimpleName("String")));
        listRewrite.insertFirst(addFilePath(classInstanceCreation), null);
        return astRewrite;
    }

    private FieldDeclaration addFilePath(ClassInstanceCreation classInstanceCreation){
        AST ast = classInstanceCreation.getAST();
        VariableDeclarationFragment varDecFrag = ast.newVariableDeclarationFragment();
        varDecFrag.setName(ast.newSimpleName("FILE_PATH"));
        StringLiteral stringLiteral = ast.newStringLiteral();
        stringLiteral.setLiteralValue("Please use relevant API interface to obtain file directory.");
        varDecFrag.setInitializer(stringLiteral);
        FieldDeclaration newFieldDec = ast.newFieldDeclaration(varDecFrag);
        newFieldDec.setType(ast.newSimpleType(classInstanceCreation.getAST().newSimpleName("String")));
        newFieldDec.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
        newFieldDec.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.STATIC_KEYWORD));
        newFieldDec.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.FINAL_KEYWORD));
        return  newFieldDec;
    }

    private ClassInstanceCreation changeCICreation(ClassInstanceCreation classInstanceCreation){
        AST ast = classInstanceCreation.getAST();
        ClassInstanceCreation newCICreation = (ClassInstanceCreation) ASTNode.copySubtree(ast, classInstanceCreation);
        newCICreation.arguments().set(0,ast.newSimpleName("FILE_PATH"));
        return newCICreation;
    }
}
