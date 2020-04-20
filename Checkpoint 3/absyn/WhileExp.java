package absyn;

public class WhileExp extends Exp
{
    public Exp test;
    public Exp body;

    public WhileExp(int row, int col, Exp test, Exp body)
    {
        this.row = row;
        this.col = col;
        this.test = test;
        this.body = body;
    }

    public int accept(AbsynVisitor visitor, int offset, boolean isAddress)
    {
        return visitor.visit(this, offset, isAddress);
    }
}
