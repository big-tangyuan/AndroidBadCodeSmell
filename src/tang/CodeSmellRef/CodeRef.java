package tang.CodeSmellRef;

import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import tang.Smells.CodeSmell;

/**
 * @Author TangZT
 */
public abstract class CodeRef {
    public abstract ASTRewrite codeRef(CodeSmell codeSmell);
}
