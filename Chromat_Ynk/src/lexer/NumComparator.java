package lexer;

public enum NumComparator {
	EQUALS,LESS,LESSEQUALS,GREATER,GREATEREQUALS;
	public static NumComparator fromString(String s) {
		switch (s) {
		case "==": {return EQUALS;}
		case "<" : {return LESS;}
		case "<=": {return LESSEQUALS;}
		case ">" : {return GREATER;}
		case ">=": {return GREATEREQUALS;}
		default:
			throw new IllegalArgumentException("Unexpected value: " + s);
		}
	}
	//return true if String can be converted to NumComparator
	public static boolean isFromString(String s) {
		return (s.equals("==") || s.equals("<") || s.equals("<=") || s.equals(">") || s.equals(">=") );
	}
}
