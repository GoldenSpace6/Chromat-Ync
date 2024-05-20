package lexer;
import java.util.HashMap;

public class BoolEvaluate {
	//if user put TRUE or FALSE
	boolean explicitValue;
	//if user put a variable name
	String variable;
	//if user put a comparison
	NumEvaluate left;//value left; 
	NumEvaluate right;//value right;
	NumComparator op;
	BoolEvaluate(Boolean b) {
		this.explicitValue=b;
		this.variable=null;
		this.left=null;
		this.right=null;
		this.op=null;
	}
	BoolEvaluate(String s) {
		this.explicitValue=false;
		this.variable=s;
		this.left=null;
		this.right=null;
		this.op=null;
	}
	BoolEvaluate(NumEvaluate l,NumComparator op,NumEvaluate r) {
		this.explicitValue=false;
		this.variable=null;
		this.left=l;//value left; 
		this.right=r;//value right;
		this.op=op;
	}
	@Override
	public String toString() {
		if(variable!=null) {
			return variable.toString();
		}
		if(op!=null) {
			return left.toString() +" "+ op.toString() +" "+ right.toString();
		}
		if(explicitValue) {
			return "TRUE";
		}
		return "FALSE";
	}
	boolean eval(HashMap<String, UserVariable> variableList) {
		if(variable!=null) {
			if (variableList.get(this.variable).type!="BOOL") {
				return false;//trow "Expected Boolean but got"+variableList.get(this.variable).type";
			}
			return (Boolean) variableList.get(this.variable).value;
		}
		if(op!=null) {
			switch (op) {
			case EQUALS: {
				return left.eval(variableList) == right.eval(variableList);
			}
			case LESS: {
				return left.eval(variableList) < right.eval(variableList);
			}
			case LESSEQUALS: {
				return left.eval(variableList) <= right.eval(variableList);
			}
			case GREATER: {
				return left.eval(variableList) > right.eval(variableList);
			}
			case GREATEREQUALS: {
				return left.eval(variableList) >= right.eval(variableList);
			}
			default:
				throw new IllegalArgumentException("Unexpected value: " + op);
			}
		}
		return explicitValue;
	};

}
