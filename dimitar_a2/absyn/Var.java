package absyn;

abstract public class Var extends Absyn
{
    abstract public void accept(AbsynVisitor visitor, int level);
}
