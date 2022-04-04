package tang.CodeSmellDec;

import org.eclipse.jdt.core.dom.*;
import tang.JdtAst.Visitor.NONStaticVisitorPac.*;
import tang.Smells.CodeSmell;
import tang.Smells.NONStaticSmell;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author TangZT
 */
public class NONStaticDec extends BadSmellDec {
    private ArrayList<CodeSmell> codeSmells;
    private File file;

    public NONStaticDec(ArrayList<CodeSmell> codeSmells, File file){
        this.codeSmells = codeSmells;
        this.file = file;
    }

    @Override
    public String codeSmellType(){
        return "NONStaticSmell";
    }

    @Override
    public void codeDec(CompilationUnit cu) {
        List<TypeDeclaration> types = (List<TypeDeclaration>) cu.types();
        for(TypeDeclaration type : types){
            MethodDeclaration[] methods = type.getMethods();
            //FieldDeclaration[] fieldDeclarations = type.getFields();
            for(MethodDeclaration method : methods){
                System.out.println("method:" + method.toString());
                //ArrayList<HMSmell> smells = new ArrayList<>();
                if(Modifier.isStatic(method.getModifiers()) || method.isConstructor() || method.getBody() == null || method.getBody().statements().size()<=0)
                    continue;
                Boolean nonStaticSmell = true;
                //NONStaticVisitor visitor = new NONStaticVisitor(codeSmells, file, nonStaticSmell);
                //method.accept(visitor);
                List<IExtendedModifier> modifiers = (List<IExtendedModifier>) method.modifiers();
                for (IExtendedModifier modifier : modifiers) {
                    if (modifier.isAnnotation()) {
                        Annotation annotation = (Annotation) modifier;
                        if ("override".equals(annotation.getTypeName().toString())) {
                            nonStaticSmell = false;
                        }
                    }
                }
                if(nonStaticSmell){
                    method.accept(new ThisExpressionVisitor(nonStaticSmell));
                }
                if(nonStaticSmell){
                    method.accept(new SuperFieAccVisitor(nonStaticSmell));
                }
                if(nonStaticSmell){
                    method.accept(new SuperMethInvVisitor(nonStaticSmell));
                }

                if(nonStaticSmell){
                    codeSmells.add(new NONStaticSmell(method, file));
                }
            }
        }
    }
}
