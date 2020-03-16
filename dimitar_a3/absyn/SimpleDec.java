package absyn;

public class SimpleDec extends VarDec
{
    public NameTy typ;
    public String name;

    public SimpleDec(int row, int col, NameTy typ, String name)
    {
        this.row = row;
        this.col = col;
        this.typ = typ;
        this.name = name;
    }

    public void accept(AbsynVisitor visitor, int level)
    {
        visitor.visit(this, level);
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s: ", name));

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
