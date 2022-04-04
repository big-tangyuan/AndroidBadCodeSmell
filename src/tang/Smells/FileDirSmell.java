package tang.Smells;

import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import java.io.File;

/**
 * @Author TangZT
 */
public class FileDirSmell extends CodeSmell {
    private ExpressionStatement expressionStatement;
    private VariableDeclarationStatement variableDeclarationStatement;
    private TypeDeclaration type;
    public FileDirSmell(ExpressionStatement expressionStatement, File file, TypeDeclaration type){
        super("FireDir smell", file, "关于FireDir相关坏味道的说明");
        this.expressionStatement = expressionStatement;
        this.type = type;
    }
    public FileDirSmell(VariableDeclarationStatement variableDeclarationStatement, File file, TypeDeclaration type){
        super("FireDir smell", file, "关于FireDir相关坏味道的说明");
        this.variableDeclarationStatement = variableDeclarationStatement;
        this.type = type;
    }

    public ExpressionStatement getExpressionStatement() {
        return expressionStatement;
    }

    public void setExpressionStatement(ExpressionStatement expressionStatement) {
        this.expressionStatement = expressionStatement;
    }
}
