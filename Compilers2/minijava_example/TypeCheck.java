import syntaxtree.*;
import visitor.*;

public class TypeCheck extends GJDepthFirst<String, Void>{
    private SymbolTable symbolTable;
    private SymbolTable.ClassSymbol currentClass = null;
    private SymbolTable.MethodSymbol currentMethod = null;

    public TypeCheck(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    
}
