package absyn;

public class FunctionDec extends Dec
{
    public NameTy result;
    public String func;
    public VarDecList params;
    public CompoundExp body;
    public int funaddr;

    public FunctionDec(int row, int col, NameTy result, String func, VarDecList params, CompoundExp body)
    {
        this.row = row;
        this.col = col;
        this.result = result;
        this.func = func;
        this.params = params;
        this.body = body;
    }
    
    public int accept(AbsynVisitor visitor, int offset, boolean isAddress)
    {
        return visitor.visit(this, offset, isAddress);
    }

    @Override
    public String toString()
    {
        return "";
    }
}
