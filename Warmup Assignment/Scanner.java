import java.io.InputStreamReader;

/**
 * Program uses previously generated lexer to print each token from $DOC-
 * $TITLE-$TEXT formatted separated on preserving old line endings
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
    public Token getNextToken() throws java.io.IOException
    {
        return scanner.yylex();
    }

    /**
     * Takes a SGML formatted document from STDIN and each token on new line
     * @param argv
     */
    public static void main(String argv[])
    {
        try
        {
            Scanner scanner = new Scanner(new Lexer(new InputStreamReader(System.in)));
            Token tok = null;

            while ((tok = scanner.getNextToken()) != null)
            {
                if (!(tok.m_type == 6)) //don't print newline characters
                {
                    System.out.println(tok);
                }
            }
        }
        catch (Exception e)
        {
            System.out.println("Unexpected exception:");
            e.printStackTrace();
        }
    }
}
