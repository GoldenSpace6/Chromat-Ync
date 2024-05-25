package interpreter;

//
public enum OperatorType {
	BoolOperator,NumComparator,NumAdder,NumMultiplier;
	//return the Operator with 1 more Priority than this : + < *
	//return null if they are no other Operator
	public OperatorType nextOperatorType() {
		switch (this) {
		case BoolOperator: {return NumComparator;}
		case NumComparator: {return NumAdder;}
		case NumAdder: {return NumMultiplier;}
		case NumMultiplier:
			return null;
		default:
			System.out.print("Error in OperatorType");
			return null;
		}
	}
	public VariableType nextVariableType() {
		switch (this) {
		case BoolOperator: {return VariableType.BOOL;}
		case NumComparator:
		case NumAdder:
		case NumMultiplier: {return VariableType.NUM;}
		default:
			System.out.print("Error in OperatorType");
			return null;
		}
	}
		
}
