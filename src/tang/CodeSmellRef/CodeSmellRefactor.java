package tang.CodeSmellRef;

import com.intellij.openapi.project.Project;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.text.edits.UndoEdit;
import tang.Smells.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import static org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants.*;
import static org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants.FALSE;

/**
 * @Author TangZT
 */
public class CodeSmellRefactor {
    private Project project;
    public CodeSmellRefactor(){};
    private List<CodeRef> codeRefs;
    public ASTRewrite startCodeRef(CodeSmell codeSmell) throws IOException {
        CodeRef codeRef = null;
        ASTRewrite astRewrite;
        if(codeSmell instanceof HMSmell){
            codeRef = new HMRef();
        }else if(codeSmell instanceof NONStaticSmell){
            codeRef = new NONStaticRef();
        }else if(codeSmell instanceof LogSmell){
            codeRef = new LogRef();
        }else if(codeSmell instanceof LogTagSmell){
            codeRef = new LogTagRef();
        }else if (codeSmell instanceof SQLSmell){
            codeRef = new SQLRef();
        }else if (codeSmell instanceof FileDirSmell){
            codeRef = new FileDirRef();
        }
        astRewrite = codeRef.codeRef(codeSmell);
        return astRewrite;
    }
    public boolean CodeRefactoring(File file, Document document) throws FileNotFoundException {
        if(file == null || document == null)return false;
        String text = document.get();
        PrintWriter pw = new PrintWriter(new FileOutputStream(file, false));
        pw.print(text);
        pw.flush();
        System.out.println();
        return true;
    }
}
