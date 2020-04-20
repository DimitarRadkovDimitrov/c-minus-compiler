package absyn;

public class SimpleVar extends Var
{
    public String name;
    public Dec dec;
    
    public SimpleVar(int row, int col, String name)
    {
        this.row = row;
        this.col = col;
        this.name = name;
    }

    public int accept(AbsynVisitor visitor, int offset, boolean isAddress)
    {
        return visitor.visit(this, offset, isAddress);
    }
}
