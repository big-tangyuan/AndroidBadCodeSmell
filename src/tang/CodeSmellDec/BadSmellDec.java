package tang.CodeSmellDec;

import org.eclipse.jdt.core.dom.CompilationUnit;
import tang.Smells.CodeSmell;

/**
 * @Author TangZT
 */
public abstract class BadSmellDec {

    // 针对某一种坏味道的代码检测，返回坏味道
    public abstract void codeDec(CompilationUnit cu);

    public abstract String codeSmellType();
}

