package absyn;

public class IndexVar extends Var
{
    public String name;
    public Exp index;
    public ArrayDec dec;

    public IndexVar(int row, int col, String name, Exp index)
    {
        this.row = row;
        this.col = col;
        this.name = name;
        this.index = index;
    }

    public int accept(AbsynVisitor visitor, int offset, boolean isAddress)
    {
        return visitor.visit(this, offset, isAddress);
    }
}
