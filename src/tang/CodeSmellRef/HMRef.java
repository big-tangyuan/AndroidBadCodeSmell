package tang.CodeSmellRef;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import tang.JdtAst.Visitor.HMRefVisitor;
import tang.Smells.CodeSmell;
import tang.Smells.HMSmell;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author TangZT
 */
public class HMRef extends CodeRef {
    @Override
    public ASTRewrite codeRef(CodeSmell codeSmell){
        HMSmell hmSmell = (HMSmell)codeSmell;
        VariableDeclarationStatement varDecStatement = hmSmell.getVariableDeclarationStatement();
        ASTRewrite astRewrite = ASTRewrite.create(varDecStatement.getAST());
        //添加SparseArray所需的imports
        List<ImportDeclaration> importDeclarations = ((CompilationUnit)varDecStatement.getRoot()).imports();
        boolean hasSparseArrayImport = false, hasUtilImport = false;
        for(ImportDeclaration importDeclaration : importDeclarations){
            if("android.util.SparseArray".equals(importDeclaration.getName().getFullyQualifiedName())){
                hasSparseArrayImport = true;
            }
            if("java.util".equals(importDeclaration.getName().getFullyQualifiedName())){
                hasUtilImport = true;
            }
        }
        if(!hasSparseArrayImport){
            ListRewrite listRewrite = astRewrite.getListRewrite((CompilationUnit)varDecStatement.getRoot(), CompilationUnit.IMPORTS_PROPERTY);
            ImportDeclaration sparseArrayImport = varDecStatement.getAST().newImportDeclaration();
            sparseArrayImport.setName(varDecStatement.getAST().newName("android.util.SparseArray"));
            listRewrite.insertFirst(sparseArrayImport, null);
        }
        if(!hasUtilImport){
            ListRewrite listRewrite = astRewrite.getListRewrite((CompilationUnit)varDecStatement.getRoot(), CompilationUnit.IMPORTS_PROPERTY);
            ImportDeclaration utilImport = varDecStatement.getAST().newImportDeclaration();
            utilImport.setName(varDecStatement.getAST().newName("java.util"));
            listRewrite.insertFirst(utilImport, null);
        }
        //将hashmap遍历声明语句改为sparsearray变量声明语句
        astRewrite.replace(varDecStatement, changeHashmapToSparseArray(varDecStatement), null);
        System.out.println("finish hashMap refator");
        return astRewrite;
    }

    private static VariableDeclarationStatement changeHashmapToSparseArray(VariableDeclarationStatement varDecStatement) {
        AST ast = varDecStatement.getAST();
        VariableDeclarationStatement newVarDecStatement = (VariableDeclarationStatement) ASTNode.copySubtree(ast, varDecStatement);

        // Hashmap< firstType, secondType>
        ParameterizedType parameterizedType = ast.newParameterizedType(ast.newSimpleType(ast.newSimpleName("SparseArray")));
        SimpleType hashMapSecondType = (SimpleType) ((ParameterizedType) varDecStatement.getType()).typeArguments().get(1);
        SimpleType newType = (SimpleType) ASTNode.copySubtree(ast, hashMapSecondType);
        parameterizedType.typeArguments().add(newType);
        newVarDecStatement.setType(parameterizedType);

        List<VariableDeclarationFragment> newVarDeclFragments = newVarDecStatement.fragments();
        for (VariableDeclarationFragment fragment : newVarDeclFragments) {
            ArrayList<ClassInstanceCreation> classInstanceCreations = new ArrayList<>();
            fragment.accept(new HMRefVisitor(classInstanceCreations));
            if (classInstanceCreations != null && !classInstanceCreations.isEmpty()) {
                ClassInstanceCreation creation = classInstanceCreations.get(0);
                ParameterizedType newConstructor = ast.newParameterizedType(ast.newSimpleType(ast.newSimpleName("SparseArray")));
                creation.setType(newConstructor);
            }
        }

        return newVarDecStatement;
    }
}
