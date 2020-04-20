/**
 * Class used by JFLEX generated Lexer class to store tokens by
 * the regex matched
 */
class Token
{
    public final static int OPENTAG = 0;
    public final static int CLOSETAG = 1;
    public final static int WORD = 2;
    public final static int NUMBER = 3;
    public final static int APOSTROPHIZED = 4;
    public final static int HYPHENATED = 5;
    public final static int NEWLINE = 6;
    public final static int PUNCTUATION = 7;
    public final static int OPENDOC = 8;
    public final static int OPENTEXT = 9;
    public final static int OPENDATE = 10;
    public final static int OPENDOCNO = 11;
    public final static int OPENHEADLINE = 12;
    public final static int OPENLENGTH = 13;
    public final static int OPENP = 14;
    public final static int CLOSEDOC = 15;
    public final static int CLOSETEXT = 16;
    public final static int CLOSEDATE = 17;
    public final static int CLOSEDOCNO = 18;
    public final static int CLOSEHEADLINE = 19;
    public final static int CLOSELENGTH = 20;
    public final static int CLOSEP = 21;

    public int m_type;
    public String m_value;
    public int m_line;
    public int m_column;

    Token (int type, String value, int line, int column)
    {
        m_type = type;
        m_value = value;
        m_line = line;
        m_column = column;
    }

    /**
     * Returns type of token in string form
     */
    public String toString()
    {
        switch (m_type)
        {
            case OPENTAG:
                return "OPEN-TAG";
            case CLOSETAG:
                return "CLOSE-TAG";
            case WORD:
                return "WORD(" + m_value + ")";
            case NUMBER:
                return "NUMBER(" + m_value + ")";
            case APOSTROPHIZED:
                return "APOSTROPHIZED(" + m_value + ")";
            case HYPHENATED:
                return "HYPHENATED(" + m_value + ")";
            case NEWLINE:
                return "NEWLINE";
            case PUNCTUATION:
                return "PUNCTUATION(" + m_value + ")";
            case OPENDOC:
                return "OPEN-DOC";
            case OPENTEXT:
                return "OPEN-TEXT";
            case OPENDATE:
                return "OPEN-DATE";
            case OPENDOCNO:
                return "OPEN-DOCNO";
            case OPENHEADLINE:
                return "OPEN-HEADLINE";
            case OPENLENGTH:
                return "OPEN-LENGTH";
            case OPENP:
                return "OPEN-P";
            case CLOSEDOC:
                return "CLOSE-DOC";
            case CLOSETEXT:
                return "CLOSE-TEXT";
            case CLOSEDATE:
                return "CLOSE-DATE";
            case CLOSEDOCNO:
                return "CLOSE-DOCNO";
            case CLOSEHEADLINE:
                return "CLOSE-HEADLINE";
            case CLOSELENGTH:
                return "CLOSE-LENGTH";
            case CLOSEP:
                return "CLOSE-P";
            default:
                return "UNKNOWN(" + m_value + ")";
        }
    }
}
