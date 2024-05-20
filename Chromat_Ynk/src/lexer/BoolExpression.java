package lexer;
import java.util.ArrayList;
import java.util.HashMap;

public class BoolExpression {
	//represent each block between boolOperator(and,or,dif)
	ArrayList<BoolEvaluate> boolList;
	ArrayList<BoolOperator> boolOperators;
	String[] str;
	
	public BoolExpression(String[] str) {
		this.boolList = new ArrayList<>();
		this.boolOperators = new ArrayList<>();
		this.str=str;//["a","==","b"]
	}
	void lexer() {
		for(int i=eatTerm(0); i<str.length; i++) {
			boolOperators.add(BoolOperator.fromString(str[i]));
			i++;
			i=eatTerm(i)-1;
		}
	}
	int eatTerm(int head) {
		if( str[head].equals("TRUE") || str[head].equals("FALSE")) {
			boolList.add(new BoolEvaluate(str[head]=="TRUE"));
			return head+1;
		}
		if (head+1<str.length && NumComparator.isFromString(str[head+1])) {
			  NumEvaluate t1 = new NumEvaluate(0);
			  head = t1.eatValue(str,head);
			  NumComparator t2 = NumComparator.fromString(str[head]);
			  NumEvaluate t3 = new NumEvaluate(0);
			  head = t3.eatValue(str,head+1);
			  boolList.add(new BoolEvaluate(t1,t2,t3));
			  return head;
		}
		if (NumEvaluate.isAVariable(str[head])) {
			boolList.add(new BoolEvaluate(str[head]));
			return head+1;
		}
		System.out.println("ERROR");
		return 0;//trows "unexpected value: " + str[head];
	}
	@Override
	public String toString() {
		if (boolList.isEmpty()) {
			return "";//trows "Boolean Expression can't be empty";
		}
		String ret = boolList.get(0).toString()+" ";
		for(int i=1; i<boolList.size();i++) {
			ret += boolOperators.get(i-1).toString() +" "+ boolList.get(i).toString()+" ";
		}
		return ret;
	}
	boolean eval(HashMap<String, UserVariable> variableNameList) {
		if (boolList.isEmpty()) {
			return false;//trows "Boolean Expression can't be empty";
		}
		boolean ret=boolList.get(0).eval(variableNameList);
		for(int i=1; i<boolList.size();i++) {
			switch (boolOperators.get(i-1)) {
			case AND:
				ret = ret && boolList.get(i).eval(variableNameList);
				break;
			case OR:
				ret = ret || boolList.get(i).eval(variableNameList);
				break;
			case DIF:
				ret = ret != boolList.get(i).eval(variableNameList);
				break;
			default:
				throw new IllegalArgumentException("Unexpected value: " + boolOperators.get(i-1));
			}
		}
		return ret;
	}
}
