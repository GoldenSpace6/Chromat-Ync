package lexer;

public enum BoolOperator {
	AND,OR,DIF;
	public static BoolOperator fromString(String s) {
		switch (s) {
		case "&&": {return AND;}
		case "||": {return OR;}
		case "!" : {return DIF;}
		default:
			throw new IllegalArgumentException("Unexpected value: " + s);
		}
	}
	

}
