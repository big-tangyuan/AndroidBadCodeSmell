package tang.JdtAst.Visitor;

import org.eclipse.jdt.core.dom.*;
import tang.Smells.CodeSmell;
import tang.Smells.LogSmell;

import java.io.File;
import java.util.ArrayList;

/**
 * @Author TangZT
 */
public class LogVisitor extends ASTVisitor {
    private ArrayList<CodeSmell> smells;
    private File file;
    private TypeDeclaration type;
    public LogVisitor(ArrayList<CodeSmell> smells, File file, TypeDeclaration type){
        this.smells = smells;
        this.file = file;
        this.type = type;
    }
    @Override
    public boolean visit(ExpressionStatement node){
        //System.out.println("ExpressionStatement node :" + node.toString());
        if(node.getExpression() instanceof MethodInvocation){
            MethodInvocation methodInvocation = (MethodInvocation)node.getExpression();
            Expression expression = methodInvocation.getExpression();
            if(expression instanceof QualifiedName){
                QualifiedName qualifiedName = (QualifiedName)expression;
                if(qualifiedName.getQualifier().isSimpleName() && qualifiedName.getName().isSimpleName() && methodInvocation.getName().isSimpleName()){
                    SimpleName simpleName1 = (SimpleName)qualifiedName.getQualifier();
                    SimpleName simpleName2 = (SimpleName)qualifiedName.getName();
                    if("System".equals(simpleName1.getIdentifier()) && "out".equals(simpleName2.getIdentifier()) && "println".equals(methodInvocation.getName().getIdentifier())){
                        System.out.println("***find Log Smell***");
                        smells.add(new LogSmell(node, file, type));
                    }
                }
            }
        }
        return true;
    }
}
