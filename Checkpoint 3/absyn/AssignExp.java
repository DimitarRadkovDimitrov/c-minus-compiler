package absyn;

public class AssignExp extends Exp
{
    public Var lhs;
    public Exp rhs;

    public AssignExp(int row, int col, Var lhs, Exp rhs)
    {
        this.row = row;
        this.col = col;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public int accept(AbsynVisitor visitor, int offset, boolean isAddress)
    {
        return visitor.visit(this, offset, isAddress);
    }
}
