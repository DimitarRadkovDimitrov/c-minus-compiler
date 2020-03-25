import absyn.*;
import java.util.List;

public class SemanticAnalyzer implements AbsynVisitor
{
    final static int SPACES = 4;

    private SymbolTable symbolTable;
    private TypeChecker typeChecker;

    boolean inFunctionSignature = false;

    public SemanticAnalyzer(SymbolTable symbolTable, TypeChecker typeChecker)
    {
        SymbolTable.Declaration inputFunctionDec = new SymbolTable.Declaration(
            "input",
            new FunctionDec(0, 0, new NameTy(0, 0, NameTy.INT), "input", null, null) 
        );
        SymbolTable.Declaration outputFunctionDec = new SymbolTable.Declaration(
            "output",
            new FunctionDec(
                0,
                0, 
                new NameTy(0, 0, NameTy.VOID),
                "output",
                new VarDecList(new SimpleDec(0, 0, new NameTy(0, 0, NameTy.INT), "x"), null),
                null
            ) 
        );

        this.symbolTable = symbolTable;
        this.typeChecker = typeChecker;
        this.symbolTable.pushDecToBlock(inputFunctionDec);
        this.symbolTable.pushDecToBlock(outputFunctionDec);
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
    }

    public void visit(AssignExp assignExp, int level)
    {
        assignExp.lhs.accept(this, level);
        assignExp.rhs.accept(this, level);

        if (assignExp.lhs instanceof SimpleVar)
        {
            SimpleVar simpleVar = (SimpleVar) assignExp.lhs;
            SimpleDec simpleDec = (SimpleDec) symbolTable.getSymbol(simpleVar.name).dec;

            if (simpleDec.typ.typ == NameTy.VOID)
            {
                SemanticError.voidTypeAssignmentError(simpleVar.row + 1, simpleVar.col + 1);
                return;
            }
        }
        else if (assignExp.lhs instanceof IndexVar)
        {
            IndexVar indexVar = (IndexVar) assignExp.lhs;
            ArrayDec arrayDec = (ArrayDec) symbolTable.getSymbol(indexVar.name).dec;
            
            if (arrayDec.typ.typ == NameTy.VOID)
            {
                SemanticError.voidTypeAssignmentError(indexVar.row + 1, indexVar.col + 1);
                return;
            }
        }

        if (!typeChecker.typeCheck(assignExp.rhs, true))
        {
            SemanticError.voidExpressionResultError(assignExp);
        }
    }

    public void visit(CallExp callExp, int level)
    {
        SymbolTable.Declaration functionDec;
        VarDecList params;
        ExpList args;

        if (!symbolTable.isDefined(callExp.func))
        {
            SemanticError.undeclaredError(callExp.func, callExp.row + 1, callExp.col + 1);
        }

        if (callExp.args != null)
        {
            callExp.args.accept(this, level);
        }

        functionDec = symbolTable.getSymbol(callExp.func);
        params = ((FunctionDec) functionDec.dec).params;
        args = callExp.args;

        if (!typeChecker.typeCheck(params, args))
        {
            SemanticError.invalidFunctionCallError(callExp);;
        }
    }

    public void visit(CompoundExp compoundExp, int level)
    {
        if (inFunctionSignature)
        {
            inFunctionSignature = false;

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

        if (functionDec.params != null)
        {
            functionDec.params.accept(this, level);
        }

        functionDec.body.accept(this, level);

        if (!typeChecker.typeCheck(functionDec))
        {
            SemanticError.invalidFunctionReturnError(functionDec);
        }

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
        if (!symbolTable.isDefined(indexVar.name))
        {
            SemanticError.undeclaredError(indexVar.name, indexVar.row + 1, indexVar.col + 1);
            SymbolTable.Declaration dec = new SymbolTable.Declaration(
                indexVar.name, 
                new ArrayDec(
                    indexVar.row,
                    indexVar.col,
                    new NameTy(indexVar.row, indexVar.col, NameTy.ERROR),
                    indexVar.name,
                    new IntExp(indexVar.row, indexVar.col, 0)
                ));
            symbolTable.pushDecToBlock(dec);
        }

        indexVar.index.accept(this, level);
    
        if (!typeChecker.typeCheck(indexVar.index, true))
        {
            SemanticError.invalidArrayIndexError(indexVar);
        }
    }

    public void visit(IntExp intExp, int level){}

    public void visit(NameTy nameTy, int level){}

    public void visit(NilDec exp, int level){}

    public void visit(NilExp exp, int level){}

    public void visit(OpExp opExp, int level)
    {
        opExp.left.accept(this, level);
        opExp.right.accept(this, level);

        if (!typeChecker.typeCheck(opExp, true))
        {
            SemanticError.voidExpressionResultError(opExp);
        } 
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

    public void visit(SimpleVar simpleVar, int level)
    {
        if (!symbolTable.isDefined(simpleVar.name))
        {
            SemanticError.undeclaredError(simpleVar.name, simpleVar.row + 1, simpleVar.col + 1);
            SymbolTable.Declaration dec = new SymbolTable.Declaration(
                simpleVar.name, 
                new SimpleDec(
                    simpleVar.row,
                    simpleVar.col,
                    new NameTy(simpleVar.row, simpleVar.col, NameTy.ERROR),
                    simpleVar.name
                ));
            symbolTable.pushDecToBlock(dec);
        }
    }

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
