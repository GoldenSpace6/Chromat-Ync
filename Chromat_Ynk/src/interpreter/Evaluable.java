package interpreter;

import java.util.HashMap;


public abstract class Evaluable {
	public abstract Object eval(HashMap<String, UserObjectValue> variableNameList) throws InterpreterException;
	public abstract VariableType getReturnType(HashMap<String, UserObjectValue> variableNameList) throws InterpreterException;
	//Construct Expression from str
	public static Evaluable newEvaluable(String str, OperatorType operatorType) throws InterpreterException {
	    //Split str with reg as separator but keep them in the split array
		String reg;
		if(operatorType==null) {
			throw new InterpreterException("No Operator given");
		}
		switch (operatorType) {
		case BoolOperator:
			reg="&&|!|\\|\\|";
			break;
		case NumComparator:
			reg="==|<|<=|>|>=";
			break;
		case NumAdder:
			reg="\\+|-";
			break;
		case NumMultiplier:
			reg="\\*|\\/";
			break;
		default:
			throw new IllegalArgumentException("Unexpected value: " + operatorType);
		}
		//split the empty String before and after reg
	    String[] expressionArray = str.split("((?="+reg+")|(?<="+reg+"))");
	    if(operatorType==OperatorType.NumAdder) {
	    	//only split "-" character if it follows a alphanumeric character
	    	expressionArray = str.split("(?=-)(?<=[A-Za-z0-9])|(?<=([A-Za-z0-9]-)|\\+)|(?=\\+)");
	    }
	    if(expressionArray.length==1) {
		    switch (operatorType) {
			case BoolOperator:
		    	return Evaluable.newEvaluable(str, OperatorType.NumComparator);
		    case NumComparator:
		    	return new UserObjectValue(str,VariableType.BOOL);//Explicit Value or UserValue
			case NumAdder:
		    	return Evaluable.newEvaluable(str, OperatorType.NumMultiplier);
		    case NumMultiplier:
		    	return new UserObjectValue(str,VariableType.NUM);//Explicit Value or UserValue
			default:
				throw new IllegalArgumentException("Unexpected value: " + operatorType);
			}
	    }
	    return new Expression(expressionArray,operatorType);
	}
	//Search next Operator to divide str
	public static Evaluable nextNewEvaluable(String str, OperatorType operatorType) throws InterpreterException {
		OperatorType next = operatorType.nextOperatorType();
		if(next==null) {
			return newEvaluable(str,operatorType.nextVariableType());
		}
		return newEvaluable(str, next);
	}
	public static Evaluable newEvaluable(String str, VariableType variableType) throws InterpreterException {
		switch (variableType) {
		case BOOL: return newEvaluable(str,OperatorType.BoolOperator);
		case NUM: return newEvaluable(str,OperatorType.NumAdder);
		case STR: return new UserObjectValue(str,variableType);
		case VAR: return new UserObjectValue(str,variableType);
		default:
			throw new IllegalArgumentException("Unexpected value: " + variableType);
		}
	}
}
