package tang.Smells;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;

import java.io.File;

/**
 * @Author TangZT
 */
public class DemoSmell {
    File file1 = new File("/com/package");
    File file2;
    Project project;
    VirtualFile virtualFile;
    public void test(){
        File file = new File("/com/package");
        file2 = new File("/a/b");
        new File("//");
        new File(String.valueOf(PsiManager.getInstance(project).findFile(virtualFile)));
        String a = "";
        new File(a);

    }
}
