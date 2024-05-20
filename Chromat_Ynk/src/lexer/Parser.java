package lexer;

import java.util.ArrayList;

public class Parser {
	private Instruction[] instructions;
	public void Lexer (String[] path) {
		/**Lire le fichier
		 * 
		 * mettre la liste d'instructions dans instructions
		 * **/
	}
	
	

	//this.instructions = [["F",0,1]];
	private int ci;//currentInsruction
	InstructionNode startInstruction;
	InstructionNode prevConditionInstruction;
	ArrayList<InstructionNode> prevList = new ArrayList<>(); //listdestrucquichercheapointersurlaprochaineinstruction en next 
	
	public Parser(Instruction[] i) { //temporary arguments
		this.instructions=i;
		ci = 0;
		startInstruction= null;
		prevConditionInstruction = null;
	}
	
	
	void parserRec() {
		System.out.print(ci);
		//return tree starting at file[ci]
		if (ci>=instructions.length ||  instructions[ci].getCommand() == Command.END) {
			ci++;
			return;
		}
	
		//ret is the first instruction
		InstructionNode ret = new InstructionNode(instructions[ci].getCommand(), instructions[ci].getArgs());
		if (startInstruction == null) {
			startInstruction = ret;
		}
		//connect every unconnected instruction to this instruction
		if(this.prevConditionInstruction != null) {
			this.prevConditionInstruction.setConditionInstruction(ret);
			this.prevConditionInstruction = null;
		}
		for(InstructionNode j : this.prevList) {
			j.setNextInstruction(ret);
		}
		this.prevList.clear();
		
		
		if (instructions[ci].getCommand() != Command.IF && instructions[ci].getCommand() != Command.FOR && instructions[ci].getCommand() != Command.WHILE) {// != {FOR,WHILE}
			ret.setConditionInstruction(null);
			ci++;
			//save the last instruction in prevList to know where to go afterward
			this.prevList.add(ret);
			
			//generate tree starting on the line after
			parserRec();
			return;
		} else {
			if (instructions[ci+1].getCommand() == Command.END) {
				 //trows "cannot create empty instruction blocks";
				return;
			}
			if(instructions[ci].getCommand() != Command.FOR)  {
				Command tempcommand = instructions[ci].getCommand();

				BoolExpression condition = new BoolExpression(instructions[ci].getArgs());
				condition.lexer();
				ret.setCondition(condition);
				ret.setArgs(null);//ret.setArgs(magic(instructions[ci].getArgs()));
				ret.setCommand(Command.IF);
				ci++;
				this.prevConditionInstruction = ret;
				parserRec();
				//all inside instruction must link back to "while"
				if(tempcommand == Command.WHILE) { 
					for(InstructionNode j : this.prevList) {
						j.setNextInstruction(ret);
					}
					this.prevList.clear();
				}
	
				//ret and the last instruction(s) must link to the same next instruction
				this.prevList.add(ret);
				parserRec();
				return;
			}
			/*if(instructions[ci].command == "FOR") {
				//turn for loop into while loop
				//create variable
				ret.command = "NUM";
				ret.args = [instructions[ci].args[0],instructions[ci].args[1]];
				ret.nextInstruction = InstructionNode();
	
				//create while
				ret = ret.nextInstruction;
				ret.command = "IF";
				ret.args = magic(instructions[ci].args[0]+"<"+instructions[ci].args[2]);
				temp = instructions[ci].args;
				
				ci++;
				this.prevConditionInstruction = ret;
				parserRec();
	
				//create incrementation;
				adder = InstructionNode();
				adder.command = "AFFECT";
				adder.args = magic2(temp[0]+"="+temp[0]+"+"+temp[3]);
	
				//all inside instruction must link back to "while"
				for(InstructionNode j:this.prevList) {
					j.nextInstruction = adder;
				}
				adder.nextInstruction = ret
				rm = indentaion();
				rm.command = "DEL";
				rm.args = temp[0];
				ret.nextInstruction = rm;
				
				//
				this.prevList = [rm];
				parserRec();
				return 0;
			}*/
		}
	}
}
