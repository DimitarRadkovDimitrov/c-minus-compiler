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

    public void visit(ArrayDec arrayDec, int level)
    {
        indent(level);
        System.out.printf("ArrayDec: %s\n", arrayDec.name);
        level++;
        arrayDec.typ.accept(this, level);
        
        if (arrayDec.size != null)
        {
            arrayDec.size.accept(this, level);
        }
        level++;
    }

    public void visit(AssignExp exp, int level)
    {
        indent(level);
        System.out.printf("AssignExp: =\n");
        level++;
        exp.lhs.accept(this, level);
        exp.rhs.accept(this, level);
    }

    public void visit(CallExp callExp, int level)
    {
        indent(level);
        System.out.printf("CallExp: %s\n", callExp.func);
        level++;
        if (callExp.args != null)
        {
            callExp.args.accept(this, level);
        }
    }

    public void visit(CompoundExp compoundExp, int level)
    {
        indent(level);
        System.out.printf("CompoundExp:\n");
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
    
    public void visit(DecList decList, int level)
    {
        while (decList != null)
        {
            decList.head.accept(this, level);
            decList = decList.tail;
        }
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
        indent(level);
        System.out.println("FunctionDec: " + functionDec.func);
        functionDec.result.accept(this, level);
        level++;

        if (functionDec.params != null)
        {
            functionDec.params.accept(this, level);
        }
        functionDec.body.accept(this, level);
    }

    public void visit(IfExp ifExp, int level)
    {
        indent(level);
        System.out.printf("IfExp:\n");
        level++;
        ifExp.test.accept(this, level);
        ifExp.then.accept(this, level);
        
        if (ifExp.els != null)
        {
            indent(level);
            System.out.printf("ElseExp:\n");
            level++;
            ifExp.els.accept(this, level);
        }
    }

    public void visit(IndexVar indexVar, int level)
    {
        indent(level);
        System.out.printf("IndexVar: %s\n", indexVar.name);
        level++;
        indexVar.index.accept(this, level);
    }

    public void visit(IntExp intExp, int level)
    {
        indent(level);
        System.out.printf("IntExp: %d\n", intExp.value);
    }

    public void visit(NameTy nameTy, int level)
    {
        indent(level);
        if (nameTy.typ == nameTy.INT)
        {
            System.out.printf("NameTy: INT\n");
        }
        else if (nameTy.typ == nameTy.VOID)
        {
            System.out.printf("NameTy: VOID\n");
        }
        else
        {
            System.out.printf("NameTy: ERROR\n");
        }
    }

    public void visit(NilDec exp, int level) {}

    public void visit(NilExp exp, int level) {}

    public void visit(OpExp opExp, int level)
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
        opExp.left.accept(this, level);
        opExp.right.accept(this, level);
    }

    public void visit(ReturnExp returnExp, int level)
    {
        indent(level);
        System.out.printf("Return Exp:\n");
        level++;
        returnExp.exp.accept(this, level);
    }
    
    public void visit(SimpleDec simpleDec, int level)
    {
        indent(level);
        System.out.println("SimpleDec: " + simpleDec.name);
        level++;
        simpleDec.typ.accept(this, level);
    }

    public void visit(SimpleVar simpleVar, int level)
    {
        indent(level);
        System.out.printf("SimpleVar: %s\n", simpleVar.name);
        level++;
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
        indent(level);
        System.out.printf("VarExp:\n");
        level++;
        varExp.variable.accept(this, level);
    }

    public void visit(WhileExp whileExp, int level)
    {
        indent(level);
        System.out.printf("WhileExp:\n");
        level++;
        whileExp.test.accept(this, level);
        whileExp.body.accept(this, level);
    }
}
