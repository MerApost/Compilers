all: compile

compile:
	java -jar ../jtb132di.jar -te minijava.jj
	java -jar ../javacc5.jar minijava-jtb.jj
	javac syntaxtree/*.java visitor/*.java
	javac *.java

clean:
	rm -rf *.class syntaxtree visitor minijava-jtb.jj *~

