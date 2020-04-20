import java.io.*;
import absyn.*;
   
class CM 
{
    static public void main(String argv[]) 
    {    
        try 
        {
            if (argv.length > 0)
            {
                String inputFilename = argv[0];
                parser p = new parser(new Lexer(new FileReader(inputFilename)));
                Absyn result = (Absyn)(p.parse().value);     

                if (result != null)
                {
                    if (argv.length > 1)
                    {
                        if (argv[1].equalsIgnoreCase("-a"))
                        {
                            ShowTreeVisitor visitor = new ShowTreeVisitor();
                            result.accept(visitor, 1, false); 
                            return;
                        }
                        else if (argv[1].equalsIgnoreCase("-s"))
                        {
                            SymbolTable symbolTable = new SymbolTable();
                            TypeChecker typeChecker = new TypeChecker(symbolTable);
                            SemanticAnalyzer visitor = new SemanticAnalyzer(symbolTable, typeChecker);
                            result.accept(visitor, 1, false); 
                            return;
                        }
                    }
                    
                    String outputFilename = inputFilename.substring(0, inputFilename.length() - 3) + ".tm";
                    CodeGenerator visitor = new CodeGenerator(result, outputFilename);
                    visitor.codeGen();
                }
            }
            else
            {
                System.err.println("Usage: java -cp <CUP_PATH>.jar:. CM <PATH_TO_FILE>.cm");
            }   
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
}


