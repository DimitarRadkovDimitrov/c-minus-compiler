package absyn;

abstract public class Var extends Absyn
{
    abstract public int accept(AbsynVisitor visitor, int offset, boolean isAddress);
}
