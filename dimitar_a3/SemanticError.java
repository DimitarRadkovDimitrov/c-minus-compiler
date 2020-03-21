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

    public static void invalidFunctionReturnError(FunctionDec functionDec)
    {
        String message = "";

        if (functionDec.result.typ == NameTy.INT)
        {
            message = String.format("Function %s has return type %s. Expected INT.", functionDec.func, "VOID");
        }
        else if (functionDec.result.typ == NameTy.VOID)
        {
            message = String.format("Function %s has return type %s. Expected VOID or NULL.", functionDec.func, "INT");
        }
        printErrorMessage(functionDec.row + 1, functionDec.col + 1, message);
    }
    public static void voidTypeError(int row, int col, String expected)
    {
        String message = String.format("Exp type is VOID. Expected %s.", expected);
        printErrorMessage(row, col, message);
    }

    public static void undeclaredError(String id, int row, int col)
    {
        String message = String.format("%s has not yet been declared.", id);
        printErrorMessage(row, col, message);
    }

    public static void redeclarationError(SymbolTable.Declaration dec)
    {
        String message = String.format("%s is already defined in current block.", dec.name);
        printErrorMessage(dec.dec.row + 1, dec.dec.col + 1, message);
    }

    private static void printErrorMessage(int row, int col, String message)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("\nError: Line %d, Column %d. ", row, col));
        sb.append(String.format("%s\n", message));
        System.err.println(sb.toString());
    }
}