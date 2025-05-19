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
    }

   // public Map<String, ClassInfo> classes = new LinkedHashMap<>();
    
}
