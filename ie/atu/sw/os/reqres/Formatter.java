package ie.atu.sw.os.reqres;

import java.util.stream.IntStream;

public interface Formatter {
	public default String getSelectionsAsString(String[] options) {
		return String.format("\nSelect Options[%d-%d]>", 1, options.length + (hasCancelOption() ? 1 : 0));
	}

	public default String getSelectionsAsString(String[] options, int indent) {
		String ind = "";
		for (int i = 0; i < indent; i++) {
			ind += "\t";
		}
		return String.format("\n%sSelect Options[%d-%d]>", ind, 1, options.length + (hasCancelOption() ? 1 : 0));
	}

	public default String getOptionsAsString(String[] options) {
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < options.length; i++) {
			s.append(String.format("\n(%d) %s", i + 1, options[i]));
		}
		s.append(getCancelOptionAsString(options));
		s.append(getSelectionsAsString(options));
		return s.toString();
	}

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

	public abstract String getHeaderAsString();

	public default String getCancelOptionAsString(String[] options) {
		return hasCancelOption() ? String.format("\n(%d) %s", options.length + 1, getDefaultCancelString()) : "";
	}

	public default String getCancelOptionAsString(String[] options, int indent) {
		String ind = "";
		for (int i = 0; i < indent; i++) {
			ind += "\t";
		}
		return hasCancelOption() ? String.format("\n%s(%d) %s", ind, options.length + 1, getDefaultCancelString()) : "";
	}

	public default boolean hasCancelOption() {
		return true;
	}

	public default String getDefaultCancelString() {
		return "Cancel";
	}

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
}
