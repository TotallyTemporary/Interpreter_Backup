package AST.arithmeticNodes;

import AST.Node;
import AST.NodeReturn;
import AST.NodeUtil;
import interpreter.CallStack;
import interpreter.CustomRuntimeException;
import interpreter.ReturnEvent;
import interpreter.ScopedSymbolTable;
import interpreter.SemanticsException;
import tokenizer.TokenType;

public class BinaryNode extends Node {

    /* A node that operates on two other nodes (binary = two) */

    /* A bunch of arithmetic happens inside this node */

    private Node left, right;
    private TokenType operator;

    public BinaryNode(Node left, Node right, TokenType operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    @Override
    public NodeReturn get(CallStack stack) throws CustomRuntimeException, ReturnEvent {
        NodeReturn leftVal = left.get(stack);
        NodeReturn rightVal = right.get(stack);

        /* string concat */
        if (leftVal.type == ScopedSymbolTable.STRING_TYPE &&
            rightVal.type == ScopedSymbolTable.STRING_TYPE &&
            operator == TokenType.PLUS) {
                return new NodeReturn(NodeUtil.concat(leftVal, rightVal),
                ScopedSymbolTable.STRING_TYPE);
        }

        int retType = NodeUtil.getArithmeticType(leftVal.type, rightVal.type);

        float value;
        switch (operator) {
            case PLUS: 
                value = leftVal.value + rightVal.value;
                break;
            case MINUS:
                value = leftVal.value - rightVal.value;
                break;
            case MUL:
                value = leftVal.value * rightVal.value;
                break;
            case DIV:
                value = leftVal.value / rightVal.value;
                break;
            case POW:
                value = (float) Math.pow(leftVal.value, rightVal.value);
                break;
            default:
                throw new CustomRuntimeException("Bad operator " + operator);
        }

        return new NodeReturn(value, retType);
    }

    @Override
    public Integer analyze(ScopedSymbolTable table) throws SemanticsException {
        int leftType = left.analyze(table);
        int rightType = right.analyze(table);

        /* String concat */
        if (leftType == ScopedSymbolTable.STRING_TYPE &&
            rightType == ScopedSymbolTable.STRING_TYPE &&
            operator == TokenType.PLUS) {
                return ScopedSymbolTable.STRING_TYPE;
            }

        if (!(NodeUtil.isNumeric(leftType) && NodeUtil.isNumeric(rightType))) {
            throw new SemanticsException("Cannot do " + operator + " on types " +
                                ScopedSymbolTable.getName(leftType) + " and " +
                                ScopedSymbolTable.getName(rightType));
        }

        int type = NodeUtil.getArithmeticType(leftType, rightType);

        return type;
    }
}
