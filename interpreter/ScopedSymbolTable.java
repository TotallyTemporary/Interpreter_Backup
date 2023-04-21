package interpreter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import AST.classes.ClassDeclarationNode;
import AST.functions.FunctionDefNode;

public class ScopedSymbolTable {
    
    public static int
        INTEGER_TYPE,
        FLOAT_TYPE,
        BOOLEAN_TYPE,
        STRING_TYPE;

    private int level;
    private ScopedSymbolTable parent;


    private HashMap<Integer, Symbol> table = new HashMap<Integer, Symbol>(); /* ints and floats and the like */
    private HashMap<Integer, FunctionDefNode> funcTable = new HashMap<Integer, FunctionDefNode>(); /* functions */
    private HashMap<Integer, ClassDeclarationNode> classTable = new HashMap<Integer, ClassDeclarationNode>(); /* classes */

    /* global scope */
    public ScopedSymbolTable() throws SemanticsException, CustomRuntimeException, ReturnEvent {
        level = 1;
        parent = null;

        /* Define built-in types */
        INTEGER_TYPE = InterpreterUtil.makeBuiltinType(this, "Integer");
        FLOAT_TYPE = InterpreterUtil.makeBuiltinType(this, "Float");
        BOOLEAN_TYPE = InterpreterUtil.makeBuiltinType(this, "Boolean");
        STRING_TYPE = InterpreterUtil.makeBuiltinType(this, "String");

        /* Define built-in variables */
        InterpreterUtil.makeBuiltinVar(this, "False", BOOLEAN_TYPE, 0);
        InterpreterUtil.makeBuiltinVar(this, "True", BOOLEAN_TYPE, 1);

        InterpreterUtil.makeFunctions(this);
    }

    public ScopedSymbolTable(ScopedSymbolTable parent) throws SemanticsException {
        this.parent = parent;
        this.level = parent.level+1;
    }

    public int define(int id, Symbol symbol) {
        table.put(id, symbol);
        return id;
    }

    public void defineFunction(int id, FunctionDefNode node) {
        funcTable.put(id, node);
    }

    public void defineClass(int id, ClassDeclarationNode node) {
        classTable.put(id, node);
    }

    public Symbol shortLookup(int id) {
        Symbol s = table.get(id);

        return s;
    }

    public Symbol lookup(int id) {
        Symbol s = table.get(id);
        
        if (s != null) {
            return s;
        }

        if (parent != null) {
            return parent.lookup(id);
        }

        return null;
    }

    public Symbol lookup(String name) {
        Integer id = idLookup.get(name);

        if (id == null) {
            return null;
        }

        return lookup(id);
    }

    public FunctionDefNode lookupFunc(int id) {
        FunctionDefNode node = funcTable.get(id);

        if (node != null) {
            return node;
        }

        if (parent != null) {
            return parent.lookupFunc(id);
        }

        return null;
    }

    public FunctionDefNode lookupFunc(String name) {
        Integer id = idLookup.get(name);

        if (id == null) {
            return null;
        }

        return lookupFunc(id);
    }

    public ClassDeclarationNode lookupClass(int id) {
        ClassDeclarationNode node = classTable.get(id);

        if (node != null) {
            return node;
        }

        if (parent != null) {
            return parent.lookupClass(id);
        }

        return null;
    }

    public ClassDeclarationNode lookupClass(String name) {
        Integer id = idLookup.get(name);

        if (id == null) {
            return null;
        }

        return lookupClass(id);
    }


    /* Static stuff  below 
        this doesn't depend on scope, it's to assign IDs to variable names and stuff.
    */

    /* Assign ID to variable name */
    private static HashMap<String, Integer> idLookup = new HashMap<String, Integer>();

    /* Assign ID to static string */
    private static HashMap<String, Integer> stringTable = new HashMap<String, Integer>();


    private static int idCounter = 1;
    private static int stringCounter = 1;

    public static void clean() {
        idCounter = 1;
        stringCounter = 1;
        idLookup.clear();
        stringTable.clear();
    }

    public static int getID(String name) {
        /* If symbol with that name exists, return its id */
        if (idLookup.containsKey(name)) {
            return idLookup.get(name);
        }

        /* If that symbol doesn't exist, make up a new ID for it */
        int value = idCounter++;

        idLookup.put(name, value);
        return value;
    }

    public static int getStringID(String staticString) {
        if (stringTable.containsKey(staticString)) {
            return stringTable.get(staticString);
        }

        int value = stringCounter++;

        stringTable.put(staticString, value);
        return value;
    }

    public static String getString(int id) {
        Iterator<Entry<String, Integer>> it = stringTable.entrySet().iterator();

        while (it.hasNext()) {
            Entry<String, Integer> entry = it.next();
            if (entry.getValue() == id) {
                return entry.getKey();
            }
        }

        return null;
    }

    public static String getName(int id) {
        Iterator<Entry<String, Integer>> it = idLookup.entrySet().iterator();

        while (it.hasNext()) {
            Entry<String, Integer> entry = it.next();

            if (entry.getValue() == id) {
                return entry.getKey();
            }
        }

        return "???";
    }

    /*
        private HashMap<Integer, Symbol> table = new HashMap<Integer, Symbol>();
        private HashMap<Integer, FunctionNode> funcTable = new HashMap<Integer, FunctionNode>();
        private HashMap<Integer, ClassDeclarationNode> classTable = new HashMap<Integer, ClassDeclarationNode>();
    

    */

    public void displayContents() {
        System.out.println("symbol table level " + level);
        {
            System.out.println("vars");
            var it = table.entrySet().iterator();
            while (it.hasNext()) {
                var e = it.next();
                System.out.println(e.getValue() + "=" + ScopedSymbolTable.getName(e.getKey()));
            }
        }

        {
            System.out.println("funcs");
            var it = funcTable.entrySet().iterator();
            while (it.hasNext()) {
                var e = it.next();
                System.out.println(e.getKey() + " :" + e.getValue() + "=" + ScopedSymbolTable.getName(e.getKey()));
            }
        }

        {
            System.out.println("classes");
            var it = classTable.entrySet().iterator();
            while (it.hasNext()) {
                var e = it.next();
                System.out.println(e.getKey() + " :" + e.getValue());
            }
        }

        if (parent != null) parent.displayContents();
    }

    public static void displayStaticContents() {
        System.out.println("id table: ");
        Iterator<Entry<String, Integer>> it = idLookup.entrySet().iterator();

        while (it.hasNext()) {
            Entry<String, Integer> entry = it.next();

            System.out.println(entry.getKey() + " = " + entry.getValue());
        }

        System.out.println("string table: ");
        var it2 = stringTable.entrySet().iterator();

        while (it2.hasNext()) {
            var entry = it2.next();

            System.out.println(entry.getKey() + " :" + entry.getValue());
        }
    }
}   