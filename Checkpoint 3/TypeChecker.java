import absyn.*;
import java.util.Stack;

public class TypeChecker
{
    private SymbolTable symbolTable;

    public TypeChecker(SymbolTable symbolTable)
    {
        this.symbolTable = symbolTable;
    }

    public boolean typeCheck(Exp exp, boolean integer)
    {
        if (exp instanceof CallExp)
        {
            return typeCheck((CallExp) exp, integer);
        }
        else if (exp instanceof IntExp)
        {
            return typeCheck((IntExp) exp, integer);
        }
        else if (exp instanceof OpExp)
        {
            return typeCheck((OpExp) exp, integer);
        }
        else if (exp instanceof ReturnExp)
        {
            return typeCheck((ReturnExp) exp, integer);
        }
        else if (exp instanceof VarExp)
        {
            return typeCheck((VarExp) exp, integer);
        }
        return false;
    }

    public boolean typeCheck(CallExp callExp, boolean integer)
    {
        SymbolTable.Declaration functionDec = symbolTable.getSymbol(callExp.func);

        if (functionDec != null)
        {
            FunctionDec dec = (FunctionDec) functionDec.dec;
            if (dec.result.typ == NameTy.VOID)
            {
                return integer == false;
            }
            return integer == true;
        }
        return false;
    }

    public boolean typeCheck(FunctionDec functionDec)
    {
        if (functionDec.result.typ == NameTy.VOID)
        {
            Stack<CompoundExp> blocks = new Stack<>();
            blocks.add(functionDec.body);

            while (!blocks.isEmpty())
            {
                CompoundExp block = blocks.pop();
                ExpList expList = block.exps;

                while (expList != null && expList.head != null)
                {
                    if (expList.head instanceof ReturnExp && typeCheck((ReturnExp) expList.head, true))
                    {
                        return false;
                    }
                    else if (expList.head instanceof CompoundExp)
                    {
                        blocks.add((CompoundExp) expList.head);
                    }
                    else if (expList.head instanceof IfExp)
                    {
                        if (((IfExp) expList.head).then instanceof CompoundExp)
                        {
                            blocks.add((CompoundExp) ((IfExp) expList.head).then);
                        }
                        else if (((IfExp) expList.head).then instanceof ReturnExp)
                        {
                            return !typeCheck((ReturnExp) ((IfExp) expList.head).then, true);
                        }
                    }
                    expList = expList.tail;
                }
            }
            return true;
        }
        else if (functionDec.result.typ == NameTy.INT)
        {
            Stack<CompoundExp> blocks = new Stack<>();
            blocks.add(functionDec.body);

            while (!blocks.isEmpty())
            {
                CompoundExp block = blocks.pop();
                ExpList expList = block.exps;

                while (expList != null && expList.head != null)
                {
                    if (expList.head instanceof ReturnExp && typeCheck((ReturnExp) expList.head, true))
                    {
                        return true;
                    }
                    else if (expList.head instanceof CompoundExp)
                    {
                        blocks.add((CompoundExp) expList.head);
                    }
                    else if (expList.head instanceof IfExp)
                    {
                        if (((IfExp) expList.head).then instanceof CompoundExp)
                        {
                            blocks.add((CompoundExp) ((IfExp) expList.head).then);
                        }
                        else if (((IfExp) expList.head).then instanceof ReturnExp)
                        {
                            return typeCheck((ReturnExp) ((IfExp) expList.head).then, true);
                        }
                    }
                    expList = expList.tail;
                }
            }
            return false;
        }
        else
        {
            return true;
        }
    }

    public boolean typeCheck(IntExp intExp, boolean integer)
    {
        return integer == true;
    }

    public boolean typeCheck(OpExp opExp, boolean integer)
    {
        return typeCheck(opExp.left, integer) && typeCheck(opExp.right, integer);
    }

    public boolean typeCheck(ReturnExp returnExp, boolean integer)
    {
        return typeCheck(returnExp.exp, integer);
    }

    public boolean typeCheck(VarDecList params, ExpList args)
    {
        if (params == null && args == null)
        {
            return true;
        }
        else if (params == null || args == null)
        {
            return false;
        }

        while (params != null && params.head != null)
        {
            if (args == null || args.head == null)
            {
                return false;
            }

            VarDec param = params.head;
            Exp arg = args.head;
            boolean integer = false;

            if (param instanceof SimpleDec)
            {
                SimpleDec dec = (SimpleDec) param;

                if (dec.typ.typ == NameTy.INT)
                {
                    integer = true;
                }
            }
            else if (param instanceof ArrayDec)
            {
                ArrayDec dec = (ArrayDec) param;

                if (dec.typ.typ == NameTy.INT)
                {
                    integer = true;
                }
            }

            if (!typeCheck(arg, integer))
            {
                return false;
            }

            args = args.tail;
            params = params.tail;
        }

        if (args != null)
        {
            return false;
        }

        return true;
    }

    public boolean typeCheck(VarExp varExp, boolean integer)
    {
        if (varExp.variable instanceof IndexVar)
        {
            IndexVar indexVar = (IndexVar) varExp.variable;
            ArrayDec arrayDec = (ArrayDec) symbolTable.getSymbol(indexVar.name).dec;
            
            if (arrayDec.typ.typ == NameTy.VOID)
            {
                return integer == false;
            }
            return integer == true;
        }
        else if (varExp.variable instanceof SimpleVar)
        {
            SimpleVar simpleVar = (SimpleVar) varExp.variable;
            Dec dec = symbolTable.getSymbol(simpleVar.name).dec;
            
            if ((dec instanceof SimpleDec && ((SimpleDec) dec).typ.typ == NameTy.VOID) || 
                (dec instanceof ArrayDec && ((ArrayDec) dec).typ.typ == NameTy.VOID))
            {
                return integer == false;
            }
            return integer == true;
        }
        
        return false;
    }
}