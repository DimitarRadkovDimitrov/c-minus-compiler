package absyn;

public class VarDecList extends Absyn
{
    public VarDec head;
    public VarDecList tail;

    public VarDecList(VarDec head, VarDecList tail)
    {
        this.head = head;
        this.tail = tail;
    }

    public int accept(AbsynVisitor visitor, int offset, boolean isAddress)
    {
        return visitor.visit(this, offset, isAddress);
    }
}
