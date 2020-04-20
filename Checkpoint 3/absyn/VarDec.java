package absyn;

abstract public class VarDec extends Dec
{
    abstract public int accept(AbsynVisitor visitor, int offset, boolean isAddress);
}
