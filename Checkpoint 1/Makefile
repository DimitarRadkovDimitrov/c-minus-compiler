JAVA=java
JAVAC=javac
#CLASSPATH=-classpath /Users/Dimitar/cup/java-cup-11b.jar:.
#JFLEX=/Users/Dimitar/jflex-1.7.0/bin/jflex
#CUP=$(JAVA) $(CLASSPATH) java_cup.Main <
CLASSPATH=-classpath /usr/share/java/cup.jar:.
JFLEX=jflex
CUP=cup

all: CM.class

CM.class: CM.java absyn/*.java parser.java sym.java Lexer.java ShowTreeVisitor.java

Scanner.class: Scanner.java Lexer.java sym.java

%.class: %.java
	$(JAVAC) $(CLASSPATH) $^

sym.java: cminus.cup
	$(CUP) cminus.cup

Lexer.java: cminus.flex
	$(JFLEX) cminus.flex

parser.java: cminus.cup
	$(CUP) cminus.cup

run_scanner: Scanner.class SymToString.class cm_progs/fac.cm
	$(JAVA) $(CLASSPATH) Scanner < cm_progs/fac.cm | $(JAVA) SymToString

run_parser: CM.class cm_progs/fac.cm cm_progs/gcd.cm cm_progs/selection_sort.cm
	$(JAVA) $(CLASSPATH) CM cm_progs/fac.cm
	$(JAVA) $(CLASSPATH) CM cm_progs/gcd.cm
	$(JAVA) $(CLASSPATH) CM cm_progs/selection_sort.cm

run_test1: CM.class cm_progs_test/1.cm
	$(JAVA) $(CLASSPATH) CM cm_progs_test/1.cm

run_test2: CM.class cm_progs_test/2.cm
	$(JAVA) $(CLASSPATH) CM cm_progs_test/2.cm

run_test3: CM.class cm_progs_test/3.cm
	$(JAVA) $(CLASSPATH) CM cm_progs_test/3.cm

run_test4: CM.class cm_progs_test/4.cm
	$(JAVA) $(CLASSPATH) CM cm_progs_test/4.cm

run_test5: CM.class cm_progs_test/5.cm
	$(JAVA) $(CLASSPATH) CM cm_progs_test/5.cm

clean:
	rm -f Lexer.java sym.java parser.java *.class absyn/*.class *~
