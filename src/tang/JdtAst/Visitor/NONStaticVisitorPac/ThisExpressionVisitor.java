package tang.JdtAst.Visitor.NONStaticVisitorPac;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ThisExpression;
import tang.Smells.CodeSmell;

import java.io.File;
import java.util.ArrayList;

/**
 * @Author TangZT
 */
public class ThisExpressionVisitor  extends ASTVisitor {
    private boolean nonStaticSmell;
    public ThisExpressionVisitor(Boolean nonStaticSmell){
        this.nonStaticSmell = nonStaticSmell;
    }
    @Override
    public boolean visit(ThisExpression thisExpression) {
        nonStaticSmell = false;
        return false;
    }
}
