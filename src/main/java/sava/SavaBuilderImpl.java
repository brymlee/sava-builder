package sava;

import com.google.common.collect.*;
import java.util.Map;
import java.util.List;
import java.io.File;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.Scanner;
import java.io.FileInputStream;

import static com.google.common.base.Preconditions.*;
import static com.google.common.base.Predicates.*;
import static java.util.Arrays.*;

public class SavaBuilderImpl implements SavaBuilder{
	private static String classTemplate = getInputStreamAsString(SavaBuilderImpl.class
		.getResourceAsStream("classTemplate.txt"));

	private String source;
	private String className;

	@Override
	public SavaBuilder createSource(final String commaDelimitedString){
		checkArgument(notNull().apply(commaDelimitedString));
		final List<String> entries = asList(commaDelimitedString.split(","));
		checkState(entries.size() >= 1);
		final Map.Entry<String, String> classEntry = entries
			.stream()
			.map(TO_ENTRY)
			.filter(whereKeyIs("class"))
			.reduce((i, j) -> i)
			.get();
		checkState(notNull().apply(classEntry));
		this.className = classEntry
			.getValue()
			.toUpperCase();
		final String fileName = this.className 
			.concat(".java");
		final String fields = entries
			.stream()
			.map(TO_ENTRY)
			.filter(whereKeyIs("class").negate())
			.map(entry -> "private "
				.concat(entry.getKey())
				.concat(" ")
				.concat(entry.getValue())
				.concat(";")
			).reduce((i, j) -> i
				.concat("\n\t")
				.concat(j))
			.get();
		final String instance = "private "
			.concat(this.className)
			.concat(" ")
			.concat(this.className.toLowerCase())
			.concat(" = new ")
			.concat(this.className)
			.concat("();");
		final String methods = entries
			.stream()
			.map(TO_ENTRY)
			.filter(whereKeyIs("class").negate())
			.map(entry -> "public "
				.concat("Builder ")
				.concat(" ")
				.concat(entry.getValue())
				.concat("(")
				.concat(entry.getKey())
				.concat(" ")
				.concat(entry.getValue())
				.concat("){\n\t\t\t")
				.concat("this.")
				.concat(this.className.toLowerCase())
				.concat(".")
				.concat(entry.getValue())
				.concat(" = ")
				.concat(entry.getValue())
				.concat(";\n\t\t\t")
				.concat("return this;\n\t\t}\n\t\t"))
			.reduce((i, j) -> i
				.concat(j))
			.get();
		final String build = "public "
			.concat(this.className)
			.concat(" build(){\n\t\t\t")
			.concat(" return this.")
			.concat(this.className.toLowerCase())
			.concat(";\n\t\t}\n\t\t");
		final String getters = entries
			.stream()
			.map(TO_ENTRY)
			.filter(whereKeyIs("class").negate())
			.map(entry -> "public "
				.concat(entry.getKey())
				.concat(" get")
				.concat(entry.getValue().toUpperCase())
				.concat("(){\n\t\t")
				.concat("return this.")
				.concat(entry.getValue().toLowerCase())
				.concat(";\n\t}\n\t"))
			.reduce((i, j) -> i.concat(j))
			.get();
		this.source = classTemplate
			.replaceAll("\\$className", this.className)
			.replaceAll("\\$fields", fields)
			.replaceAll("\\$instance", instance)
			.replaceAll("\\$methods", methods)
			.replaceAll("\\$build", build)
			.replaceAll("\\$getters", getters);
		final File file = getSourcedFile(createNewFile(fileName), this.source);
		return this;
	}

	@Override
	public SavaBuilder compileBuilder(){
		try{
			Runtime
				.getRuntime()
				.exec("javac ".concat(this.className).concat(".java"))
				.waitFor();
			return this; 
		}catch(Exception exception){
			throw new RuntimeException(exception);
		}
	}

	@Override
	public String getClassName(){
		return this.className;
	}

	public static void main(String[] args) throws Exception{
		System.out.println("Enter builder expression.");
		final Scanner scanner = new Scanner(System.in);
		while(scanner.hasNextLine()){
			final String nextLine = scanner.nextLine();
			moveFile(new SavaBuilderImpl()
				.createSource(nextLine));
			break;
		}
	}

	private static void moveFile(final SavaBuilder savaBuilder) throws Exception{
		System.out.println("Where to put builder source.");
		final Scanner scanner = new Scanner(System.in);
		while(scanner.hasNextLine()){
			final String nextLine = scanner.nextLine().trim();
			System.out.println(nextLine);
			final String[] split = nextLine.split(File.separator);
			final String directory = split[split.length - 1]
				.equals(File.separator) 
				? nextLine.substring(0, nextLine.length() - 1)
				: nextLine;
			checkState(new File(directory).isDirectory(), "Directory doesn't exist.");
			final String className = savaBuilder.getClassName().concat(".java");
			final File oldFile = new File(className);
			final String source = getInputStreamAsString(new FileInputStream(oldFile));
			oldFile.delete();
			final File newFile = new File(nextLine.concat(File.separator).concat(className));
			getSourcedFile(newFile, source);
			break;
		}
	}

	private static Predicate<Map.Entry<String, String>> whereKeyIs(final String key){
		checkArgument(notNull().apply(key));
		return entry -> key.equals(entry.getKey().toLowerCase());
	}

	private static Function<String, Map.Entry<String, String>> TO_ENTRY = entry -> {
		final String[] split = entry.split("->");
		checkState(split.length == 2);
		return entry(split[0].trim(), split[1].trim());
	}; 

	private static String getInputStreamAsString(final InputStream inputStream){
		try{
			byte[] bytes = new byte[inputStream.available()];
			inputStream.read(bytes);
			inputStream.close();
			return new String(bytes);
		}catch(Exception exception){
			throw new RuntimeException(exception);
		}
	}

	private static File getSourcedFile(final File file
				          ,final String source){
		try{
			final FileOutputStream fileOutputStream = new FileOutputStream(file);
			fileOutputStream.write(source.getBytes());
			fileOutputStream.close();
			return file;
		}catch(Exception exception){
			throw new RuntimeException(exception);
		}
	}

	private static File createNewFile(String fileName){
		final File file = new File(fileName);
		try{
			file.createNewFile();
		}catch(Exception exception){
			throw new RuntimeException(exception);
		}
		return file;
	}

	private static <T, U> Map.Entry<T, U> entry(T t, U u){
		return new ImmutableMap.Builder<T, U>()
			.put(t, u)
			.build()
			.entrySet()
			.iterator()
			.next();
	}

}
