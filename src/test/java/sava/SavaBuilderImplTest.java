package sava;

import org.junit.Test;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.net.URLClassLoader;

import static org.junit.Assert.*;

public class SavaBuilderImplTest{

	@Test
	public void createBuilderNewFileTest(){
		new SavaBuilderImpl()
			.createSource("Class -> c" +
				      ",Integer -> i" +    
				      ",String ->  s");
		assertTrue(new File("C.java").exists());
	}

	@Test
	public void createBuilderCheckClassStructure() throws Exception{
		new SavaBuilderImpl()
			.createSource("Class -> c" + 
				      ",Integer -> i" + 
				      ",String -> s");
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
		new SavaBuilderImpl()
			.createSource("Class -> c" + 
				      ",Integer -> i" + 
				      ",String -> s");
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
		new SavaBuilderImpl()
			.createSource("Class -> c" + 
				      ",Integer -> i" + 
				      ",String -> s");
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
		new SavaBuilderImpl()
			.createSource("Class -> c" + 
				      ",Integer -> i" + 
				      ",String -> s");
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
		new SavaBuilderImpl()
			.createSource("Class -> c" + 
				      ",Integer -> i" + 
				      ",String -> s");
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
		new SavaBuilderImpl()
			.createSource("Class -> c" + 
				      ",Integer -> i" + 
				      ",String -> s");
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
		new SavaBuilderImpl()
			.createSource("Class -> c" + 
				      ",Integer -> i" + 
				      ",String -> s")
			.compileBuilder();
		assertTrue(new File("C.class").exists());
	}

	@Test
	public void createBuilderLoadClass() throws Exception{

		new SavaBuilderImpl()
			.createSource("Class -> c" + 
				      ",Integer -> i" + 
				      ",String -> s")
			.compileBuilder();
		final URL url = new File(".").toURL();
		final URL[] urls = new URL[]{url};
		final ClassLoader classLoader = new URLClassLoader(urls);
		final Class clazz = classLoader.loadClass("C$Builder");
		assertEquals("C$Builder", clazz.getName());
	}

	@Test
	public void createBuilderInstantiateBuilder() throws Exception{

		new SavaBuilderImpl()
			.createSource("Class -> c" + 
				      ",Integer -> i" + 
				      ",String -> s")
			.compileBuilder();
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
}

