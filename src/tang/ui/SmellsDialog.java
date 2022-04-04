package tang.ui;


import com.intellij.diff.DiffContentFactory;
import com.intellij.diff.DiffManager;
import com.intellij.diff.DiffRequestPanel;
import com.intellij.diff.contents.DocumentContent;
import com.intellij.diff.requests.SimpleDiffRequest;
import com.intellij.diff.util.DiffUserDataKeysEx;
import com.intellij.ide.highlighter.JavaClassFileType;
import com.intellij.lang.java.JavaLanguage;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.TextEdit;
import tang.CodeSmellRef.CodeSmellRefactor;
import tang.Smells.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.concurrent.ExecutionException;

import static org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants.*;
import static org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants.FALSE;

/**
 * @Author TangZT
 */
public class SmellsDialog extends JDialog {

    ArrayList<CodeSmell> codeSmells;
    private CodeSmellRefactor codeSmellRefactor;
    private JPanel jPanel;
    private JPanel panelCode;
    private JButton btn_refactor;
    private JList codeSmellList;
    private JTextPane infoText;
    private Project project;
    private DiffRequestPanel diffRequestPanel;
    private Document document = null;
    private CodeSmell choosedSmell;
    private static final String info = "" +
            "<html><body>" +
            "<h1><b>%s</b></h1>" +
            "<h2>所在文件：%s</h2>" +
            "<h3>具体位置：%s</h3>" +
            "<h2>描述:</h2>" +
            "<h2>%s</h2>" +
            "</body></html>";

    public SmellsDialog(ArrayList<CodeSmell> codeSmells, Project project) throws IOException, BadLocationException {
        this.codeSmells = codeSmells;
        this.project = project;
        init();
    }

    private void init() throws IOException, BadLocationException {
        //jPanel = new JPanel();
        //textField1 = new JTextField();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                pack();
                setLocationRelativeTo(null);
                setSize(1000, 700);
                // 设置模态对话框
                setModal(true);
                setContentPane(jPanel);
                setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                setTitle("坏味道结果展示");
                setVisible(true);
            }
        });
        codeSmellRefactor = new CodeSmellRefactor();
        btn_refactor.setToolTipText("Click this button to apply refactoring");
        codeSmellList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        codeSmellList.setLayoutOrientation(JList.VERTICAL);
        codeSmellList.setVisibleRowCount(-1);
        codeSmellList.setCellRenderer(new SmellListRender());
        codeSmellList.setListData(codeSmells.toArray());
        codeSmellList.setSelectedIndex(0);
        codeSmellList.addListSelectionListener(e -> {
            try {
                getChoosedDiff();
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        getChoosedDiff();
        btn_refactor.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                SwingWorker<Boolean, Void> swingWorker = new SwingWorker<Boolean, Void>() {
                    @Override
                    protected Boolean doInBackground() {
                        try {
                            return codeSmellRefactor.CodeRefactoring(codeSmells.get(codeSmellList.getSelectedIndex()).getFile(), document);
                        } catch (IOException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }

                    @Override
                    protected void done() {
                        Boolean result;
                        try {
                            result = get();
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                            result = false;
                        }
                        dispose();
                    }
                };
                swingWorker.execute();
            }
        });
    }


    private void getChoosedDiff() throws BadLocationException, IOException {
        choosedSmell = codeSmells.get(codeSmellList.getSelectedIndex());
        Integer index = codeSmellList.getSelectedIndex();
        infoText.setToolTipText("文件具体位置：" + choosedSmell.getFile().getAbsolutePath());
        infoText.setText(String.format(info, choosedSmell.getName(), choosedSmell.getFile().getName(), choosedSmell.getFile().getAbsolutePath(), choosedSmell.getInfo()));
        panelCode.removeAll();
        VirtualFile vf1 = LocalFileSystem.getInstance().findFileByIoFile(choosedSmell.getFile());
        assert vf1 != null;
        DocumentContent currentDocumentContent = DiffContentFactory.getInstance().createDocument(project, vf1);
        ASTRewrite astRewrite = codeSmellRefactor.startCodeRef(choosedSmell);
        if (astRewrite != null) {
            File sourceFile = choosedSmell.getFile();
            Hashtable<String, String> options = JavaCore.getOptions();
            options.put(FORMATTER_COMMENT_FORMAT_JAVADOC_COMMENT, FALSE);
            options.put(FORMATTER_COMMENT_CLEAR_BLANK_LINES_IN_BLOCK_COMMENT, FALSE);
            options.put(FORMATTER_COMMENT_FORMAT_LINE_COMMENT, FALSE);
            String javaFileContent = new String(Files.readAllBytes(Paths.get(sourceFile.getAbsolutePath())), StandardCharsets.UTF_8);
            document = new Document(javaFileContent);
            TextEdit textEdit = astRewrite.rewriteAST(document, options);
            textEdit.apply(document);
        }
        System.out.println("document:" + document.get());
        String doc = document.get().replaceAll("(\r\n|\r)", "\n");
        com.intellij.openapi.editor.Document refaDocument = EditorFactory.getInstance().createDocument(doc);
        System.out.println("refaDocument: " + refaDocument.getText());
        DocumentContent refaDocumentContent = DiffContentFactory.getInstance().create(refaDocument.getText(),
                JavaClassFileType.INSTANCE);
        assert currentDocumentContent != null;
        assert refaDocumentContent != null;
        SimpleDiffRequest request = new SimpleDiffRequest("坏味道检测&重构结果展示", currentDocumentContent, refaDocumentContent, "坏味道" + (index + 1), "重构结果");
        diffRequestPanel = DiffManager.getInstance().createRequestPanel(project, () -> diffRequestPanel.setRequest(null), null);
        diffRequestPanel.putContextHints(DiffUserDataKeysEx.LANGUAGE, JavaLanguage.INSTANCE);
        diffRequestPanel.putContextHints(DiffUserDataKeysEx.FORCE_READ_ONLY, true);
        diffRequestPanel.setRequest(request);
        JComponent panelDiffComponent = diffRequestPanel.getComponent();
        GridConstraints gc = new GridConstraints();
        gc.setHSizePolicy(GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_CAN_SHRINK);
        gc.setFill(GridConstraints.FILL_BOTH);
        panelCode.add(panelDiffComponent, gc);
    }

    private static class SmellListRender implements ListCellRenderer {
        private static final String selected = "" +
                "<html><body>" +
                "<h1><i><b>%d. %s </b></i></h1> %s " +
                "</body></html>";
        private static final String notSelected = "" +
                "<html><body>" +
                "<h1>%d. %s </h1> %s " +
                "</body></html>";

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            CodeSmell codeSmell = (CodeSmell) value;
            JLabel label = new JLabel();
            if (isSelected) {
                label.setForeground(Color.RED);
                label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                label.setText(String.format(selected, index + 1, codeSmell.getName(), codeSmell.getFile().getName()));
            } else {
                label.setForeground(Color.BLACK);
                label.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
                label.setText(String.format(notSelected, index + 1, codeSmell.getName(), codeSmell.getFile().getName()));
            }
            return label;
        }
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        jPanel = new JPanel();
        jPanel.setLayout(new GridLayoutManager(3, 3, new Insets(0, 0, 0, 0), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 1, new Insets(10, 10, 10, 10), -1, -1));
        jPanel.add(panel1, new GridConstraints(0, 0, 3, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(50, 157), null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel1.add(scrollPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(160, 57), null, 0, false));
        codeSmellList = new JList();
        scrollPane1.setViewportView(codeSmellList);
        panelCode = new JPanel();
        panelCode.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        jPanel.add(panelCode, new GridConstraints(0, 1, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        btn_refactor = new JButton();
        btn_refactor.setText("重构");
        jPanel.add(btn_refactor, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 1, new Insets(5, 5, 5, 5), -1, -1));
        jPanel.add(panel2, new GridConstraints(0, 2, 3, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JScrollPane scrollPane2 = new JScrollPane();
        panel2.add(scrollPane2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        infoText = new JTextPane();
        infoText.setContentType("text/html");
        scrollPane2.setViewportView(infoText);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return jPanel;
    }

}
