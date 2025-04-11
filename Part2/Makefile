all: compile 

#(source -> IR) k (IR -> Java)
compile:
	jflex scanner.flex
	java -jar java-cup-11b.jar -interface -parser Parser parser.cup
	java -jar java-cup-11b.jar -interface -parser Parser2 parser2.cup
	javac -cp java-cup-11b-runtime.jar *.java

execute:
	java -cp java-cup-11b-runtime.jar:. Main < input.txt > Translated.ir
	java -cp java-cup-11b-runtime.jar:. Main2 < Translated.ir > Translated.java

clean:
	rm -f *.class *~ Parser.java Parser2.java Scanner.java sym.java Translated.ir Translated.java
