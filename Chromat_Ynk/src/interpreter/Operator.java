package interpreter;


public enum Operator {
	AND,OR,DIF, EQUALS,LESS,LESSEQUALS,GREATER,GREATEREQUALS, ADD,SUBSTRACT, MULTIPLY,DIVIDE;
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

		case "+": {return ADD;}
		case "-" : {return SUBSTRACT;}
		case "*": {return MULTIPLY;}
		case "/" : {return DIVIDE;}
		default:
			throw new IllegalArgumentException("Unexpected value: " + s);
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

		case ADD:
		case SUBSTRACT: {return OperatorType.NumAdder;}
		case MULTIPLY:
		case DIVIDE: {return OperatorType.NumMultiplier;}
		
		default:
			throw new IllegalArgumentException("Unexpected value: " + this);
		}
	}
}
