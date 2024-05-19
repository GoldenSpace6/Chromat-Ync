package lexer;

public class InstructionNode {
	private Command command;
	private String[] args;
	private Instruction nextInstruction;
	private Instruction conditionInstruction;
	
	public InstructionNode() {
		this.nextInstruction = null;
		this.conditionInstruction = null;
		this.command = null;
		this.args = null;
	}
	Command getCommand() {
		return command;
	}
	void setCommand(Command command) {
		this.command=command;
	}
	public Instruction getConditionInstruction() {
		return conditionInstruction;
	}
	public void setConditionInstruction(Instruction conditionInstruction) {
		this.conditionInstruction = conditionInstruction;
	}
	public Instruction getNextInstruction() {
		return nextInstruction;
	}
	public void setNextInstruction(Instruction nextInstruction) {
		this.nextInstruction = nextInstruction;
	}
}
