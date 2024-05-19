package lexer;

import java.util.Arrays;

public class InstructionNode {
	private Command command;
	private String[] args;
	private InstructionNode nextInstruction;
	private InstructionNode conditionInstruction;
	private Boolean hasReturnToString=false;
	
	public InstructionNode(Command command, String[] args) {
		this.nextInstruction = null;
		this.conditionInstruction = null;
		this.command = command;
		this.args = args;
	}
	Command getCommand() {
		return command;
	}
	void setCommand(Command command) {
		this.command=command;
	}
	public String[] getArgs() {
		return args;
	}
	public void setArgs(String[] args) {
		this.args = args;
	}
	public InstructionNode getConditionInstruction() {
		return conditionInstruction;
	}
	public void setConditionInstruction(InstructionNode conditionInstruction) {
		this.conditionInstruction = conditionInstruction;
	}
	public InstructionNode getNextInstruction() {
		return nextInstruction;
	}
	public void setNextInstruction(InstructionNode nextInstruction) {
		this.nextInstruction = nextInstruction;
	}
	@Override
	public String toString() {
		String temp = this.command.toString()+Arrays.toString(this.args);
		if(this.conditionInstruction!=null) {
			if(this.hasReturnToString==true) {
				return "END";
			}
			this.hasReturnToString=true;
			temp=temp+this.conditionInstruction.toString();
			this.hasReturnToString=false;
		}
		if(this.nextInstruction==null) {
			return temp+";";
		}
		return temp+", "+this.nextInstruction.toString();
	}
}
