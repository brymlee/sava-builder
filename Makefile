jarClasspath = lib/guava-21.0.jar:lib/junit-4.12.jar:lib/hamcrest-all-1.3.jar
sourceClasspath = src/main/java
resourcesClasspath = src/main/resources
testClasspath = src/test/java
classpath = ${jarClasspath}:${sourceClasspath}:${resourcesClasspath}:${testClasspath}
build: clean build-test
	javac -cp "${classpath}" src/main/java/sava/*.java 
build-test:
	javac -cp "${classpath}" src/test/java/sava/*.java
run:
	java -cp "${classpath}" sava.SavaBuilderImpl
test:
	java -cp "${classpath}" org.junit.runner.JUnitCore sava.SavaBuilderImplTest 
clean:
	-rm *.tmp *.class C.java

