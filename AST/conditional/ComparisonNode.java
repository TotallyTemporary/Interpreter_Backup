package AST.conditional;

import AST.Node;
import AST.NodeReturn;
import AST.NodeUtil;
import interpreter.CallStack;
import interpreter.CustomRuntimeException;
import interpreter.ReturnEvent;
import interpreter.ScopedSymbolTable;
import interpreter.SemanticsException;
import tokenizer.TokenType;

public class ComparisonNode extends Node {

    private TokenType operation;
    private Node left, right;

    public ComparisonNode(Node left, Node right, TokenType operation) {
        this.left = left;
        this.right = right;
        this.operation = operation;
    }

    @Override
    public NodeReturn get(CallStack stack) throws CustomRuntimeException, ReturnEvent {
        NodeReturn leftVal = left.get(stack);
        NodeReturn rightVal = right.get(stack);
        
        /* Checks have been performed by analyze function */
        /* if leftval is boolean, we can perform boolean and|or boolean operation */
        /* if leftval is float, we can perform >|<|==|>=|<= operation */

        /* boolean to boolean comparison */
        if (leftVal.type == ScopedSymbolTable.BOOLEAN_TYPE) {
            boolean result;
            switch (operation) {
                case AND:
                    result = (leftVal.value + rightVal.value == 2);
                    break;
                case OR:
                    result = (leftVal.value + rightVal.value > 0);
                    break;
                default:
                    throw new CustomRuntimeException("Bad comparison");
            }

            return new NodeReturn(result ? 1 : 0, ScopedSymbolTable.BOOLEAN_TYPE);
        }

        /* number to number comparison */
        if (NodeUtil.isNumeric(leftVal.type)) {
            boolean result;
            switch (operation) {
                case IS_EQUAL:
                    result = leftVal.value == rightVal.value;
                    break;
                case IS_LT:
                    result = leftVal.value < rightVal.value;
                    break;
                case IS_LEQ:
                    result = leftVal.value <= rightVal.value;
                    break;
                case IS_GT:
                    result = leftVal.value > rightVal.value;
                    break;
                case IS_GEQ:
                    result = leftVal.value >= rightVal.value;
                    break;
                default:
                    throw new CustomRuntimeException("Bad comparison"); 
            }
            return new NodeReturn(result ? 1 : 0, ScopedSymbolTable.BOOLEAN_TYPE);
        }

        /* String comparison */
        if (leftVal.type == ScopedSymbolTable.STRING_TYPE) {
            boolean result = NodeUtil.stringCompare(leftVal, rightVal);
            return new NodeReturn(result ? 1 : 0, ScopedSymbolTable.BOOLEAN_TYPE);
        }

        throw new CustomRuntimeException("Bad comparison");


    }

    @Override
    public Integer analyze(ScopedSymbolTable table) throws SemanticsException {
        Integer leftVal = left.analyze(table);
        Integer rightVal = right.analyze(table);

        /* float > float operation etc. */
        if (NodeUtil.isNumeric(leftVal) && NodeUtil.isNumeric(rightVal) && isNumericConversion()) {
            return ScopedSymbolTable.BOOLEAN_TYPE;
        }

        /* string comparison */
        if (leftVal == rightVal && leftVal == ScopedSymbolTable.STRING_TYPE &&
            operation == TokenType.IS_EQUAL) {
            
            return ScopedSymbolTable.BOOLEAN_TYPE;
        }

        /* boolean and|or */
        if (leftVal == rightVal && leftVal == ScopedSymbolTable.BOOLEAN_TYPE && 
            (operation == TokenType.AND || operation == TokenType.OR)) {
            return ScopedSymbolTable.BOOLEAN_TYPE;
        }

        throw new SemanticsException("Bad comparison");
    }

    /* returns if operation is a comparison between two numbers */
    private boolean isNumericConversion() {
        return (operation == TokenType.IS_EQUAL || operation == TokenType.IS_GEQ ||
            operation == TokenType.IS_LEQ || operation == TokenType.IS_LT || operation == TokenType.IS_GT);
    }
    
}
