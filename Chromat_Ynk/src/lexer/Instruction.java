package lexer;

public class Instruction {
	private Command command;
	private String[] args;
	public Instruction(Command command,String[] args) {
		this.command=command;
		this.args=args;
	}
	public String[] getArgs() {
		return args;
	}
	public Command getCommand() {
		return command;
	}
}
