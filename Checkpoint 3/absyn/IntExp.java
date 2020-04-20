package absyn;

public class IntExp extends Exp
{
    public int value;
    
    public IntExp(int row, int col, int value)
    {
        this.row = row;
        this.col = col;
        this.value = value;
    }

    public int accept(AbsynVisitor visitor, int offset, boolean isAddress)
    {
        return visitor.visit(this, offset, isAddress);
    }
}
