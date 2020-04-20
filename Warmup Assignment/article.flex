/*
  File Name: article.flex
  JFlex specification for a news article format
*/

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

%%

%class Lexer
%type Token
%line
%column

%{
    private static final String[] RELEVANT_TAGS = new String[] {"doc", "text", "date", "docno", "headline", "length", "p"};
    private static final Set<String> RELEVANT_TAG_SET = new HashSet<>(Arrays.asList(RELEVANT_TAGS));
    private static ArrayList<String> tagStack = new ArrayList<String>();
    private static Boolean inRelevantTag = false;
    
    /**
     * Returns true if tag stack is empty, false otherwise
     */
    private static Boolean stackIsEmpty()
    {
        if (tagStack.size() == 0)
        {
            return true;
        }
        return false;
    }

    /**
     * Returns the tag on top of the stack without removal
     */
    private static String stackPeek()
    {
        if (stackIsEmpty())
        {
            return null;
        }
        return tagStack.get(tagStack.size() - 1);
    }

    /**
     * Returns the tag on top of the stack with removal
     */
    private static String stackPop()
    {
        if (stackIsEmpty())
        {
            return null;
        }

        String topTag = tagStack.get(tagStack.size() - 1);
        tagStack.remove(tagStack.size() - 1);
        return topTag;
    }

    /**
     * Adds tag to top of the stack
     */
    private static void stackPush(String openTag)
    {
        tagStack.add(openTag);
    }

    /**
     * Removes all tabs, leading and trailing whitespace from string
     */
    private static String stripWhitespace(String tag)
    {
        String[] splitted = null;
        tag = tag.replace("\t", "").trim();
        splitted = tag.split(" ");
        return splitted[0];
    }
%};

%eofval{
    System.err.println(tagStack.size() + " unmatched tokens.");
    for (String tag: tagStack)
    {
        System.err.println("Unmatched token: " + tag);
    }
    return null;
%eofval};

DIGIT = [0-9]
ALPHA = [a-zA-Z]
ALPHANUM = [a-zA-Z0-9]

OPENTAG = <{WHITESPACE}*{ALPHA}{ALPHANUM}*{WHITESPACE}*>|<{WHITESPACE}*{ALPHA}{ALPHANUM}*({WHITESPACE}*{ALPHA}+{WHITESPACE}*={WHITESPACE}*\"{ALPHA}+\"{WHITESPACE}*)+>
CLOSETAG = <"/"{WHITESPACE}*{ALPHA}{ALPHANUM}*{WHITESPACE}*>
NUMBER = ("-"|"+")?{DIGIT}*"."?{DIGIT}+
HYPHENATED = {WORD}("-"{WORD})+
APOSTROPHIZED = ({HYPHENATED}('{WORD})+|{WORD}('{WORD})+)
WORD = {ALPHANUM}+
NEWLINE = \r|\n|\r\n
WHITESPACE = [ \t\f]+
PUNCTUATION = .

%%

{OPENTAG}          
{
    String tag = yytext();
    int tagSize = tag.length();
  
    String openTagText = tag.substring(1, tagSize - 1).toLowerCase();
    openTagText = stripWhitespace(openTagText);

    if (openTagText.equals("p"))
    {
        if (inRelevantTag)
        {
            return new Token(Token.OPENP, yytext(), yyline, yycolumn);
        }
    }
    else
    {
        stackPush(openTagText);
        if (RELEVANT_TAG_SET.contains(openTagText))
        {
            inRelevantTag = true;
            switch (openTagText)
            {
                case "doc":
                    return new Token(Token.OPENDOC, yytext(), yyline, yycolumn);
                case "text":
                    return new Token(Token.OPENTEXT, yytext(), yyline, yycolumn);
                case "date":
                    return new Token(Token.OPENDATE, yytext(), yyline, yycolumn);
                case "docno":
                    return new Token(Token.OPENDOCNO, yytext(), yyline, yycolumn); 
                case "headline":
                    return new Token(Token.OPENHEADLINE, yytext(), yyline, yycolumn);
                case "length":
                    return new Token(Token.OPENLENGTH, yytext(), yyline, yycolumn);
                case "default":
                    return new Token(Token.OPENTAG, yytext(), yyline, yycolumn);
            }
        }
        else
        {
            inRelevantTag = false;
        }
    }
}

{CLOSETAG}         
{
    String tag = yytext();
    int tagSize = tag.length();

    String closeTagText = tag.substring(2, tagSize - 1).toLowerCase();
    closeTagText = stripWhitespace(closeTagText);

    if (closeTagText.equals("p"))
    {
        if (inRelevantTag)
        {
            return new Token(Token.CLOSEP, yytext(), yyline, yycolumn);
        }
    }
    else
    {
        if ((stackIsEmpty()) || !(stackPeek().equals(closeTagText)))
        {
            System.err.println("Closing tag '" + closeTagText + "' has no matching open tag.");
        }
        else
        { 
            stackPop();
            if (RELEVANT_TAG_SET.contains(closeTagText))
            {
                inRelevantTag = true;
                switch (closeTagText)
                {
                    case "doc":
                        return new Token(Token.CLOSEDOC, yytext(), yyline, yycolumn);
                    case "text":
                        return new Token(Token.CLOSETEXT, yytext(), yyline, yycolumn);
                    case "date":
                        return new Token(Token.CLOSEDATE, yytext(), yyline, yycolumn);
                    case "docno":
                        return new Token(Token.CLOSEDOCNO, yytext(), yyline, yycolumn); 
                    case "headline":
                        return new Token(Token.CLOSEHEADLINE, yytext(), yyline, yycolumn);
                    case "length":
                        return new Token(Token.CLOSELENGTH, yytext(), yyline, yycolumn);
                    case "default":
                        return new Token(Token.CLOSETAG, yytext(), yyline, yycolumn);
                }
            }
            else
            {
                inRelevantTag = false;
            }
        }
    }
}

{NUMBER}           
{ 
    if (inRelevantTag)
    {
        return new Token(Token.NUMBER, yytext(), yyline, yycolumn); 
    }
}

{APOSTROPHIZED}    
{ 
    if (inRelevantTag)
    {
        return new Token(Token.APOSTROPHIZED, yytext(), yyline, yycolumn); 
    }
}

{HYPHENATED}       
{
    if (inRelevantTag)
    {
        return new Token(Token.HYPHENATED, yytext(), yyline, yycolumn); 
    }
}

{WORD}             
{ 
    if (inRelevantTag)
    {
        return new Token(Token.WORD, yytext(), yyline, yycolumn);
    }
}

{NEWLINE}          
{
    if (inRelevantTag)
    {
        return new Token(Token.NEWLINE, yytext(), yyline, yycolumn);
    }
}

{WHITESPACE}       { /** Skip Whitespace **/}

{PUNCTUATION}      
{
    if (inRelevantTag)
    {
        return new Token(Token.PUNCTUATION, yytext(), yyline, yycolumn); 
    }
}
