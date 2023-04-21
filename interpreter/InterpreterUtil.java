package interpreter;

import java.util.function.BiFunction;

import AST.ListNode;
import AST.Node;
import AST.NodeReturn;
import AST.classes.ClassDeclarationNode;
import AST.functions.FunctionDefNode;

public class InterpreterUtil {

    /* It's a bit ironic that none of these are helping the interpreter, they are run at semantic analysis */

    public static void makeFunctions(ScopedSymbolTable table) throws SemanticsException, CustomRuntimeException, ReturnEvent {
        /* Make System class, and $Sys variable */

        int classID = ScopedSymbolTable.getID("System");

        ScopedSymbolTable systemScope = new ScopedSymbolTable(table);

        ListNode node;
        ClassDeclarationNode classNode = new ClassDeclarationNode(classID, node = new ListNode(
            /* Define standard functions */

            /* make system constructor */
            InterpreterUtil.makeFunction(systemScope, "Construct", new Integer[] { classID }, (stack, arguments) -> {
                return new NodeReturn(classID, (int) arguments[0]);
            }),

            /* WriteFloat */
            InterpreterUtil.makeFunction(systemScope, "WriteFloat", new Integer[] { ScopedSymbolTable.FLOAT_TYPE }, (stack, arguments) -> {
                System.out.println(arguments[0]);
                return new NodeReturn(0, ScopedSymbolTable.INTEGER_TYPE);
            }),

            /* WriteString */
            InterpreterUtil.makeFunction(systemScope, "WriteString", new Integer[] { ScopedSymbolTable.STRING_TYPE }, (stack, arguments) -> {
                System.out.println(ScopedSymbolTable.getString((int) arguments[0]));
                return new NodeReturn(0, ScopedSymbolTable.INTEGER_TYPE);
            }),

            /* Input */
            InterpreterUtil.makeFunction(systemScope, "Input", new Integer[] { }, (stack, arguments) -> {
                String s = IO.scanner.next();
                int id = ScopedSymbolTable.getStringID(s);

                return new NodeReturn(id, ScopedSymbolTable.STRING_TYPE);
            }),

            /* ParseFloat */
            InterpreterUtil.makeFunction(systemScope, "ParseFloat", new Integer[] { ScopedSymbolTable.STRING_TYPE }, (stack, arguments) -> {
                String s = ScopedSymbolTable.getString((int) arguments[0]);

                float res = -1;
                try {
                    res = Float.parseFloat(s);
                } catch (NumberFormatException e) {
                    throw new CustomRuntimeException("Unable to parse float.");
                }

                return new NodeReturn(res, ScopedSymbolTable.FLOAT_TYPE);
            }),

            /* ParseInt */
            InterpreterUtil.makeFunction(systemScope, "ParseInt", new Integer[] { ScopedSymbolTable.STRING_TYPE }, (stack, arguments) -> {
                String s = ScopedSymbolTable.getString((int) arguments[0]);

                int res = -1;
                try {
                    res = Integer.parseInt(s);
                } catch (NumberFormatException e) {
                    throw new CustomRuntimeException("Unable to parse int.");
                }

                return new NodeReturn(res, ScopedSymbolTable.INTEGER_TYPE);
            })
        ));

        classNode.classSymbols = systemScope;
        node.analyze(systemScope);
        classNode.get(CallStack.root);

        /* define class */
        Symbol classSymbol = new Symbol(classID, ScopedSymbolTable.INTEGER_TYPE, SymbolType.CLASS_NAME);
        table.define(classID, classSymbol);
        table.defineClass(classID, classNode);
    }

    public static void makeBuiltinVar(ScopedSymbolTable table, String name, int type, float value) {
        /* Define (in)variable */
        int id;
        Symbol symbol = new Symbol(id = ScopedSymbolTable.getID(name), type, SymbolType.BUILT_IN_VAR);
        table.define(id, symbol);

        /* Give variable value  */
        CallStack.root.setValue(symbol, value);
    }

    public static int makeBuiltinType(ScopedSymbolTable table, String name) {
        int id;
        Symbol symbol = new Symbol(id = ScopedSymbolTable.getID(name), 0, SymbolType.BUILT_IN_TYPE);
        return table.define(id, symbol);
    }

    
    public static FunctionDefNode makeFunction(ScopedSymbolTable table,
                                            String funcName,
                                            Integer[] argumentTypes,
                                            BiFunction<CallStack, float[], NodeReturn> callback) throws SemanticsException {

        
        int argumentCount = argumentTypes.length;
        
        Symbol[] arguments = new Symbol[argumentCount];
        for (int i = 0; i < argumentCount; i++) {
            String argName = funcName + "__arg_" + i;
            int argID = ScopedSymbolTable.getID(argName);
            arguments[i] = new Symbol(argID, argumentTypes[i], SymbolType.VARDEF_TYPE);
        }

        int funcID = ScopedSymbolTable.getID(funcName);

        FunctionDefNode funcNode = new FunctionDefNode(funcID, ScopedSymbolTable.INTEGER_TYPE, arguments,
            new ListNode(
                new Node() { /* call func */
                    public NodeReturn get(CallStack stack) throws CustomRuntimeException, ReturnEvent {
                        float[] argumentValues = new float[argumentCount];
                        for (int i = 0; i < argumentCount; i++) {
                            int type = arguments[i].type;
                            if (type != ScopedSymbolTable.INTEGER_TYPE 
                                && type != ScopedSymbolTable.FLOAT_TYPE
                                && type != ScopedSymbolTable.STRING_TYPE
                                && type != ScopedSymbolTable.BOOLEAN_TYPE) {

                                argumentValues[i] = 0;
                                continue;
                            }
                            argumentValues[i] = stack.getValue(arguments[i]);
                        }
                        NodeReturn retVal = callback.apply(stack, argumentValues);
                        throw new ReturnEvent(retVal);
                    }
                    public Integer analyze(ScopedSymbolTable table) throws SemanticsException {
                        // TODO Auto-generated method stub
                        return null;
                    }

                }
            ));

        funcNode.analyze(table);
        return funcNode;
    }
}
