package Interpreter;

import java.util.HashMap;


public class Expression extends Evaluable{
	//represent a single block of Operation of same type and level : ["a/b","+","5","-","28"]
	Evaluable[] evaluableList;
	Operator[] operators;
	OperatorType operatorType;

	public Expression(String[] expressionArray,OperatorType operatorType) {
		//str is an Expression as a String :"a==b"
		this.evaluableList = new Evaluable[expressionArray.length/2+1];
		this.operators = new Operator[expressionArray.length/2];
		this.operatorType = operatorType;
		if(this.operatorType==OperatorType.NumComparator && expressionArray.length!=3) {
			System.out.print("NumComparator can only compare 2 numbers");
			return; //throw "NumComparator can only compare 2 numbers"
		}
		
		Operator currentOp;
		evaluableList[0]= Evaluable.nextNewEvaluable(expressionArray[0], operatorType);
		for(int i=2; i<expressionArray.length; i+=2) {
			currentOp=Operator.fromString(expressionArray[i-1]);
			if(currentOp.ExpressionType()!=this.operatorType) {
				System.out.print("bad use of Expression(), "+currentOp+" is not of type "+operatorType);
				return; //throw "bad use of Expression(), "+currentOp+" is not of type "+operatorType
			}
			operators[i/2-1] = currentOp;
			evaluableList[i/2] = Evaluable.nextNewEvaluable(expressionArray[i], operatorType);
		}
	}
	@Override
	public String toString() {
		if (evaluableList.length==0 || evaluableList[0]==null) {
			System.out.print("Expression can't be empty");
			return "";//throw "Expression can't be empty";
		}
		String ret = evaluableList[0].toString()+" ";
		for(int i=1; i<evaluableList.length;i++) {
			if (evaluableList[i]==null) {
				System.out.print("Expression can't be empty");
				return "";//throw "Expression can't be empty";
			}
			ret += operators[i-1].toString() +" "+ evaluableList[i].toString()+" ";
		}
		return ret;
	}
	//evaluate the expression depending on the variables given
	@Override
	public Object eval(HashMap<String, UserObjectValue> variableNameList) {
		if (evaluableList.length==0) {
			System.out.print("Expression can't be empty");
			return "";//throw "Expression can't be empty";
		}
		//check on operatorType to know what type of operation to do
		switch (operatorType) {
		case BoolOperator:
			//verify all entry are Boolean
			if (evaluableList[0].getReturnType()!=VariableType.BOOL) {
				System.out.print("Expected type BOOL but got\"+evaluableList[0].getReturnType()+\"and\"+evaluableList[1].getReturnType()");
				return false;//throw "Expected type INT but got"+evaluableList[0].getReturnType()+"and"+evaluableList[1].getReturnType()
			}
			boolean ret = (boolean) evaluableList[0].eval(variableNameList);
			for(int i=1; i<evaluableList.length;i++) {

				if (evaluableList[i].getReturnType()!=VariableType.BOOL) {
					System.out.print("Expected type BOOL but got\"+evaluableList[0].getReturnType()+\"and\"+evaluableList[1].getReturnType()");
					return false;//throw "Expected type INT but got"+evaluableList[0].getReturnType()+"and"+evaluableList[1].getReturnType()
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
				System.out.print("Expected type INT but got"+evaluableList[0].getReturnType()+"and"+evaluableList[1].getReturnType());
				return false;//throw "Expected type INT but got"+evaluableList[0].getReturnType()+"and"+evaluableList[1].getReturnType()
			}
			switch (operators[0]) {
			case EQUALS:
				return (int)evaluableList[0].eval(variableNameList) == (int)evaluableList[1].eval(variableNameList);
			case LESS:
				return (int)evaluableList[0].eval(variableNameList) < (int)evaluableList[1].eval(variableNameList);
			case LESSEQUALS:
				return (int)evaluableList[0].eval(variableNameList) <= (int)evaluableList[1].eval(variableNameList);
			case GREATER:
				return (int)evaluableList[0].eval(variableNameList) > (int)evaluableList[1].eval(variableNameList);
			case GREATEREQUALS:
				return (int)evaluableList[0].eval(variableNameList) >= (int)evaluableList[1].eval(variableNameList);
			default:
				throw new IllegalArgumentException("Unexpected value: " + operators[0]);
			}

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
		default:
			throw new IllegalArgumentException("Unexpected value: " + operatorType);
		}
	}
}
