import java.util.*;

public class SymbolTable {

    public static class VariableInfo {
        public String type;

        public VariableInfo(String type) {
            this.type = type;
        }
    }

    public static class MethodInfo {
        public String returnType;
        public LinkedHashMap<String, VariableInfo> parameters = new LinkedHashMap<>();
        public LinkedHashMap<String, VariableInfo> localVars = new LinkedHashMap<>();

        public MethodInfo(String returnType) {
            this.returnType = returnType;
        }

        public boolean addParameter(String name, String type) {
            parameters.put(name, new VariableInfo(type));
        }

        public boolean addLocal(String name, String type) {
            localVars.put(name, new VariableInfo(type));
        }

        public VariableInfo getVar(String name) {
            if (localVars.containsKey(name)) return localVars.get(name);
            if (parameters.containsKey(name)) return parameters.get(name);
            return null;
        }
    }
    
    // public Map<String, ClassInfo> classes = new LinkedHashMap<>();

    private final Map<String, ClassInfo> classes = new LinkedHashMap<>();

    public boolean addClass(String className, String parentName) {
        if (classes.containsKey(className)) return false;
        classes.put(className, new ClassInfo(className, parentName));
        return true;
    }

    public boolean addField(String className, String varName, String type) {
        ClassInfo cls = classes.get(className);
        if (cls == null || cls.variables.containsKey(varName)) return false;
        cls.variables.put(varName, type);
        return true;
    }

    public boolean addMethod(String className, String methodName, String returnType) {
        ClassInfo cls = classes.get(className);
        if (cls == null || cls.methods.containsKey(methodName)) return false;
        cls.methods.put(methodName, new MethodInfo(returnType));
        return true;
    }

    public boolean addParameter(String className, String methodName, String paramName, String type) {
        MethodInfo method = getMethod(className, methodName);
        if (method == null) return false;
        return method.addParameter(paramName, type);
    }

    private MethodInfo getMethod(String className, String methodName) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getMethod'");
    }
    
}
