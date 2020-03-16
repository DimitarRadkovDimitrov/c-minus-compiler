import java.io.InputStreamReader;
import java.util.Scanner;

public class SymToString
{
    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(new InputStreamReader(System.in));
        String token;

        while (scanner.hasNext())
        {
            token = scanner.next();
            token = token.replace("#", "");
            System.out.println(sym.terminalNames[Integer.parseInt(token)]);
        }
    }
}