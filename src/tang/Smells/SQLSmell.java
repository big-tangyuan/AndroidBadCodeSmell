package tang.Smells;

import org.eclipse.jdt.core.dom.*;

import java.io.File;

/**
 * @Author TangZT
 */
public class SQLSmell extends CodeSmell {
    private MethodInvocation methodInvocation;
    public SQLSmell(MethodInvocation methodInvocation, File file){
        super("sql smell", file, "关于sql相关坏味道的说明");
        this.methodInvocation = methodInvocation;
    }

    public MethodInvocation getMethodInvocation() {
        return methodInvocation;
    }

    public void setMethodInvocation(MethodInvocation methodInvocation) {
        this.methodInvocation = methodInvocation;
    }
}

