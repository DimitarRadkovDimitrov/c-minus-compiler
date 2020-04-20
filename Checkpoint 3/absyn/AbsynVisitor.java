package absyn;

public interface AbsynVisitor 
{
    public int visit(ArrayDec arrayDec, int offset, boolean isAddress);
    public int visit(AssignExp assignExp, int offset, boolean isAddress);
    public int visit(CallExp callExp, int offset, boolean isAddress);
    public int visit(CompoundExp compoundExp, int offset, boolean isAddress);
    public int visit(DecList decList, int offset, boolean isAddress);
    public int visit(ExpList expList, int offset, boolean isAddress);
    public int visit(FunctionDec functionDec, int offset, boolean isAddress);
    public int visit(IfExp ifExp, int offset, boolean isAddress);
    public int visit(IndexVar indexVar, int offset, boolean isAddress);
    public int visit(IntExp intExp, int offset, boolean isAddress);
    public int visit(NameTy nameTy, int offset, boolean isAddress);
    public int visit(NilDec nilDec, int offset, boolean isAddress);
    public int visit(NilExp nilExp, int offset, boolean isAddress);
    public int visit(OpExp opExp, int offset, boolean isAddress);
    public int visit(ReturnExp returnExp, int offset, boolean isAddress);
    public int visit(SimpleDec simpleDec, int offset, boolean isAddress);
    public int visit(SimpleVar simpleVar, int offset, boolean isAddress);
    public int visit(VarDecList varDecList, int offset, boolean isAddress);
    public int visit(VarExp varExp, int offset, boolean isAddress);
    public int visit(WhileExp whileExp, int offset, boolean isAddress);
}
