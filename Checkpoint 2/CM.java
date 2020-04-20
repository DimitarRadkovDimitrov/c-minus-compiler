import java.io.*;
import absyn.*;
   
class CM 
{
    static public void main(String argv[]) 
    {    
        try 
        {
            parser p = new parser(new Lexer(new FileReader(argv[0])));
            Absyn result = (Absyn)(p.parse().value);      
            if (result != null) 
            {
                SymbolTable symbolTable = new SymbolTable();
                TypeChecker typeChecker = new TypeChecker(symbolTable);
                SemanticAnalyzer visitor = new SemanticAnalyzer(symbolTable, typeChecker);
                result.accept(visitor, 1); 
            }
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
}


