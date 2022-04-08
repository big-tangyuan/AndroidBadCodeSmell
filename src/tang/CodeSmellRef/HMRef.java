package tang.CodeSmellRef;

import cucumber.api.java.gl.E;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import tang.JdtAst.Visitor.HMMethodVisitor;
import tang.JdtAst.Visitor.HMRefVisitor;
import tang.Smells.CodeSmell;
import tang.Smells.HMSmell;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author TangZT
 */
public class HMRef extends CodeRef {
    private ArrayList<String> hmVariables = new ArrayList<>();
    @Override
    public ASTRewrite codeRef(CodeSmell codeSmell){
        HMSmell hmSmell = (HMSmell)codeSmell;
        VariableDeclarationStatement varDecStatement = hmSmell.getVariableDeclarationStatement();
        ASTRewrite astRewrite = ASTRewrite.create(varDecStatement.getAST());

        //将hashmap变量声明语句改为sparsearray变量声明语句
        astRewrite.replace(varDecStatement, changeHashmapToSparseArray(varDecStatement), null);

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

        //等效方法转换
        List<VariableDeclarationFragment> vFragments = varDecStatement.fragments();
        for(VariableDeclarationFragment fragment : vFragments){
            if(fragment.getName().isSimpleName()){
                SimpleName simpleName = (SimpleName)fragment.getName();
                //System.out.println("get hmVariables" + simpleName.getIdentifier());
                hmVariables.add(simpleName.getIdentifier());
            }
        }
        ArrayList<MethodInvocation> methodInvocations = new ArrayList<>();
        System.out.println("*****************hmVariables : " + hmVariables.size() + hmVariables.get(0));
        varDecStatement.getParent().accept(new HMMethodVisitor(hmVariables, methodInvocations));
        for(MethodInvocation methodInvocation : methodInvocations){
            if("containsKey".equals(methodInvocation.getName().getIdentifier())){
                astRewrite.replace(methodInvocation, replaceContainsKey(methodInvocation),null);
            }
            else if("containsValue".equals(methodInvocation.getName().getIdentifier())){
                astRewrite.replace(methodInvocation, replaceContainsValue(methodInvocation),null);
            }
            else if("getOrDefault".equals(methodInvocation.getName().getIdentifier())){
                astRewrite.replace(methodInvocation, replaceGetOrDefault(methodInvocation),null);
            }
            else if("isEmpty".equals(methodInvocation.getName().getIdentifier())){
                astRewrite.replace(methodInvocation, replaceIsEmpty(methodInvocation),null);
            }
            else if("replace".equals(methodInvocation.getName().getIdentifier())){
                astRewrite.replace(methodInvocation, replaceReplace(methodInvocation),null);
            }
        }

        //System.out.println("finish hashMap refator");

        return astRewrite;
    }

    private IfStatement replaceReplace(MethodInvocation methodInvocation){
        AST ast = methodInvocation.getAST();
        MethodInvocation ifMethInvocation = (MethodInvocation)ASTNode.copySubtree(ast, methodInvocation);
        MethodInvocation thenMethInvocation = (MethodInvocation)ASTNode.copySubtree(ast, methodInvocation);
        List<ASTNode> expressions = methodInvocation.arguments();
        IfStatement ifStatement = ast.newIfStatement();
        if(expressions.size() == 2){
            Expression key = (Expression)ASTNode.copySubtree(ast, expressions.get(0));
            Expression value = (Expression)ASTNode.copySubtree(ast, expressions.get(1));
            ifMethInvocation.setName(ast.newSimpleName("contains"));
            ifMethInvocation.arguments().clear();
            ifMethInvocation.arguments().add(key);
            ifStatement.setExpression(ifMethInvocation);
            thenMethInvocation.setName(ast.newSimpleName("put"));
            thenMethInvocation.arguments().clear();
            thenMethInvocation.arguments().add((Expression)ASTNode.copySubtree(ast, expressions.get(0)));
            thenMethInvocation.arguments().add(value);
        }else {
            Expression key = (Expression)ASTNode.copySubtree(ast, expressions.get(0));
            Expression oldValue = (Expression)ASTNode.copySubtree(ast, expressions.get(1));
            Expression newValue = (Expression)ASTNode.copySubtree(ast, expressions.get(2));
            ifMethInvocation.setName(ast.newSimpleName("get"));
            ifMethInvocation.arguments().clear();
            ifMethInvocation.arguments().add(key);
            InfixExpression infixExpression = ast.newInfixExpression();
            infixExpression.setLeftOperand(ifMethInvocation);
            infixExpression.setOperator(InfixExpression.Operator.EQUALS);
            infixExpression.setRightOperand(oldValue);
            ifStatement.setExpression(infixExpression);
            thenMethInvocation.setName(ast.newSimpleName("put"));
            thenMethInvocation.arguments().clear();
            thenMethInvocation.arguments().add((Expression)ASTNode.copySubtree(ast, expressions.get(0)));
            thenMethInvocation.arguments().add(newValue);
        }
        Block block = ast.newBlock();
        block.statements().add(ast.newExpressionStatement(thenMethInvocation));
        ifStatement.setThenStatement(block);
        return ifStatement;
    }

    private InfixExpression replaceIsEmpty(MethodInvocation methodInvocation){
        AST ast = methodInvocation.getAST();
        MethodInvocation newMethInvocation = (MethodInvocation)ASTNode.copySubtree(ast, methodInvocation);
        newMethInvocation.setName(ast.newSimpleName("size"));
        InfixExpression infixExpression = ast.newInfixExpression();
        infixExpression.setLeftOperand(newMethInvocation);
        infixExpression.setOperator(InfixExpression.Operator.EQUALS);
        infixExpression.setRightOperand(ast.newNumberLiteral("0"));
        return infixExpression;
    }

    private ConditionalExpression replaceGetOrDefault(MethodInvocation methodInvocation){
        AST ast = methodInvocation.getAST();
        MethodInvocation newMethInvocation = (MethodInvocation)ASTNode.copySubtree(ast, methodInvocation);
        newMethInvocation.setName(ast.newSimpleName("get"));
        newMethInvocation.arguments().remove(1);
        InfixExpression infixExpression = ast.newInfixExpression();
        infixExpression.setLeftOperand(newMethInvocation);
        infixExpression.setOperator(InfixExpression.Operator.EQUALS);
        infixExpression.setRightOperand(ast.newNullLiteral());

        List<ASTNode> expressions = methodInvocation.arguments();
        ConditionalExpression conditionalExpression = ast.newConditionalExpression();
        conditionalExpression.setExpression(infixExpression);
        conditionalExpression.setThenExpression((Expression) ASTNode.copySubtree(ast, expressions.get(1)));
        conditionalExpression.setElseExpression((MethodInvocation)ASTNode.copySubtree(ast, newMethInvocation));
        return conditionalExpression;
    }
    private InfixExpression replaceContainsValue(MethodInvocation methodInvocation){
        AST ast = methodInvocation.getAST();
        MethodInvocation newMethInvocation = (MethodInvocation)ASTNode.copySubtree(ast, methodInvocation);
        newMethInvocation.setName(ast.newSimpleName("indexOfValue"));
        InfixExpression infixExpression = ast.newInfixExpression();
        infixExpression.setLeftOperand(newMethInvocation);
        infixExpression.setOperator(InfixExpression.Operator.GREATER_EQUALS);
        infixExpression.setRightOperand(ast.newNumberLiteral("0"));
        return infixExpression;
    }

    private MethodInvocation replaceContainsKey(MethodInvocation methodInvocation){
        AST ast = methodInvocation.getAST();
        MethodInvocation newMethInvocation = (MethodInvocation)ASTNode.copySubtree(ast, methodInvocation);
        newMethInvocation.setName(ast.newSimpleName("contains"));
        return newMethInvocation;
    }

    private  VariableDeclarationStatement changeHashmapToSparseArray(VariableDeclarationStatement varDecStatement) {
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