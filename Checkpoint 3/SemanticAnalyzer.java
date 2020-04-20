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
    
    public int visit(ArrayDec arrayDec, int level, boolean isAddress)
    {
        SymbolTable.Declaration dec = new SymbolTable.Declaration(arrayDec.name, arrayDec);
        if (!symbolTable.pushDecToBlock(dec))
        {
            SemanticError.redeclarationError(dec);
        }
        return level;
    }

    public int visit(AssignExp assignExp, int level, boolean isAddress)
    {
        assignExp.lhs.accept(this, level, isAddress);
        assignExp.rhs.accept(this, level, isAddress);

        if (assignExp.lhs instanceof SimpleVar)
        {
            SimpleVar simpleVar = (SimpleVar) assignExp.lhs;
            SimpleDec simpleDec = (SimpleDec) symbolTable.getSymbol(simpleVar.name).dec;

            if (simpleDec.typ.typ == NameTy.VOID)
            {
                SemanticError.voidTypeAssignmentError(simpleVar.row + 1, simpleVar.col + 1);
                return level;
            }
        }
        else if (assignExp.lhs instanceof IndexVar)
        {
            IndexVar indexVar = (IndexVar) assignExp.lhs;
            ArrayDec arrayDec = (ArrayDec) symbolTable.getSymbol(indexVar.name).dec;
            
            if (arrayDec.typ.typ == NameTy.VOID)
            {
                SemanticError.voidTypeAssignmentError(indexVar.row + 1, indexVar.col + 1);
                return level;
            }
        }

        if (!typeChecker.typeCheck(assignExp.rhs, true))
        {
            SemanticError.voidExpressionResultError(assignExp);
        }
        return level;
    }

    public int visit(CallExp callExp, int level, boolean isAddress)
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
            callExp.args.accept(this, level, isAddress);
        }

        functionDec = symbolTable.getSymbol(callExp.func);
        params = ((FunctionDec) functionDec.dec).params;
        args = callExp.args;

        if (!typeChecker.typeCheck(params, args))
        {
            SemanticError.invalidFunctionCallError(callExp);;
        }
        return level;
    }

    public int visit(CompoundExp compoundExp, int level, boolean isAddress)
    {
        if (inFunctionSignature)
        {
            inFunctionSignature = false;

            if (compoundExp.decs != null)
            {
                compoundExp.decs.accept(this, level, isAddress);
            }
            if (compoundExp.exps != null)
            {
                compoundExp.exps.accept(this, level, isAddress);
            }
        }
        else
        {
            indent();
            System.out.printf("Entering a new block:\n");
            symbolTable.pushBlock();

            if (compoundExp.decs != null)
            {
                compoundExp.decs.accept(this, level, isAddress);
            }
            if (compoundExp.exps != null)
            {
                compoundExp.exps.accept(this, level, isAddress);
            }

            printBlock(symbolTable.popBlock());
            indent();
            System.out.printf("Leaving the block.\n");
        }
        return level;
    }
    
    public int visit(DecList decList, int level, boolean isAddress)
    {
        System.out.println("Entering the global scope:");
        while (decList != null)
        {
            decList.head.accept(this, level, isAddress);
            decList = decList.tail;
        }
        printBlock(symbolTable.popBlock());
        System.out.println("Leaving the global scope");
        return level;
    }

    public int visit(ExpList expList, int level, boolean isAddress)
    {
        while (expList != null && expList.head != null)
        {
            expList.head.accept(this, level, isAddress);
            expList = expList.tail;  
        }
        return level;
    }

    public int visit(FunctionDec functionDec, int level, boolean isAddress)
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
            functionDec.params.accept(this, level, isAddress);
        }

        functionDec.body.accept(this, level, isAddress);

        if (!typeChecker.typeCheck(functionDec))
        {
            SemanticError.invalidFunctionReturnError(functionDec);
        }

        printBlock(symbolTable.popBlock());
        indent();
        System.out.printf("Leaving the function scope.\n");
        return level;
    }

    public int visit(IfExp ifExp, int level, boolean isAddress)
    {
        ifExp.test.accept(this, level, isAddress);
        ifExp.then.accept(this, level, isAddress);

        if (ifExp.els != null)
        {
            ifExp.els.accept(this, level, isAddress);
        }

        if (!typeChecker.typeCheck(ifExp.test, true))
        {
            SemanticError.voidTestConditionError(ifExp);
        }
        return level;
    }

    public int visit(IndexVar indexVar, int level, boolean isAddress)
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

        indexVar.index.accept(this, level, isAddress);
    
        if (!typeChecker.typeCheck(indexVar.index, true))
        {
            SemanticError.invalidArrayIndexError(indexVar);
        }
        return level;
    }

    public int visit(IntExp intExp, int level, boolean isAddress){
        return level;
    }

    public int visit(NameTy nameTy, int level, boolean isAddress){
        return level;
    }

    public int visit(NilDec exp, int level, boolean isAddress){
        return level;
    }

    public int visit(NilExp exp, int level, boolean isAddress){
        return level;
    }

    public int visit(OpExp opExp, int level, boolean isAddress)
    {
        opExp.left.accept(this, level, isAddress);
        opExp.right.accept(this, level, isAddress);

        if (!typeChecker.typeCheck(opExp, true))
        {
            SemanticError.voidExpressionResultError(opExp);
        }
        return level; 
    }

    public int visit(ReturnExp returnExp, int level, boolean isAddress)
    {
        returnExp.exp.accept(this, level, isAddress);
        return level;
    }
    
    public int visit(SimpleDec simpleDec, int level, boolean isAddress)
    {
        SymbolTable.Declaration dec = new SymbolTable.Declaration(simpleDec.name, simpleDec);
        if (!symbolTable.pushDecToBlock(dec))
        {
            SemanticError.redeclarationError(dec);
        }
        simpleDec.typ.accept(this, level, isAddress);
        return level;
    }

    public int visit(SimpleVar simpleVar, int level, boolean isAddress)
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
        return level;
    }

    public int visit(VarDecList varDecList, int level, boolean isAddress)
    {
        while (varDecList != null && varDecList.head != null)
        {
            varDecList.head.accept(this, level, isAddress);
            varDecList = varDecList.tail;
        }
        return level;
    }

    public int visit(VarExp varExp, int level, boolean isAddress)
    {
        varExp.variable.accept(this, level, isAddress);
        return level;
    }

    public int visit(WhileExp whileExp, int level, boolean isAddress)
    {
        whileExp.test.accept(this, level, isAddress);
        whileExp.body.accept(this, level, isAddress);

        if (!typeChecker.typeCheck(whileExp.test, true))
        {
            SemanticError.voidTestConditionError(whileExp);
        }
        return level;
    }
}
