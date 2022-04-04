package tang.Smells;



import org.eclipse.jdt.core.dom.MethodDeclaration;

import java.io.File;

/**
 * @Author TangZT
 */
public class NONStaticSmell extends CodeSmell {
    public MethodDeclaration getMethodDeclaration() {
        return methodDeclaration;
    }

    public void setMethodDeclaration(MethodDeclaration methodDeclaration) {
        this.methodDeclaration = methodDeclaration;
    }

    private MethodDeclaration methodDeclaration;
    public NONStaticSmell(MethodDeclaration methodDeclaration, File file) {
        super("NONStatic smell", file, "关于static相关坏味道的说明");
        this.methodDeclaration = methodDeclaration;
    }
}
