package absyn;

public class NilExp extends Exp
{    
    public NilExp(int row, int col)
    {
        this.row = row;
        this.col = col;
    }

    public int accept(AbsynVisitor visitor, int offset, boolean isAddress)
    {
        return visitor.visit(this, offset, isAddress);
    }
}
