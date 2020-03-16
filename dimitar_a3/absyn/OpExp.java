package absyn;

public class OpExp extends Exp
{
    public Exp left;
    public int op;
    public Exp right;
    public static final int PLUS = 0;
    public static final int MINUS = 1;
    public static final int MUL = 2;
    public static final int DIV = 3;
    public static final int EQ = 4;
    public static final int NE = 5;
    public static final int LT = 6;
    public static final int LE = 7;
    public static final int GT = 8;
    public static final int GE = 9;

    public OpExp(int row, int col, Exp left, int op, Exp right)
    {
        this.row = row;
        this.col = col;
        this.left = left;
        this.op = op;
        this.right = right;
    }

    public void accept(AbsynVisitor visitor, int level)
    {
        visitor.visit(this, level);
    }
}
