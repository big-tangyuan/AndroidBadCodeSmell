package tang.JdtAst.Visitor;

import org.eclipse.jdt.core.dom.*;
import tang.Smells.CodeSmell;
import tang.Smells.LogTagSmell;

import java.io.File;
import java.util.ArrayList;

/**
 * @Author TangZT
 */
public class LogTagVisitor extends ASTVisitor {
    private ArrayList<CodeSmell> smells;
    private File file;
    private TypeDeclaration type;
    public LogTagVisitor(ArrayList<CodeSmell> smells, File file, TypeDeclaration type){
        this.smells = smells;
        this.file = file;
        this.type = type;
    }
    @Override
    public boolean visit(ExpressionStatement node){
        if(node.getExpression() instanceof MethodInvocation){
            MethodInvocation methodInvocation = (MethodInvocation)node.getExpression();
            Expression expression = methodInvocation.getExpression();
            if(expression instanceof SimpleName && "Log".equals(((SimpleName) expression).getIdentifier())){
                if(methodInvocation.getName().isSimpleName() && "d".equals(((SimpleName)methodInvocation.getName()).getIdentifier())){
                    if(methodInvocation.arguments().get(0) instanceof StringLiteral){
                        //System.out.println("***" + ((StringLiteral) methodInvocation.arguments().get(0)).getLiteralValue() + "***");
                        if(((StringLiteral) methodInvocation.arguments().get(0)).getLiteralValue() == null || ((StringLiteral) methodInvocation.arguments().get(0)).getLiteralValue().equals("")){
                            System.out.println("***find Log Tag Smell***");
                            smells.add(new LogTagSmell(node, file, type));
                        }
                    }
                }
            }
        }
        return true;
    }
}
