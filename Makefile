classpath = $(shell cat Makefile.classPath)
build: clean build-test
	javac -cp "${classpath}" src/main/java/sava/*.java 
build-test:
	javac -cp "${classpath}" src/test/java/sava/*.java
run:
	java -cp "${classpath}" sava.SavaBuilder
test:
	java -cp "${classpath}" org.junit.runner.JUnitCore sava.SavaTest
clean:
	-rm *.tmp \
	*.class \
	**/C.java \
	src/main/java/sava/Something*.java \
	src/main/java/sava/Something*.class \
	*.java \
	*.class

