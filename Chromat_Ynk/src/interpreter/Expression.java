package interpreter;

import java.util.HashMap;


public class Expression extends Evaluable{
	//represent a single block of Operation of same type and level : ["a/b","+","5","-","28"]
	Evaluable[] evaluableList;
	Operator[] operators;
	OperatorType operatorType;

	public Expression(String[] expressionArray,OperatorType operatorType) throws InterpreterException {
		//str is an Expression as a String :"a==b"
		this.evaluableList = new Evaluable[expressionArray.length/2+1];
		this.operators = new Operator[expressionArray.length/2];
		this.operatorType = operatorType;
		if(this.operatorType==OperatorType.NumComparator && expressionArray.length!=3) {
			throw new InterpreterException("NumComparator can only compare 2 numbers");
		}
		
		Operator currentOp;
		evaluableList[0]= Evaluable.nextNewEvaluable(expressionArray[0], operatorType);
		for(int i=2; i<expressionArray.length; i+=2) {
			currentOp=Operator.fromString(expressionArray[i-1]);
			if(currentOp.ExpressionType()!=this.operatorType) {
				throw new InterpreterException("bad use of Expression(), "+currentOp+" is not of type "+operatorType);
			}
			operators[i/2-1] = currentOp;
			evaluableList[i/2] = Evaluable.nextNewEvaluable(expressionArray[i], operatorType);
		}
	}
	@Override
	public String toString() {
		if (evaluableList.length==0 || evaluableList[0]==null) {
			return "Error";
			//throw new InterpreterException("Expression can't be empty");
		}
		String ret = evaluableList[0].toString()+" ";
		for(int i=1; i<evaluableList.length;i++) {
			if (evaluableList[i]==null) {
				return "Error";
			}
			ret += operators[i-1].toString() +" "+ evaluableList[i].toString()+" ";
		}
		return ret;
	}
	//evaluate the expression depending on the variables given
	@Override
	public Object eval(HashMap<String, UserObjectValue> variableNameList) throws InterpreterException {
		if (evaluableList.length==0) {
			throw new InterpreterException("Expression can't be empty");
		}
		//check on operatorType to know what type of operation to do
		switch (operatorType) {
		case BoolOperator:
			//verify all entry are Boolean
			if (evaluableList[0].getReturnType()!=VariableType.BOOL) {
				throw new InterpreterException("Expected type BOOL but got"+evaluableList[0].getReturnType());
			}
			boolean ret = (boolean) evaluableList[0].eval(variableNameList);
			for(int i=1; i<evaluableList.length;i++) {

				if (evaluableList[i].getReturnType()!=VariableType.BOOL) {
					throw new InterpreterException("Expected type BOOL but got"+evaluableList[i].getReturnType());
				}
				switch (operators[i-1]) {
				case AND:
					ret = ret && (boolean)evaluableList[i].eval(variableNameList);
					break;
				case OR:
					ret = ret || (boolean)evaluableList[i].eval(variableNameList);
					break;
				case DIF:
					ret = ret != (boolean)evaluableList[i].eval(variableNameList);
					break;
				default:
					throw new IllegalArgumentException("Unexpected value: " + operators[i-1]);
				}
			}
			return ret;
		case NumComparator:
			//verify it is comparing 2 numbers
			if (evaluableList[0].getReturnType()!=VariableType.NUM && evaluableList[1].getReturnType()!=VariableType.NUM) {
				throw new InterpreterException("Expected type NUM but got"+evaluableList[0].getReturnType()+"and"+evaluableList[1].getReturnType());
			}
			switch (operators[0]) {
			case EQUALS:
				return (double)evaluableList[0].eval(variableNameList) == (double)evaluableList[1].eval(variableNameList);
			case LESS:
				return (double)evaluableList[0].eval(variableNameList) < (double)evaluableList[1].eval(variableNameList);
			case LESSEQUALS:
				return (double)evaluableList[0].eval(variableNameList) <= (double)evaluableList[1].eval(variableNameList);
			case GREATER:
				return (double)evaluableList[0].eval(variableNameList) > (double)evaluableList[1].eval(variableNameList);
			case GREATEREQUALS:
				return (double)evaluableList[0].eval(variableNameList) >= (double)evaluableList[1].eval(variableNameList);
			default:
				throw new IllegalArgumentException("Unexpected value: " + operators[0]);
			}
		case NumAdder:
			//verify all entry are NUM
			if (evaluableList[0].getReturnType()!=VariableType.NUM) {
				throw new InterpreterException("Expected type NUM but got"+evaluableList[0].getReturnType());
			}
			double ret2 = (double) evaluableList[evaluableList.length-1].eval(variableNameList);
			for(int i=evaluableList.length-2; i>=0;i--) {
				if (evaluableList[i].getReturnType()!=VariableType.NUM) {
					throw new InterpreterException("Expected type NUM but got"+evaluableList[i].getReturnType());
				}
				switch (operators[i]) {
				case ADD:
					ret2 = (double)evaluableList[i].eval(variableNameList) + ret2;
					break;
				case SUBSTRACT:
					ret2 = (double)evaluableList[i].eval(variableNameList) - ret2;
					break;
				default:
					throw new IllegalArgumentException("Unexpected value: " + operators[i]);
				}
			}
			return ret2;
		case NumMultiplier:
			//verify all entry are NUM
			if (evaluableList[0].getReturnType()!=VariableType.NUM) {
				throw new InterpreterException("Expected type NUM but got"+evaluableList[0].getReturnType());
			}
			double ret3 = (double) evaluableList[evaluableList.length-1].eval(variableNameList);
			for(int i=evaluableList.length-2; i>=0;i--) {
				if (evaluableList[i].getReturnType()!=VariableType.NUM) {
					throw new InterpreterException("Expected type NUM but got"+evaluableList[i].getReturnType());
				}
				switch (operators[i]) {
				case MULTIPLY:
					ret3 = (double)evaluableList[i].eval(variableNameList) * ret3;
					break;
				case DIVIDE:
					ret3 = (double)evaluableList[i].eval(variableNameList) / ret3;
					break;
				default:
					throw new IllegalArgumentException("Unexpected value: " + operators[i]);
				}
			}
			return ret3;
		default:
			throw new IllegalArgumentException("Unexpected value: " + operatorType);
		}
	}
	//return type of value expected to be returned
	@Override
	public VariableType getReturnType() {
		switch (operatorType) {
		case BoolOperator:
		case NumComparator:
			return VariableType.BOOL;
		case NumAdder:
		case NumMultiplier:
			return VariableType.NUM;
		default:
			throw new IllegalArgumentException("Unexpected value: " + operatorType);
		}
	}
}
