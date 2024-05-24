package interpreter;


public enum Operator {
	AND,OR,DIF, EQUALS,LESS,LESSEQUALS,GREATER,GREATEREQUALS;
	//convert to Operator
	public static Operator fromString(String s) {
		switch (s) {
		case "&&": {return AND;}
		case "||": {return OR;}
		case "!" : {return DIF;}
		
		case "==": {return EQUALS;}
		case "<" : {return LESS;}
		case "<=": {return LESSEQUALS;}
		case ">" : {return GREATER;}
		case ">=": {return GREATEREQUALS;}
		default:
			throw new IllegalArgumentException("Unexpected value: " + s);
		}
	}
	//return true if String can be converted to Operator
	public static boolean isFromString(String s) {
		try {
			fromString(s);
			return true; 
		} catch (IllegalArgumentException e) {
			return false;
		}
	}
	public OperatorType ExpressionType() {
		switch (this) {
		case AND:
		case OR:
		case DIF: {return OperatorType.BoolOperator;}

		case EQUALS:
		case LESS:
		case LESSEQUALS:
		case GREATER:
		case GREATEREQUALS: {return OperatorType.NumComparator;}
		default:
			throw new IllegalArgumentException("Unexpected value: " + this);
		}
	}
}
