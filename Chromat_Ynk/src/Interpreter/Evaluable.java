package Interpreter;

import java.util.HashMap;


public abstract class Evaluable {
	public abstract Object eval(HashMap<String, UserObjectValue> variableNameList);
	public abstract VariableType getReturnType();
	//Construct Expression from str
	public static Evaluable newEvaluable(String str, OperatorType operatorType) {
	    //Split str with reg as separator but keep them in the split array
		String reg;
		if(operatorType==null) {
			System.out.println("No Operator given");
			//throw No Operator given
			return null;// new UserObjectValue(str,operatorType);
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
	public static Evaluable nextNewEvaluable(String str, OperatorType operatorType) {
		OperatorType next = operatorType.nextOperatorType();
		if(next==null) {
			return newEvaluable(str,operatorType.nextVariableType());
		}
		return newEvaluable(str, next);
	}
	public static Evaluable newEvaluable(String str, VariableType variableType) {
		switch (variableType) {
		case BOOL: return newEvaluable(str,OperatorType.BoolOperator);
		case NUM: return new UserObjectValue(str,variableType);
		case STR: return new UserObjectValue(str,variableType);
		default:
			throw new IllegalArgumentException("Unexpected value: " + variableType);
		}
	}
}
