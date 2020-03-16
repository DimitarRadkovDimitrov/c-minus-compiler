JAVA=java
JAVAC=javac
JFLEX=/Users/Dimitar/jflex-1.7.0/bin/jflex
#JFLEX=jflex 
CLASSPATH=-classpath /Users/dimitar/cup/java-cup-11b.jar:.
#CLASSPATH=-classpath /usr/share/java/cup.jar:.
CUP=$(JAVA) $(CLASSPATH) java_cup.Main <
#CUP=cup

all: Main.class

Main.class: absyn/*.java parser.java sym.java Lexer.java ShowTreeVisitor.java Scanner.java Main.java

%.class: %.java
	$(JAVAC) $(CLASSPATH)  $^

Lexer.java: tiny.flex
	$(JFLEX) tiny.flex

parser.java: tiny.cup
	#$(CUP) -dump -expect 3 tiny.cup
	$(CUP) tiny.cup -expect 3 

clean:
	rm -f parser.java Lexer.java sym.java *.class absyn/*.class *~
