package absyn;

public class DecList extends Absyn
{
    public Dec head;
    public DecList tail;

    public DecList(Dec head, DecList tail)
    {
        this.head = head;
        this.tail = tail;
    }

    public int accept(AbsynVisitor visitor, int offset, boolean isAddress)
    {
        return visitor.visit(this, offset, isAddress);
    }
}
