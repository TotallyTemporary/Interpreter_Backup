package AST;

import interpreter.ScopedSymbolTable;
import static interpreter.ScopedSymbolTable.*;

public class NodeUtil {

    public static boolean isNumeric(int type) {
        return (type == INTEGER_TYPE || type == FLOAT_TYPE);
    }

    public static int getArithmeticType(int left, int right) {

        /* int + int => int*/
        if (left == INTEGER_TYPE && right == INTEGER_TYPE) {
            return INTEGER_TYPE;
        }

        /* float + float => float and float + int => float etc. */
        return FLOAT_TYPE;
    }

    public static int convert(int from, int to) {
        if (from == INTEGER_TYPE && to == FLOAT_TYPE) return FLOAT_TYPE;

        return from;
    }

    public static void convert(NodeReturn from, int toType) {
        /* Convert int to float automatically */
        if (from.type == INTEGER_TYPE && toType == FLOAT_TYPE) {
            from.value = (float) ((int) from.value);
            from.type = toType;
        }
    }

    public static int concat(NodeReturn left, NodeReturn right) {
        return ScopedSymbolTable.getStringID(
            ScopedSymbolTable.getString((int) left.value) +
            ScopedSymbolTable.getString((int) right.value)
        );
    }

    public static boolean stringCompare(NodeReturn left, NodeReturn right) {
        /* Assume type checking has been done */

        String s1 = ScopedSymbolTable.getString((int) left.value);
        String s2 = ScopedSymbolTable.getString((int) right.value);

        return s1.equals(s2);
    }

}
