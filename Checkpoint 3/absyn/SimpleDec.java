package absyn;

public class SimpleDec extends VarDec
{
    public NameTy typ;
    public String name;
    public int nestLevel;
    public int offset;

    public SimpleDec(int row, int col, NameTy typ, String name)
    {
        this.row = row;
        this.col = col;
        this.typ = typ;
        this.name = name;
    }

    public int accept(AbsynVisitor visitor, int offset, boolean isAddress)
    {
        return visitor.visit(this, offset, isAddress);
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(name + ": ");

        if (typ.typ == NameTy.INT)
        {
            sb.append("int");
        }
        else if (typ.typ == NameTy.VOID)
        {
            sb.append("void");
        }
        return sb.toString();
    }
}
