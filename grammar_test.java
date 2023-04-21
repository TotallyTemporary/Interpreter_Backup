/**
 * 
 * Function declaration:
 * get name as var
 * get params as list of [Param node contains Var node and Type node]
 * get contents as node
 * 
 * Semantics analyzer:
 * inserts function name to symbol table
 * makes new scope
 * inserts parameters into scope
 * goes through contents
 * leaves
 * 
 * Interpreter:
 * Does nothing
 * 
 * Function call:
 * get name as var
 * get params as list of [Param node contains Var node and Type node]
 * 
 * Semantics analyzer:
 * goes through params 
 * 
 * 
 * 
 * 
 */