package tang.JdtAst.Visitor;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;

import java.util.ArrayList;

/**
 * @Author TangZT
 */
public class HMMethodVisitor extends ASTVisitor {
    private ArrayList<MethodInvocation> methodInvocations;
    private ArrayList<String> hmVariables;
    public HMMethodVisitor(ArrayList<String> hmVariables, ArrayList<MethodInvocation> methodInvocations){
        this.methodInvocations = methodInvocations;
        this.hmVariables = hmVariables;
    }

    @Override
    public boolean visit(MethodInvocation methodInvocation){
        if(methodInvocation.getName().isSimpleName()){
            //System.out.println("get SimpleName" + methodInvocation.getName().getIdentifier());
            SimpleName simpleName = (SimpleName)methodInvocation.getName();
            SimpleName expression = (SimpleName)methodInvocation.getExpression();
            System.out.println("*****************hmSimpleName : " + methodInvocation.getName().getIdentifier());
            if(expression != null && hmVariables.contains(expression.getIdentifier())){
                //System.out.println("get methodInvocation" + simpleName.getIdentifier());
                methodInvocations.add(methodInvocation);
            }

        }
        return true;
    }
}
