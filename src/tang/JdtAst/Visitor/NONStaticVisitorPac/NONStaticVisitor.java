package tang.JdtAst.Visitor.NONStaticVisitorPac;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.ThisExpression;
import tang.Smells.CodeSmell;

import java.io.File;
import java.util.ArrayList;

/**
 * @Author TangZT
 */
public class NONStaticVisitor extends ASTVisitor {
    private ArrayList<CodeSmell> smells;
    private File file;
    private boolean nonStaticSmell;
    public NONStaticVisitor(ArrayList<CodeSmell> smells, File file, Boolean nonStaticSmell){
        this.smells = smells;
        this.file = file;
        this.nonStaticSmell = nonStaticSmell;
    }


    @Override
    public boolean visit(SuperFieldAccess superFieldAccess){
        nonStaticSmell = false;
        return false;
    }



}
