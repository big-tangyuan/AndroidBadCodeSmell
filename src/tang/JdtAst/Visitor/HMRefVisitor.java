package tang.JdtAst.Visitor;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;

import java.util.ArrayList;

/**
 * @Author TangZT
 */
public class HMRefVisitor extends ASTVisitor {
    private ArrayList<ClassInstanceCreation> classInstanceCreations;
    public HMRefVisitor(ArrayList<ClassInstanceCreation> classInstanceCreations) {
        this.classInstanceCreations = classInstanceCreations;
    }

    @Override
    public boolean visit(ClassInstanceCreation classInstanceCreation) {
        classInstanceCreations.add(classInstanceCreation);
        return true;
    }
}
