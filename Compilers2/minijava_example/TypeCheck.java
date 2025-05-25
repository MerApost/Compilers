import Compilers2.minijava_example.syntaxtree.ClassDeclaration;
import Compilers2.minijava_example.syntaxtree.MainClass;
import Compilers2.minijava_example.visitor.GJDepthFirst;
import syntaxtree.*;
import visitor.*;

public class TypeCheck extends GJDepthFirst<String, Void>{
    private SymbolTable symbolTable;
    private SymbolTable.ClassSymbol currentClass = null;
    private SymbolTable.MethodSymbol currentMethod = null;

    public TypeCheck(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    @Override
    public String visit(MainClass n, Void argu) throws Exception {
        String classname = n.f1.accept(this, null);
        currentClass = symbolTable.getClass(classname);
        currentMethod = currentClass.getMethod("main");

        n.f14.accept(this, argu);
        n.f15.accept(this, argu);

        currentMethod = null;
        currentClass = null;
        return null;
    }

    @Override
    public String visit(ClassDeclaration n, Void argu) throws Exception {
        String classname = n.f1.accept(this, null);
        currentClass = symbolTable.getClass(classname);

        n.f3.accept(this, argu);
        n.f4.accept(this, argu);

        currentClass = null;
        return null;
    }

    
}
