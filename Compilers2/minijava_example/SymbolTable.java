import java.util.*;

public class SymbolTable {

    public static class ClassInfo {
        public String name;
        public String parent;
        public Map<String, String> variables = new LinkedHashMap<>();
        public Map<String, MethodInfo> methods = new LinkedHashMap<>();

        public ClassInfo(String name, String parent) {
            this.name = name;
            this.parent = parent;
        }
    }

    public static class MethodInfo {
        public String returnType;
        public Map<String, String> parameters = new LinkedHashMap<>();
        public Map<String, String> localVars = new LinkedHashMap<>();

        public MethodInfo(String returnType) {
            this.returnType = returnType;
        }

        public boolean addParameter(String name, String type) {
            if (parameters.containsKey(name)) return false;
            parameters.put(name, type);
            return true;
        }

        public boolean addLocal(String name, String type) {
            if (localVars.containsKey(name) || parameters.containsKey(name)) return false;
            localVars.put(name, type);
            return true;
        }
    }
    
    // public Map<String, ClassInfo> classes = new LinkedHashMap<>();

    private final Map<String, ClassInfo> classes = new LinkedHashMap<>();
    
    public boolean addClass(String className, String parentName) {
        if (classes.containsKey(className)) return false;
        classes.put(className, new ClassInfo(className, parentName));
        return true;
    }
    
}
