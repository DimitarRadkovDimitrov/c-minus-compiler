package absyn;

public class VarExp extends Exp
{
    public Var variable;
    
    public VarExp(int row, int col, Var variable)
    {
        this.row = row;
        this.col = col;
        this.variable = variable;
    }

    public int accept(AbsynVisitor visitor, int offset, boolean isAddress)
    {
        return visitor.visit(this, offset, isAddress);
    }
}
