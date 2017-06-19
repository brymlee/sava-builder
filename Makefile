projectName = sava-builder
classpath = $(shell cat Makefile.classPath)
build: build-test
	javac -cp "${classpath}" src/main/java/sava/*.java 
build-test:
	javac -cp "${classpath}" src/test/java/sava/*.java
test:
	java -cp "${classpath}" org.junit.runner.JUnitCore sava.SavaBuilderTest
pack: build test clean pack-resource pack-test
	jar -uvf package/${projectName}.jar src/main/java
pack-resource:
	jar -cvf package/${projectName}.jar src/main/resources
pack-test:
	jar -uvf package/${projectName}.jar src/test/java
clean:
	-rm *.tmp \
	*.class \
	**/C.java \
	src/main/java/sava/Something*.java \
	src/main/java/sava/Something*.class \
	*.java \
	*.class \
	package/*

