JAVAC=javac
JFLEX=jflex
#JFLEX=/Users/Dimitar/jflex-1.7.0/bin/jflex

all: Token.class Lexer.class Scanner.class 

%.class: %.java
	$(JAVAC) $^

Lexer.java: article.flex
	$(JFLEX) article.flex

clean:
	rm -f Lexer.java *.class *~
