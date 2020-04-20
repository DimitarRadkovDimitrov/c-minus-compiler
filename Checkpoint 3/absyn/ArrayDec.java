package absyn;

public class ArrayDec extends VarDec
{
    public NameTy typ;
    public String name;
    public IntExp size;
    public int nestLevel;
    public int offset;
    public boolean isAddress = false;

    public ArrayDec(int row, int col, NameTy typ, String name, IntExp size)
    {
        this.row = row;
        this.col = col;
        this.typ = typ;
        this.name = name;
        this.size = size;
    }

    public int accept(AbsynVisitor visitor, int offset, boolean isAddress)
    {
        return visitor.visit(this, offset, isAddress);
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        String sizeValue = "";

        sb.append(name + ": ");

        if (this.size != null)
        {
            sizeValue += this.size.value;
        }

        if (typ.typ == NameTy.INT)
        {
            sb.append("int[" + sizeValue + "]");
        }
        else if (typ.typ == NameTy.VOID)
        {
            sb.append("void[" + sizeValue + "]");
        }
        return sb.toString();
    }
}
