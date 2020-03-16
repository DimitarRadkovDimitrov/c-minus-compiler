import java.io.InputStreamReader;
import java_cup.runtime.Symbol;

/**
 * Program uses previously generated lexer to print each token from
 * a C- programming language file
 */
public class Scanner
{
    private Lexer scanner = null;

    public Scanner(Lexer lexer)
    {
        scanner = lexer;
    }

    /**
     * Returns next token object from Lexer class
     */
    public Symbol getNextToken() throws java.io.IOException
    {
        return scanner.next_token();
    }

    /**
     * Takes a C- file from STDIN and outputs each token on new line
     * @param argv
     */
    public static void main(String argv[])
    {
        try
        {
            Scanner scanner = new Scanner(new Lexer(new InputStreamReader(System.in)));
            Symbol tok = null;

            while ((tok = scanner.getNextToken()) != null)
            {
                System.out.println(tok);
            }
        }
        catch (Exception e)
        {
            System.out.println("Unexpected exception:");
            e.printStackTrace();
        }
    }
}
