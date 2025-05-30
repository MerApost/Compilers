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
            if (var != null) {
                System.out.println("DEBUG: getVariableType " + varName + " in method: " + var.type);
                return var.type;
            }
        }
        if (currentClass != null) {
            SymbolTable.VariableInfo field = currentClass.getField(varName);
            if (field != null) {
                System.out.println("DEBUG: getVariableType " + varName + " in class: " + field.type);
                return field.type;
            }
        }
        System.out.println("DEBUG: getVariableType " + varName + " not found");
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
        String classname = n.f1.f0.toString();
        currentClass = symbolTable.getClass(classname);
        currentMethod = currentClass.getMethod("main");

        n.f14.accept(this, null);
        n.f15.accept(this, null);

        currentMethod = null;
        currentClass = null;
        return null;
    }

    @Override
    public String visit(TypeDeclaration n, Void argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    @Override
    public String visit(ClassDeclaration n, Void argu) throws Exception {
        String classname = n.f1.f0.toString();
        currentClass = symbolTable.getClass(classname);

        n.f3.accept(this, argu);
        n.f4.accept(this, argu);

        currentClass = null;
        return null;
    }

    @Override
    public String visit(ClassExtendsDeclaration n, Void argu) throws Exception {
        String classname = n.f1.f0.toString();
        currentClass = symbolTable.getClass(classname);

        n.f5.accept(this, argu);
        n.f6.accept(this, argu);

        currentClass = null;
        return null;
    }

    @Override
    public String visit(VarDeclaration n, Void argu) throws Exception {
        String type = n.f0.accept(this, null);
        if (!type.equals("int") && !type.equals("boolean") && !type.equals("int[]") && !type.equals("boolean[]") && !isValidClass(type)) {
            throw new Exception("Type error: Undefined type " + type);
        }
        return null;
    }

    @Override
    public String visit(MethodDeclaration n, Void argu) throws Exception {
        String methodName = n.f2.f0.toString();
        currentMethod = currentClass.getMethod(methodName);

        n.f4.accept(this, null);
        n.f7.accept(this, null);
        n.f8.accept(this, null);

        n.f10.accept(this, null);

        currentMethod = null;
        return null;
    }

    @Override
    public String visit(Type n, Void argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    @Override
    public String visit(ArrayType n, Void argu) {
        return "int[]";
    }

    @Override
    public String visit(BooleanArrayType n, Void argu) {
        return "boolean[]";
    }

    @Override
    public String visit(IntegerArrayType n, Void argu) {
        return "int[]";
    }

    @Override
    public String visit(BooleanType n, Void argu) {
        return "boolean";
    }

    @Override
    public String visit(IntegerType n, Void argu) {
        return "int";
    }

    @Override
    public String visit(Statement n, Void argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    @Override
    public String visit(Block n, Void argu) throws Exception {
        n.f1.accept(this, argu);
        return null;
    }

    @Override
    public String visit(AssignmentStatement n, Void argu) throws Exception {
        String varName = n.f0.f0.toString();
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

    @Override
    public String visit(ArrayAssignmentStatement n, Void argu) throws Exception {
        String varName = n.f0.f0.toString();
        String indexType = n.f2.accept(this, argu);
        String valueType = n.f5.accept(this, argu);

        String varType = getVariableType(varName);
        if (varType == null) {
            throw new Exception("Type error: Undefined variable " + varName);
        }

        if (!varType.endsWith("[]")) {
            throw new Exception("Type error: " + varName + " is not an array");
        }

        if (!indexType.equals("int")) {
            throw new Exception("Type error: Array index must be int, u got " + indexType);
        }

        return null;
    }

    @Override
    public String visit(IfStatement n, Void argu) throws Exception {
        String conditionType = n.f2.accept(this, argu);
        
        if (!conditionType.equals("boolean")) {
            throw new Exception("Type error: If condition must be boolean, got " + conditionType);
        }
        n.f4.accept(this, argu);
        n.f6.accept(this, argu);

        return null;
    }

    @Override
    public String visit(WhileStatement n, Void argu) throws Exception {
        String conditionType = n.f2.accept(this, argu);
        
        if (!conditionType.equals("boolean")) {
            throw new Exception("Type error: While condition must be boolean, got " + conditionType);
        }

        n.f4.accept(this, argu);

        return null;
    }

    @Override
    public String visit(PrintStatement n, Void argu) throws Exception {
        String exprType = n.f2.accept(this, argu);

        if (!exprType.equals("int")) {
            throw new Exception("Type error: System.out.println expects int, got " + exprType);
        }

        return null;
    }

    @Override
    public String visit(Expression n, Void argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    @Override
    public String visit(AndExpression n, Void argu) throws Exception {
        String leftType = n.f0.accept(this, argu);
        String rightType = n.f2.accept(this, argu);

        if (!leftType.equals("boolean") || !rightType.equals("boolean")) {
            throw new Exception("Type error: && operator requires boolean operands");
        }

        return "boolean";
    }

    @Override
    public String visit(CompareExpression n, Void argu) throws Exception {
        String leftType = n.f0.accept(this, argu);
        String rightType = n.f2.accept(this, argu);

        if (!leftType.equals("int") || !rightType.equals("int")) {
            throw new Exception("Type error: < operator requires int operands");
        }

        return "boolean";
    }

    @Override
    public String visit(PlusExpression n, Void argu) throws Exception {
        String leftType = n.f0.accept(this, argu);
        String rightType = n.f2.accept(this, argu);

        System.out.println("DEBUG: + left=" + leftType + ", right=" + rightType); ///

        if (!leftType.equals("int") || !rightType.equals("int")) {
            throw new Exception("Type error: + operator requires int operands");
        }

        return "int";
    }

    @Override
    public String visit(MinusExpression n, Void argu) throws Exception {
        String leftType = n.f0.accept(this, argu);
        String rightType = n.f2.accept(this, argu);

        if (!leftType.equals("int") || !rightType.equals("int")) {
            throw new Exception("Type error: - operator requires int operands");
        }

        return "int";
    }

    @Override
    public String visit(TimesExpression n, Void argu) throws Exception {
        String leftType = n.f0.accept(this, argu);
        String rightType = n.f2.accept(this, argu);

        if (!leftType.equals("int") || !rightType.equals("int")) {
            throw new Exception("Type error: * operator requires int operands");
        }

        return "int";
    }

    @Override
    public String visit(ArrayLookup n, Void argu) throws Exception {
        String arrayType = n.f0.accept(this, argu);
        String indexType = n.f2.accept(this, argu);

        if (!arrayType.endsWith("[]")) {
            throw new Exception("Type error: Array lookup on non-array type " + arrayType);
        }

        if (!indexType.equals("int")) {
            throw new Exception("Type error: Array index must be int, got " + indexType);
        }

        return "int";
    }

    @Override
    public String visit(ArrayLength n, Void argu) throws Exception {
        String arrayType = n.f0.accept(this, argu);

        if (!arrayType.endsWith("[]")) {
            throw new Exception("Type error: length on non-array type " + arrayType);
        }
        return "int";

    }

    @Override
    public String visit(MessageSend n, Void argu) throws Exception {
        String objectType = n.f0.accept(this, argu);
        String methodName = n.f2.f0.toString();

        SymbolTable.ClassSymbol objectClass = symbolTable.getClass(objectType);
        if (objectClass == null) {
            throw new Exception("Type error: " + objectType + " is not a valid class");
        }

        SymbolTable.MethodSymbol method = objectClass.getMethod(methodName);
        if (method == null) {
            throw new Exception("Type error: Method " + methodName + " not found in class " + objectType);
        }


        return method.returnType;
    }

    @Override
    public String visit(ExpressionList n, Void argu) throws Exception {
        String expr = n.f0.accept(this, argu);
        String tail = n.f1.accept(this, argu);
        return expr + tail;
    }

    @Override
    public String visit(ExpressionTail n, Void argu) throws Exception {
        String result = "";
        for (Node node : n.f0.nodes) {
            result += node.accept(this, argu);
        }
        return result;
    }

    @Override
    public String visit(ExpressionTerm n, Void argu) throws Exception {
        String expr = n.f1.accept(this, argu);
        return ", " + expr;
    }

    @Override
    public String visit(Clause n, Void argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    public String visit(PrimaryExpression n, Void argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    @Override
    public String visit(IntegerLiteral n, Void argu) throws Exception {
        return "int";
    }

    @Override
    public String visit(TrueLiteral n, Void argu) throws Exception {
        return "boolean";
    }

    @Override
    public String visit(FalseLiteral n, Void argu) throws Exception {
        return "boolean";
    }

    @Override
    public String visit(Identifier n, Void argu) throws Exception {
        String name = n.f0.toString();
        System.out.println("DEBUG: Processing identifier: " + name);

        if (name.equals("int") || name.equals("boolean")) {
            System.out.println("DEBUG: " + name + " is a built-in type");
            return name;
        }
        if (isValidClass(name)) {
            System.out.println("DEBUG: " + name + " is a valid class");
            return name;
        }
        String type = getVariableType(name);
        if (type == null) {
            System.out.println("DEBUG: Identifier " + name + " is undefined in current scope.");
            throw new Exception("Type error: Undefined variable " + name);
        }
        System.out.println("DEBUG: " + name + " resolved to type " + type);
        return type;
    }

    @Override
    public String visit(ThisExpression n, Void argu) throws Exception {
        if (currentClass == null) {
            throw new Exception("Type error: 'this' outside of class");
        }
        return currentClass.name;
    }

    @Override
    public String visit(ArrayAllocationExpression n, Void argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    @Override
    public String visit(BooleanArrayAllocationExpression n, Void argu) throws Exception {
        String sizeType = n.f3.accept(this, argu);
        if (!sizeType.equals("int")) {
            throw new Exception("Type error: Array size must be int, got " + sizeType);
        }
        return "boolean[]";
    }


    @Override
    public String visit(IntegerArrayAllocationExpression n, Void argu) throws Exception {
        String sizeType = n.f3.accept(this, argu);
        if (!sizeType.equals("int")) {
            throw new Exception("Type error: Array size must be int, got " + sizeType);
        }
        return "int[]";
    }

    @Override
    public String visit(AllocationExpression n, Void argu) throws Exception {
        String className = n.f1.accept(this, argu);
        
        if (!isValidClass(className)) {
            throw new Exception("Type error: Undefined class " + className);
        }
        return className;
    }

    @Override
    public String visit(NotExpression n, Void argu) throws Exception {
        String clauseType = n.f1.accept(this, argu);
        if (!clauseType.equals("boolean")) {
            throw new Exception("Type error: ! operator requires boolean operand");
        }

        return "boolean";
    }

    @Override
    public String visit(BracketExpression n, Void argu) throws Exception {
        return n.f1.accept(this, argu);
    }

    @Override
    public String visit(Goal n, Void argu) throws Exception {
        System.out.println("Starting type checking:");
        
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        
        System.out.println("Type checking completed successfully!!");
        return null;
    }
}
