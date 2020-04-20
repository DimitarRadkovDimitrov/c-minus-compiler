JAVA=java
JAVAC=javac
#CLASSPATH=-cp /Users/Dimitar/cup/cup.jar:.
#JFLEX=/Users/Dimitar/jflex-1.7.0/bin/jflex
#CUP=$(JAVA) $(CLASSPATH) java_cup.Main -expect 8
CLASSPATH=-classpath /usr/share/java/cup.jar:.
JFLEX=jflex
CUP=cup -expect 8

all: CM.class

CM.class: CM.java absyn/*.java parser.java sym.java Lexer.java SemanticAnalyzer.java SymbolTable.java TypeChecker.java SemanticError.java

%.class: %.java
	$(JAVAC) $(CLASSPATH) $^

sym.java: cminus.cup
	$(CUP) cminus.cup

Lexer.java: cminus.flex
	$(JFLEX) cminus.flex

parser.java: cminus.cup
	$(CUP) cminus.cup

analyzer_fac: CM.class cm_progs/fac.cm
	$(JAVA) $(CLASSPATH) CM cm_progs/fac.cm

analyzer_gcd: CM.class cm_progs/gcd.cm
	$(JAVA) $(CLASSPATH) CM cm_progs/gcd.cm

analyzer_sort: CM.class cm_progs/selection_sort.cm
	$(JAVA) $(CLASSPATH) CM cm_progs/selection_sort.cm

test1: CM.class cm_progs_test/1.cm
	$(JAVA) $(CLASSPATH) CM cm_progs_test/1.cm

test2: CM.class cm_progs_test/2.cm
	$(JAVA) $(CLASSPATH) CM cm_progs_test/2.cm

test3: CM.class cm_progs_test/3.cm
	$(JAVA) $(CLASSPATH) CM cm_progs_test/3.cm

test4: CM.class cm_progs_test/4.cm
	$(JAVA) $(CLASSPATH) CM cm_progs_test/4.cm

test5: CM.class cm_progs_test/5.cm
	$(JAVA) $(CLASSPATH) CM cm_progs_test/5.cm

clean:
	rm -f Lexer.java sym.java parser.java *.class absyn/*.class *~
