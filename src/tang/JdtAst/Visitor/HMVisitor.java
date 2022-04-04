package tang.JdtAst.Visitor;


import org.eclipse.jdt.core.dom.*;
import tang.Smells.CodeSmell;
import tang.Smells.HMSmell;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author TangZT
 */
public class HMVisitor extends ASTVisitor {
    private ArrayList<CodeSmell> smells;
    private File file;
    public HMVisitor(ArrayList<CodeSmell> smells, File file){
        this.smells = smells;
        this.file = file;
    }
    @Override
    public boolean visit(VariableDeclarationStatement node){
        Type tNode = node.getType();
        System.out.println("visit a variableDeclarationStatement:" + node.toString());
        if(tNode.isParameterizedType() && ((ParameterizedType)tNode).getType().isSimpleType()){
            //assert false;
            ParameterizedType pNode = (ParameterizedType)tNode;
            SimpleType sNode = (SimpleType)pNode.getType();
            if("HashMap".equals(sNode.getName().toString())){
                List<Type> typeParam = pNode.typeArguments();
                if(typeParam.get(0).isSimpleType() && typeParam.get(1).isSimpleType()){
                    SimpleType firParam = (SimpleType)typeParam.get(0);
                    if("Integer".equals(firParam.getName().toString())){
                        System.out.println("***find HM Smell***");
                        smells.add(new HMSmell(node, file));
                    }
                }
            }
        }
        return true;
    }

}
