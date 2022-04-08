package tang.Smells;

import b.b.I;
import b.g.b.H;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;

import java.io.File;
import java.util.HashMap;

/**
 * @Author TangZT
 */
public class DemoSmell {
    File file1 = new File("/com/package");
    File file2;
    Project project;
    VirtualFile virtualFile;
    public void test(){
        HashMap<Integer, String> hm1, hm2 = new HashMap<>(), hm3 = new HashMap<>();
        hm2.get(1);
        equals(1);
        if(hm2.get(1) == "3"){
            hm2.put(1, "2");
        }

        hm2.getOrDefault(1, "wu");
        String s = hm2.get(1) == null ? "s" : hm2.get(1);
        s = hm2.get(1) == null ? "s" : hm2.get(1);
        HashMap<Integer, Boolean> hm = new HashMap<>();
        hm.getOrDefault(1, false);
        Boolean b = hm2.size() >= 0;
        hm1 = new HashMap<>();
        hm1.put(11,"11");
        hm1.remove(11);
        hm1.containsKey(11);
        hm1.containsValue("11");
        hm1.isEmpty();
        hm1.entrySet();
        File file = new File("/com/package");
        file2 = new File("/a/b");
        new File("//");
        new File(String.valueOf(PsiManager.getInstance(project).findFile(virtualFile)));
        String a = "";
        new File(a);

    }
}
