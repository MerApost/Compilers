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

    @Override
    public String visit(ClassExtendsDeclaration n, Void argu) throws Exception {
        String classname = n.f1.accept(this, null);
        currentClass = symbolTable.getClass(classname);

        n.f5.accept(this, argu);
        n.f6.accept(this, argu);

        currentClass = null;
        return null;
    }

    //VarDeclaration

    @Override
    public String visit(MethodDeclaration n, Void argu) throws Exception {
        String methodName = n.f2.accept(this, null);
        currentMethod = currentClass.getMethod(methodName);

        n.f4.accept(this, null); // Parameters
        n.f7.accept(this, null); // Local variables
        n.f8.accept(this, null); // Statements

        n.f10.accept(this, null);

        currentMethod = null;
        return null;
    }

    
}
