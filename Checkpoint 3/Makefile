JAVA=java
JAVAC=javac
# CLASSPATH=-cp /Users/Dimitar/cup/cup.jar:.
# JFLEX=/Users/Dimitar/jflex-1.7.0/bin/jflex
# CUP=$(JAVA) $(CLASSPATH) java_cup.Main -expect 8
CLASSPATH=-classpath /usr/share/java/cup.jar:.
JFLEX=jflex
CUP=cup -expect 8

all: tm CM.class

tm: tm.c
	gcc tm.c -o tm

CM.class: CM.java absyn/*.java parser.java sym.java Lexer.java CodeGenerator.java

%.class: %.java
	$(JAVAC) $(CLASSPATH) $^

sym.java: cminus.cup
	$(CUP) cminus.cup

Lexer.java: cminus.flex
	$(JFLEX) cminus.flex

parser.java: cminus.cup
	$(CUP) cminus.cup

fac: CM.class tm sample_progs/fac.cm
	$(JAVA) $(CLASSPATH) CM sample_progs/fac.cm -c
	./tm sample_progs/fac.tm

fac_ast: CM.class sample_progs/fac.cm
	$(JAVA) $(CLASSPATH) CM sample_progs/fac.cm -a

fac_sem: CM.class sample_progs/fac.cm
	$(JAVA) $(CLASSPATH) CM sample_progs/fac.cm -s

gcd: CM.class tm sample_progs/gcd.cm
	$(JAVA) $(CLASSPATH) CM sample_progs/gcd.cm -c
	./tm sample_progs/gcd.tm 

gcd_ast: CM.class sample_progs/gcd.cm
	$(JAVA) $(CLASSPATH) CM sample_progs/gcd.cm -a

gcd_sem: CM.class sample_progs/gcd.cm
	$(JAVA) $(CLASSPATH) CM sample_progs/gcd.cm -s

sort: CM.class tm sample_progs/sort.cm
	$(JAVA) $(CLASSPATH) CM sample_progs/sort.cm -c
	./tm sample_progs/sort.tm

sort_ast: CM.class sample_progs/sort.cm
	$(JAVA) $(CLASSPATH) CM sample_progs/sort.cm -a

sort_sem: CM.class sample_progs/sort.cm
	$(JAVA) $(CLASSPATH) CM sample_progs/sort.cm -s

test1: CM.class tm test_progs/1.cm
	$(JAVA) $(CLASSPATH) CM test_progs/1.cm
	./tm test_progs/1.tm

test2: CM.class tm test_progs/2.cm
	$(JAVA) $(CLASSPATH) CM test_progs/2.cm
	./tm test_progs/2.tm

test3: CM.class tm test_progs/3.cm
	$(JAVA) $(CLASSPATH) CM test_progs/3.cm
	./tm test_progs/3.tm

test4: CM.class tm test_progs/4.cm
	$(JAVA) $(CLASSPATH) CM test_progs/4.cm
	./tm test_progs/4.tm

test5: CM.class tm test_progs/5.cm
	$(JAVA) $(CLASSPATH) CM test_progs/5.cm
	./tm test_progs/5.tm

test6: CM.class tm test_progs/6.cm
	$(JAVA) $(CLASSPATH) CM test_progs/6.cm
	./tm test_progs/6.tm

test7: CM.class tm test_progs/7.cm
	$(JAVA) $(CLASSPATH) CM test_progs/7.cm
	./tm test_progs/7.tm

test8: CM.class tm test_progs/8.cm
	$(JAVA) $(CLASSPATH) CM test_progs/8.cm
	./tm test_progs/8.tm

test9: CM.class tm test_progs/9.cm
	$(JAVA) $(CLASSPATH) CM test_progs/9.cm
	./tm test_progs/9.tm

test0: CM.class tm test_progs/0.cm
	$(JAVA) $(CLASSPATH) CM test_progs/0.cm
	./tm test_progs/0.tm

clean:
	rm -f tm Lexer.java sym.java parser.java *.class absyn/*.class sample_progs/*.tm test_progs/*.tm *~
