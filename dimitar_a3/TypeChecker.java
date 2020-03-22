import absyn.*;

public class TypeChecker
{
    private SymbolTable symbolTable;

    public TypeChecker(SymbolTable symbolTable)
    {
        this.symbolTable = symbolTable;
    }

    public boolean typeCheck(Exp exp, boolean integer)
    {
        if (exp instanceof AssignExp)
        {
            return typeCheck((AssignExp) exp);
        }
        else if (exp instanceof CallExp)
        {
            return typeCheck((CallExp) exp, integer);
        }
        else if (exp instanceof IntExp)
        {
            return typeCheck((IntExp) exp);
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
            return typeCheck((VarExp) exp);
        }
        return false;
    }
    
    public boolean typeCheck(AssignExp assignExp)
    {
        if (assignExp.lhs instanceof IndexVar)
        {
            IndexVar indexVar = (IndexVar) assignExp.lhs;
            ArrayDec arrayDec = (ArrayDec) symbolTable.getSymbol(indexVar.name).dec;
            
            if (arrayDec.typ.typ == NameTy.VOID)
            {
                return false;
            }
            else if (arrayDec.typ.typ == NameTy.INT || arrayDec.typ.typ == NameTy.ERROR)
            {
                return typeCheck(assignExp.rhs, true);
            }
        }
        else if (assignExp.lhs instanceof SimpleVar)
        {
            SimpleVar simpleVar = (SimpleVar) assignExp.lhs;
            SimpleDec simpleDec = (SimpleDec) symbolTable.getSymbol(simpleVar.name).dec;

            if (simpleDec.typ.typ == NameTy.VOID)
            {
                return false;
            }
            else if (simpleDec.typ.typ == NameTy.INT || simpleDec.typ.typ == NameTy.ERROR)
            {
                return typeCheck(assignExp.rhs, true);
            }
        }

        return false;
    }

    public boolean typeCheck(CallExp callExp)
    {
        SymbolTable.Declaration functionDec = symbolTable.getSymbol(callExp.func);
        VarDecList params = ((FunctionDec) functionDec.dec).params;
        ExpList args = callExp.args;

        return typeCheck(params, args);
    }

    public boolean typeCheck(CallExp callExp, boolean integer)
    {
        SymbolTable.Declaration functionDec = symbolTable.getSymbol(callExp.func);

        if (functionDec != null)
        {
            FunctionDec dec = (FunctionDec) functionDec.dec;
            if (dec.result.typ == NameTy.INT)
            {
                return integer;
            }
            else if (dec.result.typ == NameTy.VOID)
            {
                return !integer;
            }
        }

        return false;
    }

    public boolean typeCheck(FunctionDec functionDec)
    {
        if (functionDec.result.typ == NameTy.VOID)
        {
            ExpList functionBody = functionDec.body.exps;
            if (functionBody != null)
            {
                while (functionBody != null && functionBody.head != null)
                {
                    if (functionBody.head instanceof ReturnExp && 
                        typeCheck((ReturnExp) functionBody.head, true))
                    {
                        return false;
                    }
                    functionBody = functionBody.tail;
                }
            }
            return true;
        }
        else if (functionDec.result.typ == NameTy.INT)
        {
            ExpList functionBody = functionDec.body.exps;
            if (functionBody != null)
            {
                while (functionBody != null && functionBody.head != null)
                {
                    if (functionBody.head instanceof ReturnExp && 
                        typeCheck((ReturnExp) functionBody.head, true))
                    {
                        return true;
                    }
                    functionBody = functionBody.tail;
                }
            }
            return false;
        }
        else
        {
            return true;
        }
    }

    public boolean typeCheck(IntExp intExp)
    {
        return true;
    }

    public boolean typeCheck(OpExp opExp, boolean integer)
    {
        return typeCheck(opExp.left, integer) && typeCheck(opExp.right, integer) && integer;
    }

    public boolean typeCheck(ReturnExp returnExp, boolean integer)
    {
        return typeCheck(returnExp.exp, integer) && integer;
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

    public boolean typeCheck(VarExp varExp)
    {
        if (varExp.variable instanceof IndexVar)
        {
            IndexVar indexVar = (IndexVar) varExp.variable;
            ArrayDec arrayDec = (ArrayDec) symbolTable.getSymbol(indexVar.name).dec;
            
            if (arrayDec.typ.typ == NameTy.VOID)
            {
                return false;
            }
            else if (arrayDec.typ.typ == NameTy.INT || arrayDec.typ.typ == NameTy.ERROR)
            {
                return typeCheck(indexVar.index, true);
            }
        }
        else if (varExp.variable instanceof SimpleVar)
        {
            SimpleVar simpleVar = (SimpleVar) varExp.variable;
            SimpleDec simpleDec = (SimpleDec) symbolTable.getSymbol(simpleVar.name).dec;

            if (simpleDec.typ.typ == NameTy.VOID)
            {
                return false;
            }
            else if (simpleDec.typ.typ == NameTy.INT || simpleDec.typ.typ == NameTy.ERROR)
            {
                return true;
            }
        }

        return false;
    }
}