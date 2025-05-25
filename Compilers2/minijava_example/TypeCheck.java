import syntaxtree.*;
import visitor.*;

public class TypeCheck extends GJDepthFirst<String, Void>{
    private SymbolTable symbolTable;
    private SymbolTable.ClassSymbol currentClass = null;
    private SymbolTable.MethodSymbol currentMethod = null;

    public TypeCheck(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    private String getVariableType(String varName) {
        if (currentMethod != null) {
            SymbolTable.VariableInfo var = currentMethod.getVar(varName);
            if (var != null) return var.type;
        }
        if (currentClass != null) {
            SymbolTable.VariableInfo field = currentClass.getField(varName);
            if (field != null) return field.type;
        }
        return null;
    }

    private boolean isValidClass(String type) {
        return symbolTable.getClass(type) != null;
    }

    private boolean isCompatible(String expected, String actual) {
        if (expected.equals(actual)) return true;
        if (isValidClass(actual) && isValidClass(expected)) {
            SymbolTable.ClassSymbol actualClass = symbolTable.getClass(actual);
            while (actualClass != null) {
                if (actualClass.name.equals(expected)) return true;
                actualClass = actualClass.superClass;
            }
        }
        return false;
    }

///////////////////////////////////////////////////////////

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

        n.f4.accept(this, null);
        n.f7.accept(this, null);
        n.f8.accept(this, null);

        n.f10.accept(this, null);

        currentMethod = null;
        return null;
    }

    @Override
    public String visit(AssignmentStatement n, Void argu) throws Exception {
        String varName = n.f0.accept(this, argu);
        String exprType = n.f2.accept(this, argu);

        String varType = getVariableType(varName);
        if (varType == null) {
            throw new Exception("Type error: Undefined variable " + varName);
        }

        if (!isCompatible(varType, exprType)) {
            throw new Exception("Type error: Can't assign " + exprType + " to " + varType);
        }

        return null;
    }

    
}
