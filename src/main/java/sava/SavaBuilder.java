package sava;
public interface SavaBuilder{

	SavaBuilder createSource(final String commaDelimitedString);
	SavaBuilder compileBuilder();
	String getClassName();
}
