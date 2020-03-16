import absyn.*;
import java.util.List;

public class SemanticAnalyzer implements AbsynVisitor
{
    final static int SPACES = 4;

    private SymbolTable symbolTable;
    boolean inFunctionSignature = false;

    public SemanticAnalyzer(SymbolTable symbolTable)
    {
        this.symbolTable = symbolTable;
    }

    public void printBlock(List<SymbolTable.Declaration> declarations)
    {
        for (SymbolTable.Declaration dec: declarations)
        {
            if (dec.toString() != "")
            {
                indent();
                System.out.println(dec);
            }
        }
    }

    private void indent() 
    {
        int blockLevel = symbolTable.getCurrentBlockLevel();
        for (int i = 0; i < blockLevel * SPACES; i++)
        {
            System.out.print(" ");
        }
    }
    
    public void visit(ArrayDec arrayDec, int level)
    {
        SymbolTable.Declaration dec = new SymbolTable.Declaration(arrayDec.name, arrayDec);
        if (!symbolTable.pushDecToBlock(dec))
        {
            SemanticError.redeclarationError(dec);
        }

        arrayDec.typ.accept(this, level);
        
        if (arrayDec.size != null)
        {
            arrayDec.size.accept(this, level);
        }
    }

    public void visit(AssignExp exp, int level)
    {
        exp.lhs.accept(this, level);
        exp.rhs.accept(this, level);
    }

    public void visit(CallExp callExp, int level)
    {
        if (callExp.args != null)
        {
            callExp.args.accept(this, level);
        }
    }

    public void visit(CompoundExp compoundExp, int level)
    {
        if (inFunctionSignature)
        {
            inFunctionSignature = false;
            if (compoundExp.decs != null || compoundExp.exps != null)
            {
                level++;
            }
            if (compoundExp.decs != null)
            {
                compoundExp.decs.accept(this, level);
            }
            if (compoundExp.exps != null)
            {
                compoundExp.exps.accept(this, level);
            }
        }
        else
        {
            indent();
            System.out.printf("Entering a new block:\n");
            symbolTable.pushBlock();

            if (compoundExp.decs != null || compoundExp.exps != null)
            {
                level++;
            }

            if (compoundExp.decs != null)
            {
                compoundExp.decs.accept(this, level);
            }
            if (compoundExp.exps != null)
            {
                compoundExp.exps.accept(this, level);
            }

            printBlock(symbolTable.popBlock());
            indent();
            System.out.printf("Leaving the block.\n");
        }
    }
    
    public void visit(DecList decList, int level)
    {
        System.out.println("Entering the global scope:");
        while (decList != null)
        {
            decList.head.accept(this, level);
            decList = decList.tail;
        }
        printBlock(symbolTable.popBlock());
        System.out.println("Leaving the global scope");
    }

    public void visit(ExpList expList, int level)
    {
        while (expList != null && expList.head != null)
        {
            expList.head.accept(this, level);
            expList = expList.tail;  
        }
    }

    public void visit(FunctionDec functionDec, int level)
    {
        inFunctionSignature = true;
        SymbolTable.Declaration dec = new SymbolTable.Declaration(functionDec.func, functionDec);
        
        indent();
        System.out.printf("Entering the scope for function %s\n", functionDec.func);

        if (!symbolTable.pushDecToBlock(dec))
        {
            SemanticError.redeclarationError(dec);
        }
        symbolTable.pushBlock();

        functionDec.result.accept(this, level);

        if (functionDec.params != null)
        {
            functionDec.params.accept(this, level);
        }
        functionDec.body.accept(this, level);

        printBlock(symbolTable.popBlock());
        indent();
        System.out.printf("Leaving the function scope.\n");
    }

    public void visit(IfExp ifExp, int level)
    {
        ifExp.test.accept(this, level);
        ifExp.then.accept(this, level);
        
        if (ifExp.els != null)
        {
            ifExp.els.accept(this, level);
        }
    }

    public void visit(IndexVar indexVar, int level)
    {
        indexVar.index.accept(this, level);
    }

    public void visit(IntExp intExp, int level){}

    public void visit(NameTy nameTy, int level){}

    public void visit(NilDec exp, int level){}

    public void visit(NilExp exp, int level){}

    public void visit(OpExp opExp, int level)
    {
        opExp.left.accept(this, level);
        opExp.right.accept(this, level);
    }

    public void visit(ReturnExp returnExp, int level)
    {
        returnExp.exp.accept(this, level);
    }
    
    public void visit(SimpleDec simpleDec, int level)
    {
        SymbolTable.Declaration dec = new SymbolTable.Declaration(simpleDec.name, simpleDec);
        if (!symbolTable.pushDecToBlock(dec))
        {
            SemanticError.redeclarationError(dec);
        }
        simpleDec.typ.accept(this, level);
    }

    public void visit(SimpleVar simpleVar, int level){}

    public void visit(VarDecList varDecList, int level)
    {
        while (varDecList != null && varDecList.head != null)
        {
            varDecList.head.accept(this, level);
            varDecList = varDecList.tail;
        }
    }

    public void visit(VarExp varExp, int level)
    {
        varExp.variable.accept(this, level);
    }

    public void visit(WhileExp whileExp, int level)
    {
        whileExp.test.accept(this, level);
        whileExp.body.accept(this, level);
    }
}
