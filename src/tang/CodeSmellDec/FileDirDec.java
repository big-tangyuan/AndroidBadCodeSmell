package tang.CodeSmellDec;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import tang.JdtAst.Visitor.FileDirVisitor;
import tang.JdtAst.Visitor.HMVisitor;
import tang.Smells.CodeSmell;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author TangZT
 */
public class FileDirDec extends BadSmellDec {
    private ArrayList<CodeSmell> codeSmells;
    private File file;

    public FileDirDec(ArrayList<CodeSmell> codeSmells, File file){
        this.codeSmells = codeSmells;
        this.file = file;
    }
    @Override
    public String codeSmellType(){
        return "FileDirSmell";
    }

    @Override
    public void codeDec(CompilationUnit cu) {
        List<TypeDeclaration> types = (List<TypeDeclaration>) cu.types();
        for(TypeDeclaration type : types){
            MethodDeclaration[] methods = type.getMethods();
            for(MethodDeclaration method : methods){
                System.out.println("method:" + method.toString());
                FileDirVisitor visitor = new FileDirVisitor(codeSmells, file, type);
                method.accept(visitor);
            }
        }
    }
}
