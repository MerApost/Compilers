all: compile compile2

#(source -> IR)
compile:
	jflex scanner.flex
	java -jar java-cup-11b.jar -interface -parser Parser parser.cup
	javac -cp java-cup-11b-runtime.jar *.java

execute:
	java -cp java-cup-11b-runtime.jar:. Main < input.txt > Translated.ir

#(IR -> Java)
compile2:
	java -jar java-cup-11b.jar -interface -parser Parser2 parser2.cup
	javac -cp java-cup-11b-runtime.jar *.java

execute2:
	java -cp java-cup-11b-runtime.jar:. Main2 < Translated.ir > Translated.java

clean:
	rm -f *.class *~ Parser.java sym.java Parser2.java sym2.java Scanner.java Translated.ir Translated.java