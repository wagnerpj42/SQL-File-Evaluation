package sqlfe.general;

public class SQLFEParseException extends Exception { 
	private static final long serialVersionUID = 1L;

	public SQLFEParseException (String errorMessage) {
		super(errorMessage);
	}
}
