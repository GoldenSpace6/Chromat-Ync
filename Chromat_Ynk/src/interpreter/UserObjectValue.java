package interpreter;

import java.util.HashMap;


public class UserObjectValue extends Evaluable {
	private VariableType type;
	private Object value;
	public UserObjectValue(String str, VariableType variableType) {
		type=variableType;
		if(isANumber(str) && variableType==VariableType.NUM) {
			value=Double.valueOf(str);		
		} else if(isABoolean(str) && variableType==VariableType.BOOL) {
			value=str.equals("TRUE");		
		} else if(isAString(str) && variableType==VariableType.STR) {
			//Can't Support Special/Operator character: !,||,&&,+,<...
			//remove quote at start and end of string
			value=str.replaceAll("STR", "");//temporary
			//value=str.replaceAll("\"|'", "");
		} else if(isAVariable(str)) {//&& variableType==VariableType.VAR
			type=VariableType.VAR;
			value=str;
		} else {
			System.out.println("Expected Variable or Value but got "+str+" with type "+variableType);
			//throw "Expected Variable or Value but got"+str 
		}
	}
	public UserObjectValue(Object value, VariableType variableType) {
		 this.value=value;
		 this.type=variableType;
	}
	public Object getValue() {
		switch (type) {
		case NUM: return (double) value;
		case STR: return (String) value;
		case BOOL:return (boolean) value;
		case VAR: return (String) value;
		default:
			throw new IllegalArgumentException("Unexpected value: " + type);
		}
	}
	public VariableType getReturnType() {
		return type;
	}
	//return true if str represent a Boolean
	private boolean isABoolean(String str) {
		return str.equals("TRUE") || str.equals("FALSE");
	}
	//try to convert s to int, return false if it failed
	public static boolean isANumber(String s) {
		try {
			Double.valueOf(s);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}
	//return true if str represent a String
	private boolean isAString(String str) {
		return str.startsWith("STR");
		//return str.startsWith("\"|'") && str.endsWith("\"|'");
	}
	//return true if str represent a Variable
	public static boolean isAVariable(String s) {
		//Variable are only made of letter
		return s.matches("^[a-zA-Z]*$");
	}
	@Override
	public String toString() {
		return getValue().toString();
	}
	//evaluate the value depending on the variables given
	@Override
	public Object eval(HashMap<String, UserObjectValue> variableNameList) {
		if(type==VariableType.VAR) {
			if(variableNameList.containsKey((String) value)==false) {
    			System.out.println("Variable "+value+" doesn't exist");
    			//throw varName+" doesn't exist"
    			return 0;
    		}
			return variableNameList.get(value).getValue();
		}
		return getValue();
	}
}
