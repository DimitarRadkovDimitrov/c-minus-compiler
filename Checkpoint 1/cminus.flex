/*
  File Name: cminus.flex
  JFlex specification for the C- programming language
*/

import java_cup.runtime.*;

%%

%class Lexer
%line
%column
%cup

%{
    private Symbol symbol(int type) 
    {
        return new Symbol(type, yyline, yycolumn);
    }

    private Symbol symbol(int type, Object value) 
    {
        return new Symbol(type, yyline, yycolumn, value);
    }
%}

%eofval{
    return null;
%eofval};

COMMENT = "/*".*?"*/"
ID = [_a-zA-Z][_a-zA-Z0-9]*
NUM = [0-9]+
NEWLINE = \r|\n|\r\n
WHITESPACE = [ \t\f]+|{NEWLINE}
PUNCTUATION = .

%%

{COMMENT}               { /* Skip Comments */               }
"if"                    { return symbol(sym.IF);            }
"else"                  { return symbol(sym.ELSE);          }
"int"                   { return symbol(sym.INT);           }
"return"                { return symbol(sym.RETURN);        }
"void"                  { return symbol(sym.VOID);          }
"while"                 { return symbol(sym.WHILE);         }
"+"                     { return symbol(sym.PLUS);          }
"-"                     { return symbol(sym.MINUS);         }
"*"                     { return symbol(sym.TIMES);         }
"/"                     { return symbol(sym.DIVIDE);        }
"<"                     { return symbol(sym.LT);            }
"<="                    { return symbol(sym.LTE);           }
">"                     { return symbol(sym.GT);            }
">="                    { return symbol(sym.GTE);           }
"=="                    { return symbol(sym.EQ);            }
"!="                    { return symbol(sym.NOTEQ);         }
"="                     { return symbol(sym.ASSIGN);        }
";"                     { return symbol(sym.SEMI);          }
","                     { return symbol(sym.COMMA);         }
"("                     { return symbol(sym.LPAREN);        }
")"                     { return symbol(sym.RPAREN);        }
"["                     { return symbol(sym.LSQBRACK);      }
"]"                     { return symbol(sym.RSQBRACK);      }
"{"                     { return symbol(sym.LBRACK);        }
"}"                     { return symbol(sym.RBRACK);        }
{ID}                    { return symbol(sym.ID, yytext());  }
{NUM}                   { return symbol(sym.NUM, yytext()); }
{WHITESPACE}            { /* Skip Whitespace */             }
{PUNCTUATION}           { /* Skip Punctuation */            }
