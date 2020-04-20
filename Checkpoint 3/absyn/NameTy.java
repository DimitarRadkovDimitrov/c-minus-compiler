package absyn;

public class NameTy extends Absyn
{
    public static final int INT = 0;
    public static final int VOID = 1;
    public static final int ERROR = 2;
    public int typ;

    public NameTy(int row, int col, int typ)
    {
        this.row = row;
        this.col = col;
        this.typ = typ;
    }

    public int accept(AbsynVisitor visitor, int offset, boolean isAddress)
    {
        return visitor.visit(this, offset, isAddress);
    }
}
