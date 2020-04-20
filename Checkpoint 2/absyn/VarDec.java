package absyn;

abstract public class VarDec extends Dec
{
    abstract public void accept(AbsynVisitor visitor, int level);
}
