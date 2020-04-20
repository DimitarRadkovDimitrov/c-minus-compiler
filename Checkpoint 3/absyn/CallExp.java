package absyn;

public class CallExp extends Exp
{
    public String func;
    public ExpList args;
    public FunctionDec dec;
    
    public CallExp(int row, int col, String func, ExpList args)
    {
        this.row = row;
        this.col = col;
        this.func = func;
        this.args = args;
    }

    public int accept(AbsynVisitor visitor, int offset, boolean isAddress)
    {
        return visitor.visit(this, offset, isAddress);
    }
}
