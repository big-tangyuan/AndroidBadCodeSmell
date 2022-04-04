package tang.JdtAst.Visitor.NONStaticVisitorPac;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.SuperFieldAccess;

/**
 * @Author TangZT
 */
public class SuperFieAccVisitor extends ASTVisitor {
    private boolean nonStaticSmell;
    public SuperFieAccVisitor(Boolean nonStaticSmell){
        this.nonStaticSmell = nonStaticSmell;
    }
    @Override
    public boolean visit(SuperFieldAccess superFieldAccess){
        nonStaticSmell = false;
        return false;
    }
}
