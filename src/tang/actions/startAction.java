package tang.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import tang.ui.StartDialog;


/**
 * @Author TangZT
 */
public class startAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        //Project project = e.getProject();
        Project project = e.getData(PlatformDataKeys.PROJECT);
        assert project != null;
        assert psiFile != null;
        String path = psiFile.getVirtualFile().getPath();
        new StartDialog(path, project);
        /*
        byte[] input = null;
        try {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(psiFile.getVirtualFile().getPath()));
            input = new byte[bufferedInputStream.available()];
            bufferedInputStream.read(input);
            bufferedInputStream.close();
        } catch (FileNotFoundException fne) {
            fne.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        ASTParser astParser = ASTParser.newParser(AST.JLS11);
        assert input != null;
        astParser.setSource(new String(input).toCharArray());
        astParser.setKind(ASTParser.K_COMPILATION_UNIT);
        CompilationUnit cu = (CompilationUnit) astParser.createAST(null);
        DemoVisitor visitor = new DemoVisitor();
        cu.accept(visitor);
        AST ast = cu.getAST();
        ImportDeclaration importDeclaration = ast.newImportDeclaration();
        importDeclaration.setName(ast.newName(new String[]{"java","util","set"}));
        ASTRewrite astr = ASTRewrite.create(ast);
        ListRewrite lisr = astr.getListRewrite(cu,CompilationUnit.IMPORTS_PROPERTY);
        lisr.insertFirst(importDeclaration,null);
        Map<String, String> compilerOptions = JavaCore.getOptions();
        compilerOptions.put(FORMATTER_COMMENT_CLEAR_BLANK_LINES_IN_BLOCK_COMMENT, FALSE);
        compilerOptions.put(FORMATTER_COMMENT_FORMAT_JAVADOC_COMMENT, FALSE);
        compilerOptions.put(FORMATTER_COMMENT_FORMAT_LINE_COMMENT, FALSE);

        try{
            Document document = new Document(Arrays.toString(psiFile.getVirtualFile().getPath().toCharArray()));
            TextEdit edits = astr.rewriteAST(document,compilerOptions);
            edits.apply(document);
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }

         */
    }
}
