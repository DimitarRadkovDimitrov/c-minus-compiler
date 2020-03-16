package absyn;

abstract public class Dec extends Absyn
{
    abstract public void accept(AbsynVisitor visitor, int level);
}