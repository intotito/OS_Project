package ie.atu.sw.os;

/**
 * This interface describes the csv (Comma Separated Value) format for an implementing class.
 * It has a single public method {@link #asString() asString}, which returns a String description
 * of the class in csv format. 
 * @author intot
 *
 */
@FunctionalInterface
public interface Savable{
	/**
	 * Escape character for replacing ',' in csv processing.
	 * This is necessary to avoid commas ',' found within documents interfering with formatting.
	 */
	public static final char ESCAPE_CHAR = 'δ';
	/**
	 * 
	 * @return CSV representation of the implementing class.
	 */
	public String asString();	
}
