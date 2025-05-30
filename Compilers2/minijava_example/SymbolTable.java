import java.util.*;

public class SymbolTable {
    private LinkedHashMap<String, ClassSymbol> classes = new LinkedHashMap<>();

    public void putClass(String name, ClassSymbol classSymbol) {
        if (classes.containsKey(name)) {
            throw new RuntimeException("Semantic error: Duplicate class '" + name + "'");
        }
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
            //System.out.println("DEBUG: Adding parameter " + name + " of type " + type + " to method " + this.name);
            if (parameters.containsKey(name))  {
                throw new RuntimeException("Semantic error: Duplicate parameter " + name + " in method " + this.name);
            }
            VariableInfo param = new VariableInfo(name, type);
            param.offset = parameters.size();
            parameters.put(name, param);
            return true;
        }

        public boolean putLocalVar(String name, String type) {
            if (localVars.containsKey(name) || parameters.containsKey(name)) {
                throw new RuntimeException("Semantic error: Duplicate variable " + name + " in method " + this.name);
            }
            VariableInfo localVar = new VariableInfo(name, type);
            localVar.offset = localVars.size();
            localVars.put(name, localVar);
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

        public int fieldOffset = 0;
        public int methodOffset = 0;

        public ClassSymbol(String name, ClassSymbol superClass) {
            this.name = name;
            this.superClass = superClass;

            if (superClass != null) {
                this.fieldOffset = getMaxFieldOffset(superClass) + 1;
                this.methodOffset = getMaxMethodOffset(superClass) + 1;
            }
        }

        private int getMaxFieldOffset(ClassSymbol classSymbol) {
            int maxOffset = -1;
            for (VariableInfo field : classSymbol.fields.values()) {
                if (field.offset > maxOffset) {
                    maxOffset = field.offset;
                }
            }
            if (classSymbol.superClass != null) {
                int superMaxOffset = getMaxFieldOffset(classSymbol.superClass);
                if (superMaxOffset > maxOffset) {
                    maxOffset = superMaxOffset;
                }
            }
            
            return maxOffset;
        }

        private int getMaxMethodOffset(ClassSymbol classSymbol) {
            int maxOffset = -1;
            for (MethodSymbol method : classSymbol.methods.values()) {
                if (method.offset > maxOffset) {
                    maxOffset = method.offset;
                }
            }
            if (classSymbol.superClass != null) {
                int superMaxOffset = getMaxMethodOffset(classSymbol.superClass);
                if (superMaxOffset > maxOffset) {
                    maxOffset = superMaxOffset;
                }
            }
            
            return maxOffset;
        }

        private int getFieldSize(String type) {
            if (type.equals("int")) return 4;
            else if (type.equals("boolean")) return 1;
            else return 8;
        }

        public boolean putField(String name, String type) {
            if (fields.containsKey(name)) {
                throw new RuntimeException("Semantic error: Duplicate field " + name + " in class " + this.name);
            }
            VariableInfo var = new VariableInfo(name, type);
            int size;
            if (type.equals("int")) size = 4;
            else if (type.equals("boolean")) size = 1;
            else size = 8;
            int lastOffset = 0;
            if (!fields.isEmpty()) {
                for (VariableInfo f : fields.values()) {
                    if (f.offset + getFieldSize(f.type) > lastOffset) {
                        lastOffset = f.offset + getFieldSize(f.type);
                    }
                }
            }
            var.offset = lastOffset;
            fields.put(name, var);
            return true;
        }

        public VariableInfo getField(String name) {
            if (fields.containsKey(name)) return fields.get(name);
            if (superClass != null) return superClass.getField(name);
            return null;
        }

        public boolean putMethod(String name, MethodSymbol method) {
            int lastOffset = 0;
            //System.out.println("DEBUG putMethod: Adding method " + name + " with " + method.parameters.size() + " parameters");
            ClassSymbol superC = this.superClass;
            while (superC != null) {
                MethodSymbol superMethod = superC.methods.get(name);
                if (superMethod != null) {
                    if (!superMethod.returnType.equals(method.returnType) || superMethod.parameters.size() != method.parameters.size()) {
                       // System.out.println("DEBUG: Overloading for " + name);
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

            if( !methods.isEmpty()) {
                for (MethodSymbol m : methods.values()) {
                    if (m.offset + 8 > lastOffset) {
                        lastOffset = m.offset + 8;
                    }
                }
            }

            method.offset = lastOffset;
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
        for (String classname : classes.keySet()) {
            ClassSymbol classSymbol = classes.get(classname);
             System.out.println("-----------Class " + classname + "-----------");
            System.out.println("--Variables---");
            
            for (String field : classSymbol.fields.keySet()) {
                VariableInfo fieldInfo = classSymbol.fields.get(field);
                System.out.println(classname + "." + field + " : " + (fieldInfo.offset));
            }
            System.out.println("---Methods---");
            for (String methodName : classSymbol.methods.keySet()) {
                MethodSymbol method = classSymbol.methods.get(methodName);
                System.out.println(classname + "." + methodName + " : "+ (method.offset));
                
            }
            System.out.println();
        }                                                                                           
    }
}