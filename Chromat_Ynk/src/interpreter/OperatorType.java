package interpreter;

//
public enum OperatorType {
	BoolOperator,NumComparator;
	//return the Operator with 1 more Priority than this : + < *
	//return null if they are no other Operator
	public OperatorType nextOperatorType() {
		if(this==BoolOperator) {
			return NumComparator;
		}
		if(this==NumComparator) {
			return null;
		}
		System.out.print("Error in OperatorType");
		return null;
	}
	public VariableType nextVariableType() {
		if(this==BoolOperator) {
			return VariableType.BOOL;
		}
		if(this==NumComparator) {
			return VariableType.NUM;
		}
		System.out.print("Error in OperatorType");
		return null;
	}
		
}
