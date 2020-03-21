import absyn.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SymbolTable
{
    private ArrayList<HashMap<String, Declaration>> declarations;
    
    public SymbolTable()
    {
        this.declarations = new ArrayList<>();
        this.declarations.add(new HashMap<String, Declaration>());
    }

    public Declaration getSymbol(String id)
    {
        for (HashMap<String, Declaration> block: declarations)
        {
            if (block.containsKey(id))
            {
                return block.get(id);
            }
        }
        return null;
    }

    public boolean isDefined(String id)
    {
        for (HashMap<String, Declaration> block: declarations)
        {
            if (block.containsKey(id))
            {
                return true;
            }
        }
        return false;
    }

    public boolean pushDecToBlock(Declaration dec)
    {
        HashMap<String, Declaration> currentBlock = getCurrentBlock();

        if (currentBlock.containsKey(dec.name))
        {
            return false;
        }

        currentBlock.put(dec.name, dec);
        return true;
    }

    public void pushBlock()
    {
        declarations.add(new HashMap<String, Declaration>());
    }

    public List<Declaration> popBlock()
    {
        List<Declaration> blockDeclarations = new ArrayList<>();

        if (declarations.size() > 0)
        {
            HashMap<String, Declaration> currentBlock = getCurrentBlock();
            for (Map.Entry<String, Declaration> entry: currentBlock.entrySet())
            {
                blockDeclarations.add(0, entry.getValue());
            }

            declarations.remove(declarations.size() - 1);
        }

        return blockDeclarations;
    }

    public int getCurrentBlockLevel()
    {
        return declarations.size();
    }

    private HashMap<String, Declaration> getCurrentBlock()
    {
        return declarations.get(declarations.size() - 1);
    }

    public static class Declaration
    {
        public String name;
        public Dec dec;

        public Declaration(String name, Dec dec)
        {
            this.name = name;
            this.dec = dec;
        }

        @Override
        public String toString()
        {
            return dec.toString();
        }
    }
}
