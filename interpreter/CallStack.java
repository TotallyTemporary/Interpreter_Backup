package interpreter;

import java.util.HashMap;

import AST.classes.ClassInstance;

public class CallStack {
    
    public static CallStack root = new CallStack();
    private static int instanceCounter = 1;

    private CallStack parent;
    private int level;

    public String reason;

    /* Every symbol has a value */
    private HashMap<Symbol, Float> table = new HashMap<Symbol, Float>();
    private HashMap<Integer, ClassInstance> classInstances = new HashMap<Integer, ClassInstance>();

    private CallStack() {
        this.level = 0;
    }

    public CallStack(CallStack parent) throws CustomRuntimeException {
        this.parent = parent;
        this.level = parent.level+1;

        if (this.level > 50) {
            throw new CustomRuntimeException("Maximum recursion depth reached.");
        }
    }

    public Float getValue(Symbol symbol) {
        Float value = table.get(symbol);
        if (value != null) return value;
        if (parent == null) return null;
        return parent.getValue(symbol);
    }

    private ClassInstance getInstance1(Integer id) {
        ClassInstance value = classInstances.get(id);
        if (value != null) return value;
        if (parent == null) return null;
        return parent.getInstance(id);
    }

    public ClassInstance getInstance(Integer id) {
        ClassInstance val = getInstance1(id);
        return val;
    }

    public void setValue(Symbol symbol, Float value) {
        table.put(symbol, value);
    }

    public int setInstance(ClassInstance value) {
        int id = instanceCounter++;

        classInstances.put(id, value);
        return id;
    }

    public void displayContents() {
        System.out.println("stack level " + level);
        var it = table.entrySet().iterator();
        
        System.out.println("memory:");
        while (it.hasNext()) {
            var entry = it.next();

            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        var it2 = classInstances.entrySet().iterator();

        System.out.println("objects:");
        while (it2.hasNext()) {
            var entry = it2.next();

            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        
        if (parent != null) parent.displayContents();
    }

}
