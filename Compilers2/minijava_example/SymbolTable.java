import java.util.*;

public class SymbolTable {
    private HashMap<String, ClassSymbol> classes = new HashMap<>();

    public void putClass(String name, ClassSymbol classSymbol) {
        classes.put(name, classSymbol);
    }

    public ClassSymbol getClass(String name) {
        return classes.get(name);
    }

    public static class VariableInfo {
        public String name;
        public String type;
        public int offset;

        public VariableInfo(String name, String type) {
            this.name = name;
            this.type = type;
            this.offset = -1;
        }
    }

    public static class MethodSymbol {
        public String name;
        public String returnType;
        public int offset;
        public LinkedHashMap<String, VariableInfo> parameters = new LinkedHashMap<>();
        public LinkedHashMap<String, VariableInfo> localVars = new LinkedHashMap<>();
        public ClassSymbol parentClass;

        public MethodSymbol(String name, String returnType, ClassSymbol parentClass) {
            this.name = name;
            this.returnType = returnType;
            this.parentClass = parentClass;
            this.offset = -1;
        }

        public boolean putParameter(String name, String type) {
            System.out.println("DEBUG: Adding parameter " + name + " of type " + type + " to method " + this.name);
            if (parameters.containsKey(name))  {
                throw new RuntimeException("Semantic error: Duplicate parameter " + name + " in method " + this.name);
            }
            parameters.put(name, new VariableInfo(name, type));
            return true;
        }

        public boolean putLocalVar(String name, String type) {
            if (localVars.containsKey(name) || parameters.containsKey(name)) {
                throw new RuntimeException("Semantic error: Duplicate variable " + name + " in method " + this.name);
            }
            localVars.put(name, new VariableInfo(name, type));
            return true;
        }

        public VariableInfo getVar(String name) {
            if (localVars.containsKey(name)) return localVars.get(name);
            if (parameters.containsKey(name)) return parameters.get(name);
            if (parentClass != null) return parentClass.getField(name);
            return null;
        }
    }

    public static class ClassSymbol {
        public String name;
        public ClassSymbol superClass;
        public LinkedHashMap<String, VariableInfo> fields = new LinkedHashMap<>();
        public LinkedHashMap<String, MethodSymbol> methods = new LinkedHashMap<>();

        public ClassSymbol(String name, ClassSymbol superClass) {
            this.name = name;
            this.superClass = superClass;
        }

        public boolean putField(String name, String type) {
            if (fields.containsKey(name)) {
                throw new RuntimeException("Semantic error: Duplicate field " + name + " in class " + this.name);
            }
            fields.put(name, new VariableInfo(name, type));
            return true;
        }

        public VariableInfo getField(String name) {
            if (fields.containsKey(name)) return fields.get(name);
            if (superClass != null) return superClass.getField(name);
            return null;
        }

        public boolean putMethod(String name, MethodSymbol method) {
            System.out.println("DEBUG putMethod: Adding method " + name + " with " + method.parameters.size() + " parameters");
            ClassSymbol superC = this.superClass;
            while (superC != null) {
                MethodSymbol superMethod = superC.methods.get(name);
                if (superMethod != null) {
                    // System.out.println("DEBUG: Found method " + name + " in superclass " + superC.name + " with " + superMethod.parameters.size() + " parameters");
                    
                    // System.out.println("DEBUG: Comparing signatures:");
                    // System.out.println("  Super return type: " + superMethod.returnType + ", This return type: " + method.returnType);
                    // System.out.println("  Super param count: " + superMethod.parameters.size() + ", This param count: " + method.parameters.size());
            


                    if (!superMethod.returnType.equals(method.returnType) || superMethod.parameters.size() != method.parameters.size()) {
                        System.out.println("DEBUG: Overloading detected for " + name);
                        throw new RuntimeException("Semantic error: Method " + name + " in class" + this.name + " overloads method in superclass " + superC.name + " (not allowed in MiniJava)");
                    }

                        //boolean flag = true;
                        int i = 0;
                        for (String paramName : superMethod.parameters.keySet()) {
                            String superType = superMethod.parameters.get(paramName).type;
                            String thisType = ((VariableInfo) method.parameters.values().toArray()[i]).type;
                            if (!superType.equals(thisType)) {
                                 throw new RuntimeException("Semantic error: Method " + name + " in class" + this.name + " overloads method in superclass " + superC.name + " (not allowed in MiniJava)");
                            }
                            i++;
                        }
                        break;
                    
                        // if (flag){
                        //     System.out.println("DEBUG: Method " + name + " overridden in class " + this.name);
                        //     break;
                        // } else {
                        //     System.out.println("ERROR: Method overloading not allowed. Method " + name + " already exists in superclass " + superC.name);
                        //     return false;
                        // }
                    // }else {
                    //     System.out.println("ERROR: Method overloading not allowed. Method " + name + " already exists in superclass " + superC.name);
                    //     return false;
                    // }
                }
                superC = superC.superClass;
            }

            if (methods.containsKey(name)) {
                System.out.println("ERROR: Method " + name + " already defined in class " + this.name);
                throw new RuntimeException("Semantic error: Method '" + name + "' already defined in class '" + this.name + "'");
            }

            methods.put(name, method);
            return true;
        }

        public MethodSymbol getMethod(String name) {
            if (methods.containsKey(name)) return methods.get(name);
            if (superClass != null) return superClass.getMethod(name);
            return null;
        }
    }
    
    public void printSymbolTable() {
        for (String className : classes.keySet()) {
            ClassSymbol classSymbol = classes.get(className);
            System.out.println("Class: " + className);
            if (classSymbol.superClass != null) {
                System.out.println("  Extends: " + classSymbol.superClass.name);
            }
            System.out.println("  Fields:");
            for (String field : classSymbol.fields.keySet()) {
                VariableInfo fieldInfo = classSymbol.fields.get(field);
                System.out.println("    " + field + ": " + fieldInfo.type);
            }
            System.out.println("  Methods:");
            for (String methodName : classSymbol.methods.keySet()) {
                MethodSymbol method = classSymbol.methods.get(methodName);
                System.out.println("    " + methodName + ": "+ method.returnType);
                if (!method.parameters.isEmpty()){
                    System.out.println("    Parameters:");
                    for (String paramName : method.parameters.keySet()) {
                        VariableInfo paramInfo = method.parameters.get(paramName);
                        System.out.println("        " + paramName + ": " + paramInfo.type);
                    }
                }
                if (!method.localVars.isEmpty()){
                    System.out.println("    Local Variables:");
                    for (String localVarName : method.localVars.keySet()) {
                        VariableInfo localVarInfo = method.localVars.get(localVarName);
                        System.out.println("        " + localVarName + ": " + localVarInfo.type);
                    }
                }
            }
        }                                                                                           
    }
}