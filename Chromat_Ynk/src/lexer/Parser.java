package lexer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;


public class Parser {
	private Instruction[] instructions;
	public void Lexer (File file) throws IOException {
		ArrayList<Instruction> instructionList = new ArrayList<Instruction>();
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line = reader.readLine();
			while (line!=null) {
				String[] words = line.split(" ");
				instructionList.add(toInstruction(words));
				line = reader.readLine();
			}
			instructions = new Instruction[instructionList.size()];
			instructions = instructionList.toArray(instructions);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public Instruction toInstruction (String[] words) {
		System.out.print(words[0]);
		Instruction instruction = new Instruction(Command.valueOf(words[0]), Arrays.copyOfRange(words,1,words.length));
		return instruction;
	}
	
	

	//this.instructions = [["F",0,1]];
	private int ci;//currentInsruction
	InstructionNode startInstruction;
	InstructionNode prevConditionInstruction;
	ArrayList<InstructionNode> prevList = new ArrayList<>(); //listdestrucquichercheapointersurlaprochaineinstruction en next 
	
	public Parser(File file) { //temporary arguments
		try {
			Lexer(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		ci = 0;
		startInstruction= null;
		prevConditionInstruction = null;
	}
	
	
	public void parserRec() {
		//return graph starting at instructions[ci]
		if (ci>=instructions.length ||  instructions[ci].getCommand() == Command.END) {
			ci++;
			return;
		}
	
		//ret is the first instruction
		InstructionNode ret = new InstructionNode(instructions[ci].getCommand(), instructions[ci].getArgs());
		if (startInstruction == null) {
			startInstruction = ret;
		}
		//connect every unconnected instruction to this instruction ------
		if(this.prevConditionInstruction != null) {
			this.prevConditionInstruction.setConditionInstruction(ret);
			this.prevConditionInstruction = null;
		}
		for(InstructionNode j : this.prevList) {
			j.setNextInstruction(ret);
		}
		this.prevList.clear();
		//------
		
		if (instructions[ci].getCommand().isInstructionBlock()==false ) {// != {FOR,WHILE}
			ret.setConditionInstruction(null);
			ci++;
			//save the last instruction in prevList to know where to go afterward
			this.prevList.add(ret);
			
			//generate tree starting on the line after
			parserRec();
			return;
		} else {
			if (instructions[ci+1].getCommand() == Command.END) {
				//trow "cannot create empty instruction blocks";
				return;
			}
			if(instructions[ci].getCommand() != Command.FOR)  {
				Command tempcommand = instructions[ci].getCommand();


				ret.setArgs(instructions[ci].getArgs());
				ret.setCommand(instructions[ci].getCommand());
				ci++;
				this.prevConditionInstruction = ret;
				parserRec();
				//all inside instruction must link back to "while"
				if(tempcommand != Command.IF) { 
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
	public InstructionNode getStartInstruction() {
		return startInstruction;
	}
}
