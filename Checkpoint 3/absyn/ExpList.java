package absyn;

public class ExpList extends Absyn
{
    public Exp head;
    public ExpList tail;

    public ExpList(Exp head, ExpList tail)
    {
        this.head = head;
        this.tail = tail;
    }

    public int accept(AbsynVisitor visitor, int offset, boolean isAddress)
    {
        return visitor.visit(this, offset, isAddress);
    }
}
