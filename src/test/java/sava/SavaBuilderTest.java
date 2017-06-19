package sava;

import org.junit.Test;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.net.URLClassLoader;

import static org.junit.Assert.*;
import static sava.SavaBuilder.*;
import static java.util.Optional.*;

public class SavaBuilderTest{

	private static final String CLASSPATH = "lib/guava-21.0.jar:lib/junit-4.12.jar:lib/hamcrest-all-1.3.jar:lib/commons-lang3-3.5.jar:src/main/java:src/main/resources:src/test/java";

	@Test
	public void createBuilderNewFileTest(){
		
		final String builderSource = getBuilderSource("Class -> c" +
							      ",Integer -> i" +    
						 	      ",String ->  s");
		writeSource("C.java", builderSource);
		assertTrue(new File("C.java").exists());
	}

	@Test
	public void createBuilderCheckClassStructure() throws Exception{

		final String builderSource = getBuilderSource("Class -> c" +
							      ",Integer -> i" +    
						 	      ",String ->  s");
		writeSource("C.java", builderSource);
		final File file = new File("C.java");
		final FileInputStream fileInputStream = new FileInputStream(file);
		byte[] bytes = new byte[fileInputStream.available()];
		fileInputStream.read(bytes);
		fileInputStream.close();
		final String source = new String(bytes);
		assertTrue(source.contains("public "));
		assertTrue(source.contains("class "));
		assertTrue(source.contains("C{"));
	}

	@Test
	public void createBuilderCheckFieldStructure() throws Exception{
		final String builderSource = getBuilderSource("Class -> c" +
							      ",Integer -> i" +    
						 	      ",String ->  s");
		writeSource("C.java", builderSource);
		final File file = new File("C.java");
		final FileInputStream fileInputStream = new FileInputStream(file);
		byte[] bytes = new byte[fileInputStream.available()];
		fileInputStream.read(bytes);
		fileInputStream.close();
		final String source = new String(bytes);
		assertTrue(source.contains("private "));
		assertTrue(source.contains("Integer "));
		assertTrue(source.contains("i;"));

		assertTrue(source.contains("private "));
		assertTrue(source.contains("String "));
		assertTrue(source.contains("s;"));
	} 

	@Test
	public void createBuilderCheckInstanceStructure() throws Exception{
		final String builderSource = getBuilderSource("Class -> c" +
							      ",Integer -> i" +    
						 	      ",String ->  s");
		writeSource("C.java", builderSource);
		final File file = new File("C.java");
		final FileInputStream fileInputStream = new FileInputStream(file);
		byte[] bytes = new byte[fileInputStream.available()];
		fileInputStream.read(bytes);
		fileInputStream.close();
		final String source = new String(bytes);
		assertTrue(source.contains("private "));
		assertTrue(source.contains("C "));
		assertTrue(source.contains("c = "));
		assertTrue(source.contains("new "));
		assertTrue(source.contains("C();"));
	}

	@Test
	public void createBuilderCheckBuilderMethodStructure() throws Exception{
		final String builderSource = getBuilderSource("Class -> c" +
							      ",Integer -> i" +    
						 	      ",String ->  s");
		writeSource("C.java", builderSource);
		final File file = new File("C.java");
		final FileInputStream fileInputStream = new FileInputStream(file);
		byte[] bytes = new byte[fileInputStream.available()];
		fileInputStream.read(bytes);
		fileInputStream.close();
		final String source = new String(bytes);
		assertTrue(source.contains("public "));
		assertTrue(source.contains("Builder "));
		assertTrue(source.contains("i(Integer "));
		assertTrue(source.contains("i){"));
		assertTrue(source.contains("this.c.i "));
		assertTrue(source.contains("= i;"));
		assertTrue(source.contains("return this;"));

		assertTrue(source.contains("public "));
		assertTrue(source.contains("Builder "));
		assertTrue(source.contains("s(String "));
		assertTrue(source.contains("s){"));
		assertTrue(source.contains("this.c.s "));
		assertTrue(source.contains("= s;"));
	}

	@Test
	public void createBuilderCheckBuildMethodStructure() throws Exception{
		final String builderSource = getBuilderSource("Class -> c" +
							      ",Integer -> i" +    
						 	      ",String ->  s");
		writeSource("C.java", builderSource);
		final File file = new File("C.java");
		final FileInputStream fileInputStream = new FileInputStream(file);
		byte[] bytes = new byte[fileInputStream.available()];
		fileInputStream.read(bytes);
		fileInputStream.close();
		final String source = new String(bytes);
		assertTrue(source.contains("public "));
		assertTrue(source.contains("C "));
		assertTrue(source.contains("build(){"));
		assertTrue(source.contains("return "));
		assertTrue(source.contains("this.c;"));
	}

	@Test
	public void createBuilderCheckGettersMethodStructure() throws Exception{
		final String builderSource = getBuilderSource("Class -> c" +
							      ",Integer -> i" +    
						 	      ",String ->  s");
		writeSource("C.java", builderSource);
		final File file = new File("C.java");
		final FileInputStream fileInputStream = new FileInputStream(file);
		byte[] bytes = new byte[fileInputStream.available()];
		fileInputStream.read(bytes);
		fileInputStream.close();
		final String source = new String(bytes);
		assertTrue(source.contains("public "));
		assertTrue(source.contains("String "));
		assertTrue(source.contains("getS(){"));
		assertTrue(source.contains("return this.s;"));

		assertTrue(source.contains("public "));
		assertTrue(source.contains("Integer "));
		assertTrue(source.contains("getI(){"));
		assertTrue(source.contains("return this.i;"));
	}

	@Test
	public void createBuilderCheckCompilation() throws Exception{
		final String builderExpression = "Class -> c" +
						  ",Integer -> i" +    
						  ",String ->  s";
		putBuilderSource(builderExpression, ".", ".");
		Runtime.getRuntime().exec("javac C.java").waitFor();
		assertTrue(new File("C.class").exists());
	}

	@Test
	public void createBuilderLoadClass() throws Exception{
		final String builderExpression = "Class -> c" +
						  ",Integer -> i" +    
						  ",String ->  s";
		putBuilderSource(builderExpression, ".", ".");
		Runtime.getRuntime().exec("javac C.java").waitFor();
		final URL url = new File(".").toURL();
		final URL[] urls = new URL[]{url};
		final ClassLoader classLoader = new URLClassLoader(urls);
		final Class clazz = classLoader.loadClass("C$Builder");
		assertEquals("C$Builder", clazz.getName());
	}

	@Test
	public void createBuilderInstantiateBuilder() throws Exception{
		final String builderExpression = "Class -> c" +
						  ",Integer -> i" +    
						  ",String ->  s";
		putBuilderSource(builderExpression, ".", ".");
		Runtime.getRuntime().exec("javac C.java").waitFor();
		final URL url = new File(".").toURL();
		final URL[] urls = new URL[]{url};
		final ClassLoader classLoader = new URLClassLoader(urls);
		final Class clazz = classLoader.loadClass("C");
		final Class builderClass = classLoader.loadClass("C$Builder");
		Object object = builderClass.newInstance();
		object = builderClass.getMethod("i", Integer.class).invoke(object, Integer.valueOf(1));
		object = builderClass.getMethod("s", String.class).invoke(object, "Hello");
		object = builderClass.getMethod("build", null).invoke(object, null);
		final Integer i = (Integer) clazz.getMethod("getI", null).invoke(object, null);
		final String s = (String) clazz.getMethod("getS", null).invoke(object, null);
		assertEquals(Integer.valueOf(1), i);
		assertEquals("Hello", s);
	}

	@Test
	public void createBuilderInClassPath() throws Exception{
		final String builderExpression = "Class -> sava.Something" +
						 ",Integer -> i1" +    
						 ",Integer -> i2 " +
						 ",Integer -> i3";
		putBuilderSource(builderExpression
				,"src/main/java"
				,"src/main/java");
		Runtime.getRuntime().exec("javac src/main/java/sava/Something.java").waitFor();
		assertTrue(new File("src/main/java/sava/Something.java").exists());
		assertTrue(new File("src/main/java/sava/Something.class").exists());
	}
}

