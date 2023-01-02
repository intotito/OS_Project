package ie.atu.sw.os.reqres;

import java.util.function.Function;
import java.util.stream.IntStream;

import ie.atu.sw.os.data.Report;

/**
 * This interface has default and static methods for formatting outputs to the
 * console. The default method gives inheriting class the capability to print
 * formatted menus to the screen. And the static method offer generic methods
 * for formatting tabulated values to the console.
 * 
 * @author intot
 *
 */
public interface Formatter {
	/**
	 * Prints values at the bottom of a table by replacing the current line.
	 * 
	 * @param entry   - Array of values to be output on the console
	 * @param ratios  - The width of various columns of the table
	 * @param indent  - Indent to start displaying the feed
	 * @param corners - Corners characters for the table
	 * @param vEdge   - Vertical edge characters for the table
	 * @param hEdge   - Horizontal edge characters for the table
	 */
	public static void printTabularFeed(String[] entry, int[] ratios, int indent, char corners, char vEdge,
			char hEdge) {
		final int rows = 2;
		final int cols = entry.length;
		final int unitWidth = 2;
		final int[] WIDTHS = IntStream.range(0, entry.length).map(index -> {
			return ratios[index] * unitWidth;
		}).toArray();
		for (int i = 0; i < rows; i++) {
			IntStream.range(0, indent).forEach(index -> System.out.print('\t'));
			System.out.printf("%c", i % 2 == 1 ? corners : vEdge);

			for (int j = 0; j < cols; j++) {
				if (i % 2 == 1) {
					IntStream.range(0, WIDTHS[j]).forEach(index -> System.out.printf("%c", hEdge));
					System.out.printf("%c", corners);
				} else {
					String s = entry[j];
					s = s.length() > WIDTHS[j] ? s.substring(0, WIDTHS[j] - 5) + "..." : s;
					System.out.printf("%s%" + (WIDTHS[j] - s.length() + 1) + "c", s, vEdge);
				}
			}

			System.out.print('\n');
		}

	}

	/**
	 * This method prints supplied values in tabular form
	 * 
	 * @param title          - An array containing titles for the columns
	 * @param numberOfValues - Number of rows in the table
	 * @param supplier       - A Function functional interface that gets Each row of
	 *                       the table given an index
	 * @param ratios         - The width of various columns of the table
	 * @param indent         - Indent to start displaying the feed
	 * @param corners        - Corners characters for the table
	 * @param vEdge          - Vertical edge characters for the table
	 * @param hEdge          - Horizontal edge characters for the table
	 */
	public static void printTabular(String[] title, int numberOfValues, Function<Integer, String[]> supplier,
			int[] ratios, int indent, char corners, char vEdge, char hEdge) {
		final int rows = numberOfValues * 2 + 3;
		final int cols = title.length;
		final int unitWidth = 2;
		final int[] WIDTHS = IntStream.range(0, title.length).map(index -> {
			return ratios[index] * unitWidth;
		}).toArray();
//		final int WIDTH = IntStream.of(WIDTHS).sum() + title.length + 1;
		for (int i = 0; i < rows; i++) {
			IntStream.range(0, indent).forEach(index -> System.out.print('\t'));
			System.out.printf("%c", i % 2 == 0 ? corners : vEdge);
			String[] values = (i % 2 == 0 || i == 1) ? null : supplier.apply((i - 2) / 2);
			for (int j = 0; j < cols; j++) {
				if (i % 2 == 0) {
					IntStream.range(0, WIDTHS[j]).forEach(index -> System.out.printf("%c", hEdge));
					System.out.printf("%c", corners);
				} else {
					String s = (i == 1) ? title[j] : values[j].trim();
					s = s.length() > WIDTHS[j] ? s.substring(0, WIDTHS[j] - 5) + "..." : s;
					System.out.printf("%s%" + (WIDTHS[j] - s.length() + 1) + "c", s, vEdge);
				}
			}

			System.out.print('\n');
		}

	}

	/**
	 * A variant of the {@link Formatter#getSelectionsAsString(String[]) that
	 * formats the output with a given indent
	 * 
	 * @param options - An array containing the various options for the menu
	 * @param indent  - The indent to format the menu with
	 * @return - The formatted menu as a String
	 * @see Formatter#getSelectionsAsString(String[])
	 * 
	 */
	public default String getStandardOptionsAsString(String[] options, int indent) {
		StringBuilder sb = new StringBuilder("\n");

		for (int i = 0; i < (options.length * 2); i++) {
			IntStream.range(0, indent).forEach(j -> sb.append('\t'));
			if (i % 2 == 1) {
				sb.append("+---+\n");
			} else {
				sb.append(String.format("%c %d %c%s\n", '|', (i + 1) / 2 + 1, '|', options[i / 2]));
			}
		}
		sb.append(getStandardCancelOptionAsString(options, indent));
		sb.append(getStandardSelectionAsString(options, indent));
		return sb.toString();
	}

	/**
	 * Variant of the {@link Formatter#getSelectionAsString(String[], int)
	 * getSelectionsAsString} method
	 * 
	 * @see Formatter#getSelectionsAsString(String[], int)
	 * @param options - An array containing different options of the men
	 * @param indent  - The indentation to format the output with
	 * @return - The formatted selection prompt
	 */
	public default String getStandardSelectionAsString(String[] options, int indent) {
		StringBuilder sb = new StringBuilder();
		IntStream.range(0, indent).forEach(j -> sb.append('\t'));
		sb.append(String.format("%cSelect Options[%d-%d]> ", '|', 1, options.length + (hasCancelOption() ? 1 : 0)));
		return sb.toString();
	}

	/**
	 * Variant of the {@link Formatter#getCancelOptionAsString(String[], int)
	 * getCancelOptionAsString} method
	 * 
	 * @see Formatter#getCancelOptionAsString(String[], int)
	 * @param options - An array containing different options of the men
	 * @param indent  - The indentation to format the output with
	 * @return - The formatted Cancel option
	 */
	public default String getStandardCancelOptionAsString(String[] options, int indent) {
		StringBuffer sb = new StringBuffer();

		IntStream.range(0, 2).forEach(i -> {
			IntStream.range(0, indent).forEach(j -> sb.append('\t'));
			if (i % 2 == 0) {
				sb.append(String.format("%c %d %c%s\n", '|', options.length + 1, '|', getDefaultCancelString()));
			} else {
				sb.append("+---+\n");
			}
		});
		return sb.toString();
	}

	/**
	 * This method retrieves the selection prompt as string in the format
	 * <code>Select options[1-n]></code> where <code>n</code> is the number of
	 * options.
	 * 
	 * @param options - An array containing different options of the menu
	 * @return - The formatted selection prompt
	 */
	public default String getSelectionsAsString(String[] options) {
		return String.format("\nSelect Options[%d-%d]>", 1, options.length + (hasCancelOption() ? 1 : 0));
	}

	/**
	 * An overloaded form of {@link Formatter#getSelectionsAsString(String[])} with
	 * an optional indentation
	 * 
	 * @param options - An array containing different options of the men
	 * @param indent  - The indentation to format the output with
	 * @return - The formatted selection prompt
	 */
	public default String getSelectionsAsString(String[] options, int indent) {
		String ind = "";
		for (int i = 0; i < indent; i++) {
			ind += "\t";
		}
		return String.format("\n%sSelect Options[%d-%d]>", ind, 1, options.length + (hasCancelOption() ? 1 : 0));
	}

	/**
	 * This method retrieves a formatted menu options as string
	 * 
	 * @param options - An array containing the various options for the menu
	 * @return - The formatted menu as a String
	 */
	public default String getOptionsAsString(String[] options) {
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < options.length; i++) {
			s.append(String.format("\n(%d) %s", i + 1, options[i]));
		}
		s.append(getCancelOptionAsString(options));
		s.append(getSelectionsAsString(options));
		return s.toString();
	}

	/**
	 * An overloaded form of {@link Formatter#getOptionsAsString(String[])} with an
	 * optional indentation
	 * 
	 * @param options - An array containing different options of the menu
	 * @param indent  - The indentation to format the output with
	 * @return - The formatted menu as a String
	 */
	public default String getOptionsAsString(String[] options, int indent) {
		String ind = "";
		for (int i = 0; i < indent; i++) {
			ind += "\t";
		}
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < options.length; i++) {
			s.append(String.format("\n%s(%d) %s", ind, i + 1, options[i]));
		}
		s.append(getCancelOptionAsString(options, indent));
		s.append(getSelectionsAsString(options, indent));
		return s.toString();
	}

	/**
	 * Gets the Header
	 * 
	 * @return - The header of title of the implementing class
	 */
	public abstract String getHeaderAsString();

	public default String getCancelOptionAsString(String[] options) {
		return hasCancelOption() ? String.format("\n(%d) %s", options.length + 1, getDefaultCancelString()) : "";
	}

	/**
	 * An overloaded form of the
	 * {@link Formatter#getCancelOptionAsString(String[])getCancelOptionAsString}
	 * method with optional indentation
	 * 
	 * @see Formatter#getCancelOptionAsString(String[])
	 * @param options - An array containing different options of the men
	 * @param indent  - The indentation to format the output with
	 * @return - The formatted Cancel option
	 */
	public default String getCancelOptionAsString(String[] options, int indent) {
		String ind = "";
		for (int i = 0; i < indent; i++) {
			ind += "\t";
		}
		return hasCancelOption() ? String.format("\n%s(%d) %s", ind, options.length + 1, getDefaultCancelString()) : "";
	}

	/**
	 * A predicate method to determine if the cancel option will be displayed
	 * 
	 * @return - True if the cancel option should be displayed or otherwise
	 */
	public default boolean hasCancelOption() {
		return true;
	}

	/**
	 * Gets the Cancel option display string
	 * 
	 * @return - The Cancel option as String
	 */
	public default String getDefaultCancelString() {
		return "Logout";
	}

	/**
	 * This method displays the supplied String in a boxed format
	 * 
	 * @param string  - The String to be displayed
	 * @param indent  - The indentation of the display
	 * @param corners - Corners characters for the table
	 * @param vEdge   - Vertical edge characters for the table
	 * @param hEdge   - Horizontal edge characters for the table
	 */
	public static void printBoxed(String string, int indent, char corners, char vEdge, char hEdge) {
		final int width = string.length() + (((string.length() + 1) % 2) + 3);
		for (int i = 0; i < 3; i++) {
			System.out.print('\n');
			IntStream.range(0, indent).forEach(k -> System.out.print('\t'));
			for (int j = 0; j < width; j++) {
				if (i == 0 || i == 2) {
					System.out.printf("%c", (j == 0 || j == (width - 1)) ? corners : hEdge);
				} else {
					System.out.printf("%c %s%" + (width - 2 - string.length()) + "c", vEdge, string, vEdge);
					break;
				}
			}
		}
	}

	/**
	 * A variant of the {@link Formatter#printBoxed(String, int, char, char, char)
	 * printBoxed} method with a title text.
	 * 
	 * @see Formatter#printBoxed(String, int, char, char, char)
	 * @param title   - The title to display with the string
	 * @param string  - The String to be displayed
	 * @param indent  - The indentation of the display
	 * @param corners - Corners characters for the table
	 * @param vEdge   - Vertical edge characters for the table
	 * @param hEdge   - Horizontal edge characters for the table
	 * @param skip    - Number of spaces to skip while printing the enclosing box
	 */
	public static void printBoxedTitled(String title, String string, int indent, char corners, char vEdge, char hEdge,
			int skip) {
		final int width = string.length() + (((string.length() + 1) % 2) + 3);
		final int width1 = title.length() + (((title.length() + 0) % 2) + 3);
		for (int i = 0; i < 3; i++) {
			System.out.print('\n');
			IntStream.range(0, indent).forEach(k -> System.out.print('\t'));
			for (int j = 0; j < width + width1; j++) {
				if (i == 0 || i == 2) {
					System.out.printf("%c",
							(skip == 0 || j % (skip + 1) == 0)
									? ((j == 0 || j == (width1 - 1) || (j == width + width1 - 1)) ? corners : hEdge)
									: '\0');
				} else {
					System.out.printf(
							"%c %s%" + (width1 - 2 - title.length()) + "c %s%" + (width - 1 - string.length()) + "c",
							vEdge, title, vEdge, string, vEdge);
					break;
				}
			}
		}
	}

	/**
	 * A specialized variant of the
	 * {@link Formatter#printBoxedTitled(String, String, int, char, char, char, int)
	 * printBoxedTitled} method that displays an Error message with default title
	 * and corner and edge characters
	 * 
	 * @param msg    - Error message to be displayed
	 * @param indent - The indentation of the display
	 */
	public static void printError(String msg, int indent) {
		printBoxedTitled("Error", msg, indent, '*', '*', '*', 1);
	}
}
