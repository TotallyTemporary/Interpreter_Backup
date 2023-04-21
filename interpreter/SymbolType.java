package interpreter;

public enum SymbolType {
    /* Built-ins */
    BUILT_IN_TYPE, /* Integer, Float */
    BUILT_IN_VAR, /* $True, $False */
    BUILT_IN_FUNC, /* :$WriteFloat */
    BUILT_IN_CLASS, /* System */
    BUILT_IN_OBJECT, /* $Sys */

    /* user defined */
    VARDEF_TYPE, /* $x, $y */
    FUNCTION_NAME, /* :$Main() */
    CLASS_NAME, /* Class $Animal => {}; */
    CLASS_INSTANCE, /* Animal $dog = New Animal(5); */
}
