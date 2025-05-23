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

        public VariableInfo(String name, String type) {
            this.name = name;
            this.type = type;
        }
    }

    public static class MethodSymbol {
        public String name;
        public String returnType;
        public LinkedHashMap<String, VariableInfo> parameters = new LinkedHashMap<>();
        public LinkedHashMap<String, VariableInfo> localVars = new LinkedHashMap<>();
        public ClassSymbol parentClass;

        public MethodSymbol(String name, String returnType, ClassSymbol parentClass) {
            this.name = name;
            this.returnType = returnType;
            this.parentClass = parentClass;
        }

        public boolean putParameter(String name, String type) {
            if (parameters.containsKey(name)) return false;
            parameters.put(name, new VariableInfo(name, type));
            return true;
        }

        public boolean putLocalVar(String name, String type) {
            if (localVars.containsKey(name)) return false;
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
            if (fields.containsKey(name)) return false;
            fields.put(name, new VariableInfo(name, type));
            return true;
        }

        public VariableInfo getField(String name) {
            if (fields.containsKey(name)) return fields.get(name);
            if (superClass != null) return superClass.getField(name);
            return null;
        }

        public boolean putMethod(String name, MethodSymbol method) {
            if (methods.containsKey(name)) return false;
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