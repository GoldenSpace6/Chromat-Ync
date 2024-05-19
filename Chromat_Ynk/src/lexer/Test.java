package lexer;


public class Test {
	public static void main(String[] args) {
		Instruction[] ii= {new Instruction(Command.FWD,new String[]{"5"}),
				new Instruction(Command.MOV,new String[]{"10", "15"}),
				new Instruction(Command.WHILE,new String[]{"A", "==", "B"}),
				new Instruction(Command.COLOR,new String[]{"255", "255", "0"}),
				new Instruction(Command.END,new String[]{})};
		Parser main= new Parser(ii);
		main.parserRec();
		System.out.println(main.startInstruction);
		//System.out.println(main.startInstruction.getNextInstruction().getNextInstruction().getConditionInstruction().getNextInstruction());
	}
}
