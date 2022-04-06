package tang.JdtAst.Visitor;

import org.eclipse.jdt.core.dom.*;
import tang.Smells.CodeSmell;
import tang.Smells.FileDirSmell;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * @Author TangZT
 */
public class FileDirVisitor extends ASTVisitor {
    private ArrayList<CodeSmell> smells;
    private File file;
    private TypeDeclaration type;
    public FileDirVisitor(ArrayList<CodeSmell> smells, File file, TypeDeclaration type){
        this.smells = smells;
        this.file = file;
        this.type = type;
    }
    @Override
    public boolean visit(ClassInstanceCreation node){
        if(node.getType().isSimpleType()){
            SimpleType simpleType = (SimpleType)node.getType();
            if(simpleType.getName().isSimpleName()){
                SimpleName simpleName = (SimpleName)simpleType.getName();
                if("File".equals(simpleName.getIdentifier().toString())){
                    if(node.arguments().get(0) instanceof StringLiteral){
                        //StringLiteral stringLiteral = (StringLiteral)node.arguments().get(0);
                        //if(Pattern.matches("[a-zA-Z0-9]*(/[a-zA-Z0-9])?",stringLiteral.getEscapedValue()))
                        System.out.println("***find FileDir Smell***");
                        smells.add(new FileDirSmell(node, file, type));

                    }
                }
            }
        }
        return true;
    }
}
