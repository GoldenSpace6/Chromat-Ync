package lexer;
import java.util.HashMap;

public class NumEvaluate {
	//if User put a number
	int explicitValue;
	//if User put a variable
	String variableName;
	
	public NumEvaluate(int b) {
		this.explicitValue=b;
		this.variableName=null;
	}
	public NumEvaluate(String s) {
		this.explicitValue=0;
		this.variableName=s;
	}
	public int eatValue(String[] s,int head) {
		if (isANumber(s[head])) {
			this.explicitValue = Integer.valueOf(s[head]);;
			this.variableName = null;
			return head+1;
			
		} else if (isAVariable(s[head])) {
			this.explicitValue = 0;
			this.variableName = s[head];
			return head+1;
		}//else is an expression ["5","+","a"]
		
		return 0;//trow "Expected type NUM but got"+s[head];
	}
	public static boolean isANumber(String s) {
		try {
			Integer.valueOf(s);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}
	public static boolean isAVariable(String s) {
		//Variable are only made of letter
		return s.matches("^[a-zA-Z]*$");
	}
	@Override
	public String toString() {
		if(variableName!=null) {
			return variableName;
		}
		return String.valueOf(explicitValue);
	}
	public int eval(HashMap<String, UserVariable> variableNameList) {
		if(variableName!=null) {
			if (variableNameList.get(this.variableName).type!="BOOL") {
				return 0;//trow "Expected Boolean but got"+variableNameList.get(this.variableName).type";
			}
			return (Integer) variableNameList.get(this.variableName).value;
		}
		return explicitValue;
	};

}
