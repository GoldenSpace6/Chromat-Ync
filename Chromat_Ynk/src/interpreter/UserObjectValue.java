package interpreter;

import java.util.HashMap;


public class UserObjectValue extends Evaluable {
	private VariableType type;
	private Object value;
	private boolean isPercentage;

	public UserObjectValue(String str, VariableType variableType) throws InterpreterException {
		type=variableType;
		isPercentage = false;
		if(isANumber(str) && variableType==VariableType.NUM) {
			isPercentage = false;
			value=Double.valueOf(str);		
		} else if(isABoolean(str) && variableType==VariableType.BOOL) {
			value=str.equals("TRUE");		
		} else if(isAString(str) && variableType==VariableType.STR) {
			//Can't Support Special/Operator character: !,||,&&,+,<...
			//remove quote at start and end of string
			value=str.replaceAll("\"", "");//temporary
			//value=str.replaceAll("\"|'", "");
		} else if(isAVariable(str) && variableType==VariableType.VAR) {
			type=VariableType.STR;
			value=str;
		} else if(isAVariable(str)) {//&& variableType==VariableType.VAR
			type=VariableType.VAR;
			value=str;
		} else if (str.length()>0 && variableType==VariableType.NUM) {	// test if NUM percentage
			char lastChar = str.charAt(str.length()-1);
			if (lastChar =='%') {
				isPercentage = true;
				value=Double.valueOf(str.substring(0, str.length()-1));			
			}
		} else {
			throw new InterpreterException("Expected Variable or Value but got "+str+" with type "+variableType);
		}
	}
	public UserObjectValue(Object value, VariableType variableType, String str) {
		if (variableType==VariableType.NUM && str.length()>0) {
			char lastChar = str.charAt(str.length()-1);
			if (lastChar =='%') {
				isPercentage = true;
				this.value = value;;			
			} else {
				isPercentage = false;
				this.value = value;
			}
		} else {
		this.value=value;
		}
		this.type=variableType;
	}
	public boolean getIsPercentage() {
		return isPercentage;
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
	//get specific type ----
	public double getDouble() throws InterpreterException {
		if(type==VariableType.NUM) {return (double) value;}
		throw new InterpreterException("is not type NUM");
	}
	public int getInt() throws InterpreterException {
		if(type==VariableType.NUM) {return (int) Math.round((double) value);}
		throw new InterpreterException("is not type NUM");
	}
	public boolean getBoolean() throws InterpreterException {
		if(type==VariableType.BOOL) {return (boolean) value;}
		throw new InterpreterException("is not type BOOL");
	}
	public String getString() throws InterpreterException {
		if(type==VariableType.STR || type==VariableType.VAR) {return (String) value;}
		throw new InterpreterException("is not type STR or VAR");
	}
	//----
	public VariableType getReturnType(HashMap<String, UserObjectValue> variableNameList) throws InterpreterException {
		if(type==VariableType.VAR) {
			if(variableNameList.containsKey((String) value)==false) {
				throw new InterpreterException("Variable "+value+" doesn't exist");
    		}
			return variableNameList.get(value).getReturnType(variableNameList);
		}
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
		return str.startsWith("\"") && str.endsWith("\"");
		//return str.startsWith("\"|'") && str.endsWith("\"|'");
	}
	//return true if str represent a Variable
	public static boolean isAVariable(String s) {
		//Variable can only contains letters
		return s.matches("^[a-zA-Z]*$");
	}
	@Override
	public String toString() {
		return getValue().toString();
	}
	//evaluate the value depending on the variables given
	@Override
	public Object eval(HashMap<String, UserObjectValue> variableNameList) throws InterpreterException {
		if(type==VariableType.VAR) {
			if(variableNameList.containsKey((String) value)==false) {
				throw new InterpreterException("Variable "+value+" doesn't exist");
    		}
			return variableNameList.get(value).getValue();
		}
		return getValue();
	}
}