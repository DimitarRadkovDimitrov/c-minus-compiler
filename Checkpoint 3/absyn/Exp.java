package absyn;

abstract public class Exp extends Absyn 
{
    abstract public int accept(AbsynVisitor visitor, int offset, boolean isAddress);
}
