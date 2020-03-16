import java.io.*;
import absyn.*;
   
class CM 
{
    public final static boolean SHOW_TREE = true;
    static public void main(String argv[]) 
    {    
        try 
        {
            parser p = new parser(new Lexer(new FileReader(argv[0])));
            Absyn result = (Absyn)(p.parse().value);      
            if (SHOW_TREE && result != null) 
            {
                System.out.println("The abstract syntax tree is:");
                ShowTreeVisitor visitor = new ShowTreeVisitor();
                result.accept(visitor, 1); 
            }
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
}


