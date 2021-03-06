import absyn.*;

public class SemanticError
{
    public static void invalidArrayIndexError(IndexVar indexVar)
    {
        String message = String.format(
            "%s array type has invalid index expression result. Expected INT.", 
            indexVar.name);
        printErrorMessage(indexVar.index.row + 1, indexVar.index.col + 1, message);
    }

    public static void invalidFunctionCallError(CallExp functionCall)
    {
        String message = String.format("Function %s argument list does not match parameter list.", functionCall.func);
        printErrorMessage(functionCall.row + 1, functionCall.col + 1, message);
    }

    public static void invalidFunctionReturnError(FunctionDec functionDec)
    {
        String message = "";

        if (functionDec.result.typ == NameTy.INT)
        {
            message = String.format("Function %s has return type %s. Return expression evaluates to VOID.", functionDec.func, "INT");
        }
        else if (functionDec.result.typ == NameTy.VOID)
        {
            message = String.format("Function %s has return type %s. Return expression evaluates to INT.", functionDec.func, "VOID");
        }
        printErrorMessage(functionDec.row + 1, functionDec.col + 1, message);
    }

    public static void redeclarationError(SymbolTable.Declaration dec)
    {
        String message = String.format("%s is already defined in current block.", dec.name);
        printErrorMessage(dec.dec.row + 1, dec.dec.col + 1, message);
    }

    public static void undeclaredError(String id, int row, int col)
    {
        String message = String.format("%s has not yet been declared.", id);
        printErrorMessage(row, col, message);
    }

    public static void voidExpressionResultError(Exp exp)
    {
        String message = String.format("%s result is of type VOID. Expected INT.", exp.getClass().getSimpleName());
        printErrorMessage(exp.row + 1, exp.col + 1, message);
    }

    public static void voidTestConditionError(Exp exp)
    {
        String message = String.format("Test condition expression has result of type VOID. Expected INT.");
        printErrorMessage(exp.row + 1, exp.col + 1, message);
    }

    public static void voidTypeAssignmentError(int row, int col)
    {
        String message = String.format("Assignment to variable of type VOID.");
        printErrorMessage(row, col, message);
    }

    private static void printErrorMessage(int row, int col, String message)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("\nError: Line %d, Column %d. ", row, col));
        sb.append(String.format("%s\n", message));
        System.err.println(sb.toString());
    }
}