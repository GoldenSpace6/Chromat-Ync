package interpreter;

import java.util.HashMap;


public abstract class Evaluable {
	public abstract Object eval(HashMap<String, UserObjectValue> variableNameList) throws InterpreterException;
	public abstract VariableType getReturnType();
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
		default:
			throw new IllegalArgumentException("Unexpected value: " + operatorType);
		}
	    String[] expressionArray = str.split("((?="+reg+")|(?<="+reg+"))");
	    if(expressionArray.length==1 && operatorType == OperatorType.BoolOperator) {
	    	return Evaluable.newEvaluable(str, OperatorType.NumComparator);
	    }
	    if(expressionArray.length==1 && operatorType == OperatorType.NumComparator) {
	    	return new UserObjectValue(str,VariableType.BOOL);//Explicit Value or UserValue
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
		case NUM: return new UserObjectValue(str,variableType);
		case STR: return new UserObjectValue(str,variableType);
		case VAR: return new UserObjectValue(str,variableType);
		default:
			throw new IllegalArgumentException("Unexpected value: " + variableType);
		}
	}
}
