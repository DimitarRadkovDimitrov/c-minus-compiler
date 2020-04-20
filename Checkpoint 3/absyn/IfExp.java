package absyn;

public class IfExp extends Exp
{
    public Exp test;
    public Exp then;
    public Exp els;

    public IfExp(int row, int col, Exp test, Exp then, Exp els)
    {
        this.row = row;
        this.col = col;
        this.test = test;
        this.then = then;
        this.els = els;
    }

    public int accept(AbsynVisitor visitor, int offset, boolean isAddress)
    {
        return visitor.visit(this, offset, isAddress);
    }
}
