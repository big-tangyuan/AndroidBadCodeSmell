package tang.Smells;

import org.eclipse.jdt.core.dom.TypeDeclarationStatement;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;

import java.io.File;

/**
 * @Author TangZT
 */
public class HMSmell extends CodeSmell {
    //private TypeDeclaration typeDeclaration;
    private VariableDeclarationStatement variableDeclarationStatement;

    public VariableDeclarationStatement getVariableDeclarationStatement() {
        return variableDeclarationStatement;
    }

    public void setVariableDeclarationStatement(VariableDeclarationStatement variableDeclarationStatement) {
        this.variableDeclarationStatement = variableDeclarationStatement;
    }

    public HMSmell(VariableDeclarationStatement variableDeclarationStatement, File file){
        super("hm smell", file, "关于hashmap相关坏味道的说明");
        this.variableDeclarationStatement = variableDeclarationStatement;
    }
}
