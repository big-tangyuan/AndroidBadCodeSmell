package tang.Smells;

import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import java.io.File;

/**
 * @Author TangZT
 */
public class LogTagSmell extends CodeSmell {
    private ExpressionStatement expressionStatement;
    private TypeDeclaration type;
    public LogTagSmell(ExpressionStatement expressionStatement, File file, TypeDeclaration type){
        super("Log Tag smell", file, "关于log tag相关坏味道的说明");
        this.expressionStatement = expressionStatement;
        this.type = type;
    }

    public ExpressionStatement getExpressionStatement() {
        return expressionStatement;
    }

    public void setExpressionStatement(ExpressionStatement expressionStatement) {
        this.expressionStatement = expressionStatement;
    }

    public TypeDeclaration getType() {
        return type;
    }

    public void setType(TypeDeclaration type) {
        this.type = type;
    }
}
