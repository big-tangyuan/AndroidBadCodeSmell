package tang.JdtAst.Visitor.NONStaticVisitorPac;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;

/**
 * @Author TangZT
 */
public class SuperMethInvVisitor extends ASTVisitor {
    private boolean nonStaticSmell;
    public SuperMethInvVisitor(Boolean nonStaticSmell){
        this.nonStaticSmell = nonStaticSmell;
    }
    @Override
    public boolean visit(SuperMethodInvocation superMethodInvocation){
        nonStaticSmell = false;
        return false;
    }
}
