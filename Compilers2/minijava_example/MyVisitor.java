import syntaxtree.*;
import visitor.*;

public class MyVisitor extends GJDepthFirst<String, Void> {
    public SymbolTable symbolTable = new SymbolTable();

    private SymbolTable.ClassSymbol currentClass = null;
    private SymbolTable.MethodSymbol currentMethod = null;

// class MyVisitor extends GJDepthFirst<String, Void>{
    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "{"
     * f3 -> "public"
     * f4 -> "static"
     * f5 -> "void"
     * f6 -> "main"
     * f7 -> "("
     * f8 -> "String"
     * f9 -> "["
     * f10 -> "]"
     * f11 -> Identifier()
     * f12 -> ")"
     * f13 -> "{"
     * f14 -> ( VarDeclaration() )*
     * f15 -> ( Statement() )*
     * f16 -> "}"
     * f17 -> "}"
     */
    @Override
    public String visit(MainClass n, Void argu) throws Exception {
        String classname = n.f1.accept(this, null);
        SymbolTable.ClassSymbol mainClass = new SymbolTable.ClassSymbol(classname, null);
        symbolTable.putClass(classname, mainClass);
        currentClass = mainClass;
        
        //System.out.println("Class: " + classname);
        SymbolTable.MethodSymbol mainMethod = new SymbolTable.MethodSymbol("main", "void", mainClass);
        mainClass.putMethod("main", mainMethod);
        currentMethod = mainMethod;
        mainMethod.putParameter(n.f11.accept(this, null), "String[]");
        n.f14.accept(this, argu); 
    
        //super.visit(n, argu);

        //System.out.println();
        currentMethod = null;
        currentClass = null;

        return null;
    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "{"
     * f3 -> ( VarDeclaration() )*
     * f4 -> ( MethodDeclaration() )*
     * f5 -> "}"
     */
    @Override
    public String visit(ClassDeclaration n, Void argu) throws Exception {
    String classname = n.f1.accept(this, null);
    SymbolTable.ClassSymbol classSymbol = new SymbolTable.ClassSymbol(classname, null);
    symbolTable.putClass(classname, classSymbol);
    currentClass = classSymbol;

    n.f3.accept(this, argu); // fields
    n.f4.accept(this, argu); // methods

    currentClass = null;
    return null;
}

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "extends"
     * f3 -> Identifier()
     * f4 -> "{"
     * f5 -> ( VarDeclaration() )*
     * f6 -> ( MethodDeclaration() )*
     * f7 -> "}"
     */
    @Override
    public String visit(ClassExtendsDeclaration n, Void argu) throws Exception {
        //n.f0.accept(this, argu);

        String classname = n.f1.accept(this, null);
        String parentName = n.f3.accept(this, null);
        SymbolTable.ClassSymbol parent = symbolTable.getClass(parentName);
        if (parent == null) {
            throw new Exception("Undefined superclass: " + parentName);
        }
        //System.out.println("Class: " + classname);

        // n.f2.accept(this, argu);
        // n.f3.accept(this, argu);
        // n.f4.accept(this, argu);
        // System.out.println("Fields: ");
        // 
        // System.out.println("Methods: ");
        // 
        // n.f7.accept(this, argu);

        // System.out.println();
        SymbolTable.ClassSymbol classSymbol = new SymbolTable.ClassSymbol(classname, parent);
        symbolTable.putClass(classname, classSymbol);
        currentClass = classSymbol;
        n.f5.accept(this, argu);
        n.f6.accept(this, argu);
        currentClass = null;

        return null;
    }

    /**
    * f0 -> Type()
    * f1 -> Identifier()
    * f2 -> ";"
    */
   public String visit(VarDeclaration n, Void argu) throws Exception {
        //String _ret=null;
        String type = n.f0.accept(this, null);
        String var = n.f1.accept(this, null);
        // System.out.println(var + " " + type);
        //super.visit(n, argu);
        if (currentMethod != null) {
            currentMethod.putLocalVar(var, type);
        } else if (currentClass != null) {
            currentClass.putField(var, type);
        }
        
        return null;
    }

    /**
     * f0 -> "public"
     * f1 -> Type()
     * f2 -> Identifier()
     * f3 -> "("
     * f4 -> ( FormalParameterList() )?
     * f5 -> ")"
     * f6 -> "{"
     * f7 -> ( VarDeclaration() )*
     * f8 -> ( Statement() )*
     * f9 -> "return"
     * f10 -> Expression()
     * f11 -> ";"
     * f12 -> "}"
     */
    @Override
    public String visit(MethodDeclaration n, Void argu) throws Exception {
        //String argumentList = n.f4.present() ? n.f4.accept(this, null) : "";

        String myType = n.f1.accept(this, null);
        String myName = n.f2.accept(this, null);

        SymbolTable.MethodSymbol methodSymbol = new SymbolTable.MethodSymbol(myName, myType, currentClass);
        currentClass.putMethod(myName, methodSymbol);
        currentMethod = methodSymbol;
        n.f4.accept(this, null);
        n.f7.accept(this, null);


        // System.out.println("Method: " + myType + " " + myName + " (" + argumentList + ")");
        // System.out.println("Local vars:");

        // super.visit(n, argu);
        currentMethod = null;
        return null;
    }

    /**
     * f0 -> FormalParameter()
     * f1 -> FormalParameterTail()
     */
    @Override
    public String visit(FormalParameterList n, Void argu) throws Exception {
        String ret = n.f0.accept(this, null);

        if (n.f1 != null) {
            ret += n.f1.accept(this, null);
        }

        return ret;
    }

    /**
     * f0 -> FormalParameter()
     * f1 -> FormalParameterTail()
     */
    public String visit(FormalParameterTerm n, Void argu) throws Exception {
        return n.f1.accept(this, argu);
    }

    /**
     * f0 -> ","
     * f1 -> FormalParameter()
     */
    @Override
    public String visit(FormalParameterTail n, Void argu) throws Exception {
        String ret = "";
        for ( Node node: n.f0.nodes) {
            ret += ", " + node.accept(this, null);
        }

        return ret;
    }

    /**
     * f0 -> Type()
     * f1 -> Identifier()
     */
    @Override
    public String visit(FormalParameter n, Void argu) throws Exception{
        String type = n.f0.accept(this, null);
        String name = n.f1.accept(this, null);
        if (currentMethod != null) {
            currentMethod.putParameter(name, type);
        }
        return null;
        //return type + " " + name;
    }

    @Override
    public String visit(ArrayType n, Void argu) {
        return "int[]";
    }

    public String visit(BooleanType n, Void argu) {
        return "boolean";
    }

    public String visit(IntegerType n, Void argu) {
        return "int";
    }

    @Override
    public String visit(Identifier n, Void argu) {
        return n.f0.toString();
    }
    
    /**
     * f0 -> "boolean"
     * f1 -> "["
     * f2 -> "]"
     */
    @Override
    public String visit(BooleanArrayType n, Void argu) {
        return "boolean[]";
    }

    /**
     * f0 -> "int"
     * f1 -> "["
     * f2 -> "]"
     */
    @Override
    public String visit(IntegerArrayType n, Void argu) {
        return "int[]";
    }

    /**
     * f0 -> Block()
     *       | AssignmentStatement()
     *       | ArrayAssignmentStatement()
     *       | IfStatement()
     *       | WhileStatement()
     *       | PrintStatement()
     */
    @Override
    public String visit(Statement n, Void argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    /**
     * f0 -> "{"
     * f1 -> ( Statement() )*
     * f2 -> "}"
     */
    @Override
    public String visit(Block n, Void argu) throws Exception {
        String _ret = null;
        n.f0.accept(this, argu);
        System.out.println("Block start");
        n.f1.accept(this, argu);
        System.out.println("Block end");
        n.f2.accept(this, argu);
        
        return _ret;
    }

    /**
     * f0 -> Identifier()
     * f1 -> "="
     * f2 -> Expression()
     * f3 -> ";"
     */
    @Override
    public String visit(AssignmentStatement n, Void argu) throws Exception {
        String _ret = null;
        String id = n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        
        String expr = n.f2.accept(this, argu);
        System.out.println("Assignment: " + id + " = " + expr);
        
        n.f3.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> Identifier()
     * f1 -> "["
     * f2 -> Expression()
     * f3 -> "]"
     * f4 -> "="
     * f5 -> Expression()
     * f6 -> ";"
     */
    @Override
    public String visit(ArrayAssignmentStatement n, Void argu) throws Exception {
        String _ret = null;
        String id = n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        
        String indexExpr = n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        
        String valueExpr = n.f5.accept(this, argu);
        System.out.println("Array Assignment: " + id + "[" + indexExpr + "] = " + valueExpr);
        
        n.f6.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "if"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> Statement()
     * f5 -> "else"
     * f6 -> Statement()
     */
    @Override
    public String visit(IfStatement n, Void argu) throws Exception {
        String _ret = null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        
        String condition = n.f2.accept(this, argu);
        System.out.println("If condition: " + condition);
        
        n.f3.accept(this, argu);
        System.out.println("If body:");
        n.f4.accept(this, argu);
        
        n.f5.accept(this, argu);
        System.out.println("Else body:");
        n.f6.accept(this, argu);
        
        return _ret;
    }

    /**
     * f0 -> "while"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> Statement()
     */
    @Override
    public String visit(WhileStatement n, Void argu) throws Exception {
        String _ret = null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        
        String condition = n.f2.accept(this, argu);
        System.out.println("While condition: " + condition);
        
        n.f3.accept(this, argu);
        System.out.println("While body:");
        n.f4.accept(this, argu);
        
        return _ret;
    }

    /**
     * f0 -> "System.out.println"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> ";"
     */
    @Override
    public String visit(PrintStatement n, Void argu) throws Exception {
        String _ret = null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        
        String expr = n.f2.accept(this, argu);
        System.out.println("Print: " + expr);
        
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        
        return _ret;
    }

        /**
     * f0 -> AndExpression()
     *       | CompareExpression()
     *       | PlusExpression()
     *       | MinusExpression()
     *       | TimesExpression()
     *       | ArrayLookup()
     *       | ArrayLength()
     *       | MessageSend()
     *       | Clause()
     */
    @Override
    public String visit(Expression n, Void argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    /**
     * f0 -> Clause()
     * f1 -> "&&"
     * f2 -> Clause()
     */
    @Override
    public String visit(AndExpression n, Void argu) throws Exception {
        String leftClause = n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        String rightClause = n.f2.accept(this, argu);
        
        return leftClause + " && " + rightClause;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "<"
     * f2 -> PrimaryExpression()
     */
    @Override
    public String visit(CompareExpression n, Void argu) throws Exception {
        String leftExpr = n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        String rightExpr = n.f2.accept(this, argu);
        
        return leftExpr + " < " + rightExpr;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "+"
     * f2 -> PrimaryExpression()
     */
    @Override
    public String visit(PlusExpression n, Void argu) throws Exception {
        String leftExpr = n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        String rightExpr = n.f2.accept(this, argu);
        
        return leftExpr + " + " + rightExpr;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "-"
     * f2 -> PrimaryExpression()
     */
    @Override
    public String visit(MinusExpression n, Void argu) throws Exception {
        String leftExpr = n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        String rightExpr = n.f2.accept(this, argu);
        
        return leftExpr + " - " + rightExpr;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "*"
     * f2 -> PrimaryExpression()
     */
    @Override
    public String visit(TimesExpression n, Void argu) throws Exception {
        String leftExpr = n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        String rightExpr = n.f2.accept(this, argu);
        
        return leftExpr + " * " + rightExpr;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "["
     * f2 -> PrimaryExpression()
     * f3 -> "]"
     */
    @Override
    public String visit(ArrayLookup n, Void argu) throws Exception {
        String arrayExpr = n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        String indexExpr = n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        
        return arrayExpr + "[" + indexExpr + "]";
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "."
     * f2 -> "length"
     */
    @Override
    public String visit(ArrayLength n, Void argu) throws Exception {
        String arrayExpr = n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        
        return arrayExpr + ".length";
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "."
     * f2 -> Identifier()
     * f3 -> "("
     * f4 -> ( ExpressionList() )?
     * f5 -> ")"
     */
    @Override
    public String visit(MessageSend n, Void argu) throws Exception {
        String objectExpr = n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        String methodName = n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        
        String arguments = n.f4.present() ? n.f4.accept(this, argu) : "";
        n.f5.accept(this, argu);
        
        System.out.println("Method call: " + objectExpr + "." + methodName + "(" + arguments + ")");
        
        return objectExpr + "." + methodName + "(" + arguments + ")";
    }

    /**
     * f0 -> Expression()
     * f1 -> ExpressionTail()
     */
    @Override
    public String visit(ExpressionList n, Void argu) throws Exception {
        String expr = n.f0.accept(this, argu);
        String tail = n.f1.accept(this, argu);
        
        return expr + tail;
    }

    /**
     * f0 -> ( ExpressionTerm() )*
     */
    @Override
    public String visit(ExpressionTail n, Void argu) throws Exception {
        String result = "";
        
        for (Node node : n.f0.nodes) {
            result += node.accept(this, argu);
        }
        
        return result;
    }

    /**
     * f0 -> ","
     * f1 -> Expression()
     */
    @Override
    public String visit(ExpressionTerm n, Void argu) throws Exception {
        n.f0.accept(this, argu);
        String expr = n.f1.accept(this, argu);
        
        return ", " + expr;
    }

    /**
     * f0 -> NotExpression()
     *       | PrimaryExpression()
     */
    @Override
    public String visit(Clause n, Void argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    /**
     * f0 -> IntegerLiteral()
     *       | TrueLiteral()
     *       | FalseLiteral()
     *       | Identifier()
     *       | ThisExpression()
     *       | ArrayAllocationExpression()
     *       | AllocationExpression()
     *       | BracketExpression()
     */
    @Override
    public String visit(PrimaryExpression n, Void argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    /**
     * f0 -> <INTEGER_LITERAL>
     */
    @Override
    public String visit(IntegerLiteral n, Void argu) throws Exception {
        return n.f0.toString();
    }

    /**
     * f0 -> "true"
     */
    @Override
    public String visit(TrueLiteral n, Void argu) throws Exception {
        return "true";
    }

    /**
     * f0 -> "false"
     */
    @Override
    public String visit(FalseLiteral n, Void argu) throws Exception {
        return "false";
    }

    /**
     * f0 -> "this"
     */
    @Override
    public String visit(ThisExpression n, Void argu) throws Exception {
        return "this";
    }

    /**
     * f0 -> BooleanArrayAllocationExpression()
     *       | IntegerArrayAllocationExpression()
     */
    @Override
    public String visit(ArrayAllocationExpression n, Void argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    /**
     * f0 -> "new"
     * f1 -> "boolean"
     * f2 -> "["
     * f3 -> Expression()
     * f4 -> "]"
     */
    @Override
    public String visit(BooleanArrayAllocationExpression n, Void argu) throws Exception {
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        
        String sizeExpr = n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        
        return "new boolean[" + sizeExpr + "]";
    }

    /**
     * f0 -> "new"
     * f1 -> "int"
     * f2 -> "["
     * f3 -> Expression()
     * f4 -> "]"
     */
    @Override
    public String visit(IntegerArrayAllocationExpression n, Void argu) throws Exception {
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        
        String sizeExpr = n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        
        return "new int[" + sizeExpr + "]";
    }

    /**
     * f0 -> "new"
     * f1 -> Identifier()
     * f2 -> "("
     * f3 -> ")"
     */
    @Override
    public String visit(AllocationExpression n, Void argu) throws Exception {
        n.f0.accept(this, argu);
        String classname = n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        
        return "new " + classname + "()";
    }

    /**
     * f0 -> "!"
     * f1 -> Clause()
     */
    @Override
    public String visit(NotExpression n, Void argu) throws Exception {
        n.f0.accept(this, argu);
        String clause = n.f1.accept(this, argu);
        
        return "!" + clause;
    }

    /**
     * f0 -> "("
     * f1 -> Expression()
     * f2 -> ")"
     */
    @Override
    public String visit(BracketExpression n, Void argu) throws Exception {
        n.f0.accept(this, argu);
        String expr = n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        
        return "(" + expr + ")";
    }






    /**
     * f0 -> MainClass()
     * f1 -> ( TypeDeclaration() )*
     * f2 -> <EOF>
     */
    @Override
    public String visit(Goal n, Void argu) throws Exception {
        System.out.println("Program start:");
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        System.out.println("Program end.");
        
        return null;
    }

    /**
     * f0 -> ClassDeclaration()
     *       | ClassExtendsDeclaration()
     */
    @Override
    public String visit(TypeDeclaration n, Void argu) throws Exception {
        return n.f0.accept(this, argu);
    }
}