package ie.atu.sw.os.reqres;

public interface Formatter {
	public default String getSelectionsAsString(String[] options) {
		return String.format("\nSelect Options[%d-%d]>", 1, options.length + (hasCancelOption() ? 1 : 0));
	}
	public default String getSelectionsAsString(String[] options, int indent) {
		String ind = "";
		for(int i = 0; i < indent; i++) {
			ind += "\t";
		}
		return String.format("\n%sSelect Options[%d-%d]>", ind, 1, options.length + (hasCancelOption() ? 1 : 0));
	}
	public default String getOptionsAsString(String[] options) {
		StringBuilder s = new StringBuilder();
		for(int i = 0; i < options.length; i++) {
			s.append(String.format("\n(%d) %s", i + 1, options[i]));
		}
		s.append(getCancelOptionAsString(options));
		s.append(getSelectionsAsString(options));
		return s.toString();
	}
	public default String getOptionsAsString(String[] options, int indent) {
		String ind = "";
		for(int i = 0; i < indent; i++) {
			ind += "\t";
		}
		StringBuilder s = new StringBuilder();
		for(int i = 0; i < options.length; i++) {
			s.append(String.format("\n%s(%d) %s", ind, i + 1, options[i]));
		}
		s.append(getCancelOptionAsString(options , indent));
		s.append(getSelectionsAsString(options, indent));
		return s.toString();
	}
	public abstract String getHeaderAsString();
	public default String getCancelOptionAsString(String[] options) {
		return hasCancelOption() ? 
				String.format("\n(%d) %s", options.length + 1, getDefaultCancelString())
				:
				"";
	}
	public default String getCancelOptionAsString(String[] options, int indent) {
		String ind = "";
		for(int i = 0; i < indent; i++) {
			ind += "\t";
		}
		return hasCancelOption() ? 
				String.format("\n%s(%d) %s", ind, options.length + 1, getDefaultCancelString())
				:
				"";
	}
	public default boolean hasCancelOption() {
		return true;
	}
	
	public default String getDefaultCancelString() {
		return "Cancel";
	}
}
