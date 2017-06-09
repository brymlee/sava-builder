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
import java.util.Optional;

import static com.google.common.base.Preconditions.*;
import static com.google.common.base.Predicates.*;
import static java.util.Arrays.*;
import static java.util.stream.IntStream.*;
import static org.apache.commons.lang3.text.WordUtils.*;

public class Sava{
	private static String classTemplate = getInputStreamAsString(Sava.class
		.getResourceAsStream("classTemplate.txt"));

	public static String getBuilderSource(final String commaDelimitedString){
		checkArgument(notNull().apply(commaDelimitedString));
		final List<String> entries = asList(commaDelimitedString.split(","));
		checkState(entries.size() >= 1);
		final Map.Entry<String, String> classEntry = entries
			.stream()
			.map(TO_ENTRY)
			.filter(whereKeyIs("class"))
			.map(entry -> {
				final String[] split = entry.getValue().split("\\.");
				return entry(entry.getKey(), split[split.length - 1]);
			}).reduce((i, j) -> i)
			.get();
		checkState(notNull().apply(classEntry));
		final String className = capitalize(classEntry.getValue());
		final Map.Entry<String, String> packageEntry = entries
			.stream()
			.map(TO_ENTRY)
			.filter(whereKeyIs("class"))
			.map(entry -> {
				final String[] split = entry.getValue().split("\\.");
				final Optional<String> value = range(0, split.length - 1)
					.mapToObj(index -> split[index])
					.reduce((i, j) -> i.concat(j));
				return entry(entry.getKey()
					    ,value.isPresent() 
					? value.get()
					: "");
			}).reduce((i, j) -> i)
			.get();
		final String packageName = packageEntry
				.getValue()
				.trim()
				.equals("") 
			? ""
			: "package "
				.concat(packageEntry
					.getValue()
					.trim())
				.concat(";");
		final String fileName = className 
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
			.concat(className)
			.concat(" ")
			.concat(className.toLowerCase())
			.concat(" = new ")
			.concat(className)
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
				.concat(className.toLowerCase())
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
			.concat(className)
			.concat(" build(){\n\t\t\t")
			.concat(" return this.")
			.concat(className.toLowerCase())
			.concat(";\n\t\t}\n\t\t");
		final String getters = entries
			.stream()
			.map(TO_ENTRY)
			.filter(whereKeyIs("class").negate())
			.map(entry -> "public "
				.concat(entry.getKey())
				.concat(" get")
				.concat(capitalize(entry.getValue()))
				.concat("(){\n\t\t")
				.concat("return this.")
				.concat(entry.getValue().toLowerCase())
				.concat(";\n\t}\n\t"))
			.reduce((i, j) -> i.concat(j))
			.get();
		return classTemplate
			.replaceAll("\\$package", packageName)
			.replaceAll("\\$className", className)
			.replaceAll("\\$fields", fields)
			.replaceAll("\\$instance", instance)
			.replaceAll("\\$methods", methods)
			.replaceAll("\\$build", build)
			.replaceAll("\\$getters", getters);
	}

	public static String putBuilderSource(final String builderExpression
					     ,final String sourcePath 
					     ,final String destination){
		final List<String> entries = asList(builderExpression.split(","));
		checkState(entries.size() >= 1);
		final Map.Entry<String, String> classEntry = entries
			.stream()
			.map(TO_ENTRY)
			.filter(whereKeyIs("class"))
			.map(entry -> {
				final String[] split = entry.getValue().split("\\.");
				return entry(entry.getKey(), split[split.length - 1]);
			}).reduce((i, j) -> i)
			.get();
		final Map.Entry<String, String> packageEntry = entries
			.stream()
			.map(TO_ENTRY)
			.filter(whereKeyIs("class"))
			.map(entry -> {
				final String[] split = entry.getValue().split("\\.");
				final Optional<String> value = range(0, split.length - 1)
					.mapToObj(index -> split[index])
					.reduce((i, j) -> i.concat(j));
				return entry(entry.getKey()
					    ,value.isPresent() 
					? value.get()
					: "");
			}).reduce((i, j) -> i)
			.get();
		final String className = capitalize(classEntry.getValue());
		final String packageName = packageEntry.getValue().replaceAll("\\.", "/");
		final String builderSource = getBuilderSource(builderExpression);
		if(".".equals(destination) || ".".equals(sourcePath)){
			writeSource(className.concat(".java"), builderSource); 
			return className.concat(".java");
		}else{
			final String source = sourcePath
				.concat(File.separator)
				.concat(packageName)
				.concat(File.separator)
				.concat(className)
				.concat(".java");
			writeSource(source, builderSource);
			return source;
		}
	}

	private static String moveSource(final String destination
				        ,final File file){
		try{
			
			final String classSource = getInputStreamAsString(new FileInputStream(file));
			deleteFile(file);
			writeSource(destination, classSource);
			return destination;
		}catch(Exception exception){
			throw new RuntimeException(exception);
		}
	}

	private static File deleteFile(final File file){
		try{
			file.delete();
			return file;
		}catch(Exception exception){
			throw new RuntimeException(exception);
		}
	}

	public static String writeSource(final String path 
				        ,final String source){
		try{
			final File file = createNewFile(path);
			final FileOutputStream fileOutputStream = new FileOutputStream(file);
			fileOutputStream.write(source.getBytes());
			fileOutputStream.close();
			return path;
		}catch(Exception exception){
			throw new RuntimeException(exception);
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

	protected static String getInputStreamAsString(final InputStream inputStream){
		try{
			byte[] bytes = new byte[inputStream.available()];
			inputStream.read(bytes);
			inputStream.close();
			return new String(bytes);
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
