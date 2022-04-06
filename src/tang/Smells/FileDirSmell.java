package tang.Smells;

import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import java.io.File;

/**
 * @Author TangZT
 */
public class FileDirSmell extends CodeSmell {
    private ClassInstanceCreation classInstanceCreation;
    private TypeDeclaration type;
    public FileDirSmell(ClassInstanceCreation classInstanceCreation, File file, TypeDeclaration type){
        super("FireDir smell", file, "关于FireDir相关坏味道的说明");
        this.classInstanceCreation = classInstanceCreation;
        this.type = type;
    }

    public ClassInstanceCreation getClassInstanceCreation() {
        return classInstanceCreation;
    }

    public void setClassInstanceCreation(ClassInstanceCreation classInstanceCreation) {
        this.classInstanceCreation = classInstanceCreation;
    }

    public TypeDeclaration getType() {
        return type;
    }

    public void setType(TypeDeclaration type) {
        this.type = type;
    }
}
