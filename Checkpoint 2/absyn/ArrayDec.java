package absyn;

public class ArrayDec extends VarDec
{
    public NameTy typ;
    public String name;
    public IntExp size;

    public ArrayDec(int row, int col, NameTy typ, String name, IntExp size)
    {
        this.row = row;
        this.col = col;
        this.typ = typ;
        this.name = name;
        this.size = size;
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
            sb.append("int[]");
        }
        else if (typ.typ == NameTy.VOID)
        {
            sb.append("void[]");
        }
        return sb.toString();
    }
}
