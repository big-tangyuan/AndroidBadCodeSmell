package tang.JdtAst.Visitor;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import tang.Smells.CodeSmell;
import tang.Smells.SQLSmell;

import java.io.File;
import java.util.ArrayList;

/**
 * @Author TangZT
 */
public class SQLVisitor extends ASTVisitor {
    private ArrayList<CodeSmell> smells;
    private File file;
    public SQLVisitor(ArrayList<CodeSmell> smells, File file){
        this.smells = smells;
        this.file = file;
    }

    @Override
    public boolean visit(MethodInvocation node){
        Expression expression = node.getExpression();
        if(node.getName().isSimpleName()){
            SimpleName sn = node.getName();
            if("execSQL".equals(sn.getIdentifier())){
                System.out.println("***find SQL Smell***");
                smells.add(new SQLSmell(node, file));
            }
        }
        return true;
    }

}
