import absyn.Absyn;

public class SemanticError
{
    public static void undeclaredError(SymbolTable.Declaration dec)
    {
        String message = String.format("%s has not yet been declared.", dec.name);
        printErrorMessage(dec.dec, message);
    }

    public static void redeclarationError(SymbolTable.Declaration dec)
    {
        String message = String.format("%s is already defined in current block.", dec.name);
        printErrorMessage(dec.dec, message);
    }

    private static void printErrorMessage(Absyn node, String message)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("\nError: Line %d, Column %d. ", node.row + 1, node.col + 1));
        sb.append(String.format("%s\n", message));
        System.err.println(sb.toString());
    }
}