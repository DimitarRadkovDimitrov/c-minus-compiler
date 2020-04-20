import absyn.*;

public class ShowTreeVisitor implements AbsynVisitor 
{
    final static int SPACES = 4;

    private void indent(int level) 
    {
      for (int i = 0; i < level * SPACES; i++)
      {
          System.out.print(" ");
      }
    }

    public int visit(ArrayDec arrayDec, int level, boolean isAddress)
    {
        indent(level);
        System.out.printf("ArrayDec: %s\n", arrayDec.name);
        level++;
        arrayDec.typ.accept(this, level, isAddress);
        
        if (arrayDec.size != null)
        {
            arrayDec.size.accept(this, level, isAddress);
        }
        level++;
        return level;
    }

    public int visit(AssignExp exp, int level, boolean isAddress)
    {
        indent(level);
        System.out.printf("AssignExp: =\n");
        level++;
        exp.lhs.accept(this, level, isAddress);
        exp.rhs.accept(this, level, isAddress);
        return level;
    }

    public int visit(CallExp callExp, int level, boolean isAddress)
    {
        indent(level);
        System.out.printf("CallExp: %s\n", callExp.func);
        level++;
        if (callExp.args != null)
        {
            callExp.args.accept(this, level, isAddress);
        }
        return level;
    }

    public int visit(CompoundExp compoundExp, int level, boolean isAddress)
    {
        indent(level);
        System.out.printf("CompoundExp:\n");
        if (compoundExp.decs != null || compoundExp.exps != null)
        {
            level++;
        }

        if (compoundExp.decs != null)
        {
            compoundExp.decs.accept(this, level, isAddress);
        }
        if (compoundExp.exps != null)
        {
            compoundExp.exps.accept(this, level, isAddress);
        }
        return level;
    }
    
    public int visit(DecList decList, int level, boolean isAddress)
    {
        while (decList != null)
        {
            decList.head.accept(this, level, isAddress);
            decList = decList.tail;
        }
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
        indent(level);
        System.out.println("FunctionDec: " + functionDec.func);
        functionDec.result.accept(this, level, isAddress);
        level++;

        if (functionDec.params != null)
        {
            functionDec.params.accept(this, level, isAddress);
        }
        functionDec.body.accept(this, level, isAddress);
        return level;
    }

    public int visit(IfExp ifExp, int level, boolean isAddress)
    {
        indent(level);
        System.out.printf("IfExp:\n");
        level++;
        ifExp.test.accept(this, level, isAddress);
        ifExp.then.accept(this, level, isAddress);
        
        if (ifExp.els != null)
        {
            indent(level);
            System.out.printf("ElseExp:\n");
            level++;
            ifExp.els.accept(this, level, isAddress);
        }
        return level;
    }

    public int visit(IndexVar indexVar, int level, boolean isAddress)
    {
        indent(level);
        System.out.printf("IndexVar: %s\n", indexVar.name);
        level++;
        indexVar.index.accept(this, level, isAddress);
        return level;
    }

    public int visit(IntExp intExp, int level, boolean isAddress)
    {
        indent(level);
        System.out.printf("IntExp: %d\n", intExp.value);
        return level;
    }

    public int visit(NameTy nameTy, int level, boolean isAddress)
    {
        indent(level);
        if (nameTy.typ == NameTy.INT)
        {
            System.out.printf("NameTy: INT\n");
        }
        else if (nameTy.typ == NameTy.VOID)
        {
            System.out.printf("NameTy: VOID\n");
        }
        else
        {
            System.out.printf("NameTy: ERROR\n");
        }
        return level;
    }

    public int visit(NilDec exp, int level, boolean isAddress) {
        return level;
    }

    public int visit(NilExp exp, int level, boolean isAddress) {
        return level;
    }

    public int visit(OpExp opExp, int level, boolean isAddress)
    {
        indent(level);
        System.out.printf("OpExp: ");

        switch (opExp.op) {
            case OpExp.LT:
                System.out.printf("<\n");
                break;
            case OpExp.LE:
                System.out.printf("<=\n");
                break;
            case OpExp.GT:
                System.out.printf(">\n");
                break;            
            case OpExp.GE:
                System.out.printf(">=\n");
                break;
            case OpExp.EQ:
                System.out.printf("==\n");
                break;
            case OpExp.NE:
                System.out.printf("!=\n");
                break;
            case OpExp.PLUS:
                System.out.printf("+\n");
                break;
            case OpExp.MINUS:
                System.out.printf("-\n");
                break;
            case OpExp.MUL:
                System.out.printf("*\n");
                break;
            case OpExp.DIV:
                System.out.printf("/\n");
                break;
            default:
                break;
        }

        level++;
        opExp.left.accept(this, level, isAddress);
        opExp.right.accept(this, level, isAddress);
        return level;
    }

    public int visit(ReturnExp returnExp, int level, boolean isAddress)
    {
        indent(level);
        System.out.printf("Return Exp:\n");
        level++;
        returnExp.exp.accept(this, level, isAddress);
        return level;
    }
    
    public int visit(SimpleDec simpleDec, int level, boolean isAddress)
    {
        indent(level);
        System.out.println("SimpleDec: " + simpleDec.name);
        level++;
        simpleDec.typ.accept(this, level, isAddress);
        return level;
    }

    public int visit(SimpleVar simpleVar, int level, boolean isAddress)
    {
        indent(level);
        System.out.printf("SimpleVar: %s\n", simpleVar.name);
        level++;
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
        indent(level);
        System.out.printf("VarExp:\n");
        level++;
        varExp.variable.accept(this, level, isAddress);
        return level;
    }

    public int visit(WhileExp whileExp, int level, boolean isAddress)
    {
        indent(level);
        System.out.printf("WhileExp:\n");
        level++;
        whileExp.test.accept(this, level, isAddress);
        whileExp.body.accept(this, level, isAddress);
        return level;
    }
}
