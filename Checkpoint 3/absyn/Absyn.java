package absyn;

abstract public class Absyn 
{
    public int row;
    public int col;

    abstract public int accept(AbsynVisitor visitor, int offset, boolean isAddress);
}
