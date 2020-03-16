package absyn;

abstract public class Exp extends Absyn 
{
    abstract public void accept(AbsynVisitor visitor, int level);
}
