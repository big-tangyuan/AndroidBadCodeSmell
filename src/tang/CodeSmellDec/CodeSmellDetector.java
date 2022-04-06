package tang.CodeSmellDec;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.openapi.project.Project;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jface.text.BadLocationException;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import tang.Smells.CodeSmell;
import tang.ui.DetectoringDialog;
import tang.ui.SmellsDialog;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author TangZT
 */
public class CodeSmellDetector {
    private VirtualFile[] virtualFiles;
    private final CodeDetection codeDetection;
    private Project project;
    private ArrayList<File> javaFiles;
    private ArrayList<File> xmlFiles;
    private List<BadSmellDec> badSmellDecs;
    private Integer progressValue = 0;
    private String progressText = "";
    private DetectoringDialog detectoringDialog;

    public CodeSmellDetector(@NotNull VirtualFile[] virtualFiles, @NotNull Project project, DetectoringDialog detectoringDialog) {
        this.virtualFiles = virtualFiles;
        this.codeDetection = new CodeDetection();
        this.project = project;
        this.detectoringDialog = detectoringDialog;
        javaFiles = new ArrayList<>();
        xmlFiles = new ArrayList<>();
        for(VirtualFile file : virtualFiles){
            getFiles(file);
        }
    }


    public void startCodeDec() throws InterruptedException, IOException, BadLocationException {
        codeDetection.start();
        //System.out.println("current file:" + filePath);
        System.out.println("CodeDetection start");
        synchronized (codeDetection) {
            while(codeDetection.decResult == null){
                codeDetection.wait();
            }
        }
        detectoringDialog.done();
        new SmellsDialog(codeDetection.decResult, project);
    }

    private class CodeDetection extends Thread{
        private ArrayList<CodeSmell> decResult;
        public void run(){
            synchronized (this){
                try {
                    decResult = analyzeAst();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        private ArrayList<CodeSmell> analyzeAst() throws Exception{
            ArrayList<CodeSmell> codeSmells = new ArrayList<>();
            /*
            byte[] input = null;
            try {
                BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(filePath));
                input = new byte[bufferedInputStream.available()];
                //bufferedInputStream.read(input);
                bufferedInputStream.close();
            } catch (IOException fne) {
                fne.printStackTrace();
            }

             */
            List<File> xmlFilesWithoutDup = new ArrayList<>(new LinkedHashSet<>(xmlFiles));
            for(File file : xmlFilesWithoutDup){
                System.out.println("XMLfile:" + file.getAbsolutePath());
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document dom = builder.parse(file);
                Element root = dom.getDocumentElement();
                NodeList items = root.getChildNodes();
            }
            List<File> javaFilesWithoutDup = new ArrayList<>(new LinkedHashSet<>(javaFiles));
            for(File file : javaFilesWithoutDup){
                System.out.println("Javafile:" + file.getAbsolutePath());
                ASTParser astParser = ASTParser.newParser(AST.JLS11);
                //assert input != null;
                //System.out.println("get input:" + Arrays.toString(new String(input).toCharArray()));
                astParser.setSource(new String(Files.readAllBytes(Paths.get(file.getAbsolutePath()))).toCharArray());
                astParser.setKind(ASTParser.K_COMPILATION_UNIT);
                CompilationUnit cu = (CompilationUnit) astParser.createAST(null);
                System.out.println("created cu:" + cu.getPackage());
                if(cu.getPackage() != null){
                    badSmellDecs = new ArrayList<>();
                    badSmellDecs.add(new HMDec(codeSmells, file));
                    badSmellDecs.add(new SQLDec(codeSmells, file));
                    badSmellDecs.add(new NONStaticDec(codeSmells, file));
                    badSmellDecs.add(new LogDec(codeSmells, file));
                    badSmellDecs.add(new LogTagDec(codeSmells, file));
                    badSmellDecs.add(new FileDirDec(codeSmells, file));
                    for(BadSmellDec badSmellDec : badSmellDecs){
                        badSmellDec.codeDec(cu);
                        progressValue++;
                        progressText += badSmellDec.codeSmellType() + "检测" + file.getName() + "中\n";
                        detectoringDialog.updateProgress(progressValue, progressText);
                    }
                }
            }
            return codeSmells;
        }

    }

    private void getFiles(VirtualFile file){
        if (file.isDirectory()) {
            for (VirtualFile file1 : file.getChildren()) {
                getFiles(file1);
            }
        } else {
            javaFiles.addAll(FileTypeIndex.getFiles(JavaFileType.INSTANCE,
                    GlobalSearchScope.fileScope(
                            Objects.requireNonNull(
                                    PsiManager.getInstance(project).findFile(file))))
                    .stream()
                    .map(vf -> new File(vf.getPath()))
                    .filter(File::isFile)
                    .collect(Collectors.toList()));
            xmlFiles.addAll(FileTypeIndex.getFiles(XmlFileType.INSTANCE, GlobalSearchScope.fileScope(Objects.requireNonNull(PsiManager.getInstance(project).findFile(file))))
                    .stream()
                    .map(vf -> new File(vf.getPath()))
                    .filter(File::isFile)
                    .collect(Collectors.toList()));
        }
    }
}
