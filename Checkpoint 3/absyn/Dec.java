package absyn;

abstract public class Dec extends Absyn
{
    abstract public int accept(AbsynVisitor visitor, int offset, boolean isAddress);
}